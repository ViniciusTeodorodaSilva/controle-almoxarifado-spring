package br.com.almoxarifado.security;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.*;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import br.com.almoxarifado.model.*;
import br.com.almoxarifado.repository.*;
import br.com.almoxarifado.service.EstoqueService;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.Mockito.*;

@SpringBootTest(properties={"spring.datasource.url=jdbc:h2:mem:bes-security2;MODE=MySQL;DB_CLOSE_DELAY=-1", "bes.cors.origins=https://bes-autorizada.example"})
@AutoConfigureMockMvc @ActiveProfiles("test")
class SecurityBaselineTests {
 // Fictitious credential used only in isolated H2 tests. Never production configuration.
 static final String TEST_PASSWORD="H2-only-security-passphrase!";
 static final String FIXTURE_HASH=new BCryptPasswordEncoder(4).encode(TEST_PASSWORD);
 @Autowired LoginRateLimiter limiter; @Autowired org.springframework.transaction.PlatformTransactionManager transactions;
 @Autowired MockMvc mvc; @Autowired UsuarioRepository usuarios; @MockitoSpyBean AuditoriaRepository audit;
 @Autowired PasswordEncoder encoder; @Autowired AuditoriaService auditoria; @Autowired EstoqueService estoqueService;
 @Autowired ProdutoRepository produtos; @Autowired FuncionarioRepository pessoas; @Autowired AlmoxarifadoRepository locais;
 @Autowired EstoqueRepository estoques; @Autowired MovimentacaoRepository movimentos;
 @Autowired tools.jackson.databind.ObjectMapper json;
 @Autowired org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping mappings;
 @Autowired br.com.almoxarifado.service.TransferenciaEstoqueService transferir;
 @Autowired br.com.almoxarifado.service.AtendimentoSolicitacaoService atender;
 @Autowired br.com.almoxarifado.service.SolicitacaoService solicitar;
 @Autowired br.com.almoxarifado.service.EstoqueInteligenteService limites;
 @Autowired br.com.almoxarifado.service.NecessidadeCompraService necessidades;
 @Autowired UsuarioService gerir;
 @Autowired TransferenciaEstoqueRepository transferencias;
 @Autowired AtendimentoSolicitacaoRepository atendimentos;
 @BeforeEach void setup(){((java.util.Map<?,?>)org.springframework.test.util.ReflectionTestUtils.getField(limiter,"buckets")).clear();SecurityContextHolder.clearContext();audit.deleteAll();usuarios.deleteAll();for(var perfil:Perfil.values())usuarios.saveAndFlush(new Usuario(perfil.name().toLowerCase(),FIXTURE_HASH,"H2 "+perfil,perfil));}
 @AfterEach void limpar(){SecurityContextHolder.clearContext();reset(audit);}
 record Sessao(MockHttpSession session,String token){}
 Sessao token(MockHttpSession session)throws Exception {var result=mvc.perform(session==null?get("/auth/csrf"):get("/auth/csrf").session(session)).andExpect(status().isOk()).andReturn();return new Sessao((MockHttpSession)result.getRequest().getSession(),json.readTree(result.getResponse().getContentAsString()).get("token").asText());}
 Sessao login(Perfil perfil)throws Exception {var anon=token(null);mvc.perform(post("/auth/login").session(anon.session()).header("X-CSRF-TOKEN",anon.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("username",perfil.name().toLowerCase(),"password",TEST_PASSWORD)))).andExpect(status().isOk());return token(anon.session());}
 void identidade(Sessao s){SecurityContextHolder.setContext((org.springframework.security.core.context.SecurityContext)s.session().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY));}
 @Test void loginValidoEMeSemSenha()throws Exception {var s=login(Perfil.ADMIN);var r=mvc.perform(get("/auth/me").session(s.session())).andExpect(status().isOk()).andExpect(jsonPath("$.username").value("admin")).andExpect(jsonPath("$.perfil").value("ADMIN")).andExpect(jsonPath("$.senhaHash").doesNotExist()).andExpect(jsonPath("$.password").doesNotExist()).andReturn();assertFalse(r.getResponse().getContentAsString().contains(TEST_PASSWORD));assertNotNull(usuarios.findByUsername("admin").orElseThrow().getUltimoLoginEm());}
 @Test void errosLoginGenericosSemEnumerar()throws Exception {String first=null;for(String name:List.of("admin","ausente")){var s=token(null);var r=mvc.perform(post("/auth/login").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("username",name,"password","H2-wrong-passphrase!")))).andExpect(status().isUnauthorized()).andReturn();String message=json.readTree(r.getResponse().getContentAsString()).get("mensagem").asText();if(first==null)first=message;else assertEquals(first,message);assertFalse(r.getResponse().getContentAsString().contains(name));}}
 @Test void usuarioInativoNaoAutentica()throws Exception {var u=usuarios.findByUsername("consulta").orElseThrow();u.alterar(u.getUsername(),u.getNomeExibicao(),u.getPerfil(),false,null);usuarios.saveAndFlush(u);var s=token(null);mvc.perform(post("/auth/login").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("username","consulta","password",TEST_PASSWORD)))).andExpect(status().isUnauthorized());}
 @Test void csrfObrigatorioNoLoginENaEscrita()throws Exception {mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());var s=login(Perfil.ADMIN);mvc.perform(post("/produtos").session(s.session()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());}
 @Test void csrfRenovadoEIdSessaoRotacionado()throws Exception {var s=token(null);String antigo=s.session().getId();mvc.perform(post("/auth/login").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("username","admin","password",TEST_PASSWORD)))).andExpect(status().isOk());assertNotEquals(antigo,s.session().getId());mvc.perform(post("/produtos").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());}
 @Test void logoutInvalidaSessao()throws Exception {var s=login(Perfil.ADMIN);mvc.perform(post("/auth/logout").session(s.session()).header("X-CSRF-TOKEN",s.token())).andExpect(status().isNoContent());assertTrue(s.session().isInvalid());mvc.perform(get("/auth/me")).andExpect(status().isUnauthorized());}
 @Test void sessaoInexistenteNaoAutentica()throws Exception {mvc.perform(get("/produtos").session(new MockHttpSession())).andExpect(status().isUnauthorized());}
 @Test void sessaoExpiradaAbsolutaNaoAutentica()throws Exception {var s=login(Perfil.ADMIN);var ctx=(org.springframework.security.core.context.SecurityContext)s.session().getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);var a=ctx.getAuthentication();var i=(Identidade)a.getPrincipal();ctx.setAuthentication(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated(new Identidade(i.id(),i.username(),i.authVersion(),0),null,a.getAuthorities()));mvc.perform(get("/produtos").session(s.session())).andExpect(status().isUnauthorized());}
 @Test void bootstrapDesabilitadoNaoCria() {usuarios.deleteAll();new BootstrapAdmin(usuarios,encoder,auditoria,false,"","").run(null);assertEquals(0,usuarios.count());}
 @Test void bootstrapNaoSobrescreve(){var u=usuarios.findByUsername("admin").orElseThrow();String hash=u.getSenhaHash();new BootstrapAdmin(usuarios,encoder,auditoria,true,"outro","H2-only-new-passphrase!").run(null);assertEquals(4,usuarios.count());assertEquals(hash,usuarios.findByUsername("admin").orElseThrow().getSenhaHash());}
 @Test void bootstrapValidaSenha(){usuarios.deleteAll();assertThrows(IllegalArgumentException.class,()->new BootstrapAdmin(usuarios,encoder,auditoria,true,"teste","curta").run(null));assertEquals(0,usuarios.count());}
 @Test void politicaComprimentoEBytes(){assertThrows(IllegalArgumentException.class,()->PoliticaSenha.validar("curta"));assertThrows(IllegalArgumentException.class,()->PoliticaSenha.validar("a".repeat(73)));assertThrows(IllegalArgumentException.class,()->PoliticaSenha.validar("á".repeat(37)));assertDoesNotThrow(()->PoliticaSenha.validar("a".repeat(72)));assertDoesNotThrow(()->PoliticaSenha.validar("passphrase longa sem regras artificiais"));}
 @Test void loginNormalizadoUnico(){assertEquals("joao.silva",PoliticaSenha.login(" Joao.Silva "));assertThrows(IllegalArgumentException.class,()->PoliticaSenha.login("<malicioso>"));}
 @ParameterizedTest @EnumSource(Perfil.class) void matrizLeitura(Perfil perfil)throws Exception {var s=login(perfil);for(String path:List.of("/produtos","/categorias","/unidades-medida","/estoques","/movimentacoes","/transferencias","/solicitacoes","/necessidades-compra","/funcionarios","/almoxarifados"))mvc.perform(get(path).session(s.session())).andExpect(status().isOk());}
 @ParameterizedTest @EnumSource(Perfil.class) void matrizEscrita(Perfil perfil)throws Exception {var s=login(perfil);Map<String,Permissao> routes=new LinkedHashMap<>();routes.put("/produtos",Permissao.PRODUTO_GERENCIAR);routes.put("/categorias",Permissao.CATEGORIA_GERENCIAR);routes.put("/unidades-medida",Permissao.UNIDADE_GERENCIAR);routes.put("/estoques",Permissao.ESTOQUE_MOVIMENTAR);routes.put("/transferencias",Permissao.ESTOQUE_TRANSFERIR);routes.put("/solicitacoes",Permissao.SOLICITACAO_CRIAR);routes.put("/necessidades-compra",Permissao.NECESSIDADE_COMPRA_CRIAR);routes.put("/funcionarios",Permissao.FUNCIONARIO_GERENCIAR);routes.put("/almoxarifados",Permissao.ALMOXARIFADO_GERENCIAR);
  for(var entry:routes.entrySet()){var req=post(entry.getKey()).session(s.session()).header("X-CSRF-TOKEN",s.token()).header("Idempotency-Key",UUID.randomUUID().toString()).param("solicitanteId","999999").param("almoxarifadoId","999999").contentType(MediaType.APPLICATION_JSON).content("{}");int actual=mvc.perform(req).andReturn().getResponse().getStatus();if(perfil.permissoes().contains(entry.getValue()))assertTrue(actual==400||actual==404,"Permitted route must reach validation: "+entry.getKey()+" status="+actual);else assertEquals(403,actual,entry.getKey());}
 }
 @ParameterizedTest @EnumSource(Perfil.class) void matrizOperacoesCriticas(Perfil perfil)throws Exception {var s=login(perfil);Map<String,Permissao> routes=Map.of("/estoques/entrada",Permissao.ESTOQUE_MOVIMENTAR,"/estoques/saida",Permissao.ESTOQUE_MOVIMENTAR,"/estoques/999999/limites",Permissao.ESTOQUE_CONFIGURAR,"/solicitacoes/999999/aprovar",Permissao.SOLICITACAO_APROVAR,"/solicitacoes/999999/rejeitar",Permissao.SOLICITACAO_REJEITAR,"/solicitacoes/999999/iniciar-separacao",Permissao.SOLICITACAO_SEPARAR);
  for(var e:routes.entrySet()){var req=put(e.getKey()).session(s.session()).header("X-CSRF-TOKEN",s.token()).param("produtoId","999999").param("almoxarifadoId","999999").param("quantidade","1").param("solicitanteId","999999").param("responsavelId","999999").contentType(MediaType.APPLICATION_JSON).content("{}");int actual=mvc.perform(req).andReturn().getResponse().getStatus();if(perfil.permissoes().contains(e.getValue()))assertTrue(actual==400||actual==404);else assertEquals(403,actual,e.getKey());}
  int atendimento=mvc.perform(post("/solicitacoes/999999/atendimentos").session(s.session()).header("X-CSRF-TOKEN",s.token()).header("Idempotency-Key",UUID.randomUUID().toString()).contentType(MediaType.APPLICATION_JSON).content("{\"responsavelId\":999999,\"itens\":[{\"itemSolicitacaoId\":999999,\"quantidade\":1}]}")).andReturn().getResponse().getStatus();assertEquals(perfil.permissoes().contains(Permissao.SOLICITACAO_ATENDER)?404:403,atendimento);
 }
 @Test void endpointsSemIdentidade401()throws Exception {for(String path:List.of("/produtos","/estoques","/solicitacoes","/usuarios","/auditoria"))mvc.perform(get(path)).andExpect(status().isUnauthorized());mvc.perform(post("/produtos").with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());}
 @ParameterizedTest @EnumSource(Perfil.class) void administracaoLeastPrivilege(Perfil perfil)throws Exception {var s=login(perfil);mvc.perform(get("/usuarios").session(s.session())).andExpect(status().is(perfil==Perfil.ADMIN?200:403));mvc.perform(get("/auditoria").session(s.session())).andExpect(status().is(perfil==Perfil.ADMIN||perfil==Perfil.GESTOR?200:403));}
 @Test void hashPersistidoENuncaRetornado()throws Exception {var s=login(Perfil.ADMIN);var body=Map.of("username","Novo.Login","nomeExibicao","Usuario H2","perfil","CONSULTA","ativo",true,"password",TEST_PASSWORD);var result=mvc.perform(post("/usuarios").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isOk()).andExpect(jsonPath("$.senhaHash").doesNotExist()).andReturn();assertFalse(result.getResponse().getContentAsString().contains(TEST_PASSWORD));var u=usuarios.findByUsername("novo.login").orElseThrow();assertNotEquals(TEST_PASSWORD,u.getSenhaHash());assertTrue(encoder.matches(TEST_PASSWORD,u.getSenhaHash()));assertTrue(u.getSenhaHash().startsWith("$2a$12$")||u.getSenhaHash().startsWith("$2b$12$"));}
 @Test void massAssignmentRejeitado()throws Exception {var s=login(Perfil.ADMIN);for(String campo:List.of("id","senhaHash","criadoEm","atualizadoEm","ultimoLoginEm","authVersion","atorId","permissoes")){var body=new HashMap<String,Object>(Map.of("username","tentativa","nomeExibicao","H2","perfil","CONSULTA","ativo",true,"password",TEST_PASSWORD));body.put(campo,"forjado");mvc.perform(post("/usuarios").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isBadRequest());}assertFalse(usuarios.findByUsername("tentativa").isPresent());}
 @Test void consultaNaoElevaPerfilNemForjaId()throws Exception {var s=login(Perfil.CONSULTA);Long id=usuarios.findByUsername("consulta").orElseThrow().getId();mvc.perform(put("/usuarios/"+id).session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"consulta\",\"nomeExibicao\":\"H2\",\"perfil\":\"ADMIN\",\"ativo\":true}")).andExpect(status().isForbidden());assertEquals(Perfil.CONSULTA,usuarios.findById(id).orElseThrow().getPerfil());}
 @Test void bypassServiceNegado()throws Exception {var s=login(Perfil.CONSULTA);identidade(s);assertThrows(AccessDeniedException.class,()->estoqueService.entradaEstoque(1,1,1,1,1));}
 @Test void resetRevogaSessao()throws Exception {var user=login(Perfil.CONSULTA);var admin=login(Perfil.ADMIN);Long id=usuarios.findByUsername("consulta").orElseThrow().getId();mvc.perform(put("/usuarios/"+id+"/senha").session(admin.session()).header("X-CSRF-TOKEN",admin.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("password","H2-new-reset-passphrase!")))).andExpect(status().isOk());mvc.perform(get("/produtos").session(user.session())).andExpect(status().isUnauthorized());}
 @Test void inativacaoRevogaSessao()throws Exception {revogar(false,Perfil.CONSULTA);}
 @Test void alteracaoPerfilRevogaSessao()throws Exception {revogar(true,Perfil.GESTOR);}
 void revogar(boolean ativo,Perfil perfil)throws Exception {var user=login(Perfil.CONSULTA);var admin=login(Perfil.ADMIN);Long id=usuarios.findByUsername("consulta").orElseThrow().getId();mvc.perform(put("/usuarios/"+id).session(admin.session()).header("X-CSRF-TOKEN",admin.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(Map.of("username","consulta","nomeExibicao","H2","perfil",perfil.name(),"ativo",ativo)))).andExpect(status().isOk());mvc.perform(get("/produtos").session(user.session())).andExpect(status().isUnauthorized());}
 @Test void ultimoAdminNaoPodeSerDesativado()throws Exception {var s=login(Perfil.ADMIN);Long id=usuarios.findByUsername("admin").orElseThrow().getId();mvc.perform(put("/usuarios/"+id).session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"admin\",\"nomeExibicao\":\"H2\",\"perfil\":\"CONSULTA\",\"ativo\":false}")).andExpect(status().isConflict());}
 @Test void auditoriaLoginFalhaSemSegredo()throws Exception {login(Perfil.ADMIN);assertTrue(audit.findAll().stream().anyMatch(e->e.getEvento().equals("LOGIN")&&e.getResultado().equals("SUCESSO")&&e.getAtorId()!=null));var s=token(null);mvc.perform(post("/auth/login").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"ausente\",\"password\":\"ficticio-invalido\"}")).andExpect(status().isUnauthorized());assertTrue(audit.findAll().stream().anyMatch(e->e.getResultado().equals("FALHA")&&e.getAtorId()==null));assertFalse(json.writeValueAsString(audit.findAll()).contains(TEST_PASSWORD));}
 @Test void auditoriaNegacaoImutavelEIdentidadeCorreta()throws Exception {var s=login(Perfil.CONSULTA);mvc.perform(post("/produtos").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());assertTrue(audit.findAll().stream().anyMatch(e->e.getEvento().equals("ACESSO_NEGADO")&&Objects.equals(e.getAtorId(),usuarios.findByUsername("consulta").orElseThrow().getId())));mvc.perform(put("/auditoria/1").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());}
 record Material(Produto produto,Funcionario pessoa,Almoxarifado local,Estoque estoque){}
 Material material(){var p=new Produto();p.setNome("H2 auditoria");p.setCodigo(UUID.randomUUID().toString());produtos.saveAndFlush(p);var f=new Funcionario();f.setNome("Responsavel operacional H2");f.setMatricula(UUID.randomUUID().toString());pessoas.saveAndFlush(f);var a=new Almoxarifado();a.setNome("H2 auditoria");locais.saveAndFlush(a);var e=new Estoque();e.setProduto(p);e.setAlmoxarifado(a);e.setQuantidade(0);estoques.saveAndFlush(e);return new Material(p,f,a,e);}
 @Test void auditoriaEstoqueAtorDiferenteDoResponsavelESaldos()throws Exception {var m=material();var s=login(Perfil.ADMIN);String correlation=UUID.randomUUID().toString();mvc.perform(put("/estoques/entrada").session(s.session()).header("X-CSRF-TOKEN",s.token()).header("X-Request-ID",correlation).param("produtoId",m.produto().getId().toString()).param("almoxarifadoId",m.local().getId().toString()).param("quantidade","2").param("solicitanteId",m.pessoa().getId().toString()).param("responsavelId",m.pessoa().getId().toString())).andExpect(status().isOk());var e=audit.findAll().stream().filter(x->x.getEvento().equals("ESTOQUE_ENTRADAESTOQUE")).reduce((x,y)->y).orElseThrow();assertEquals(usuarios.findByUsername("admin").orElseThrow().getId(),e.getAtorId());assertEquals(m.pessoa().getId(),e.getResponsavelOperacionalId());assertEquals(correlation,e.getRequestId());assertNotNull(e.getInstante());assertEquals("Movimentacao",e.getEntidade());assertEquals("quantidade=0.0",e.getAntes());assertEquals("quantidade=2.0",e.getDepois());assertNotNull(e.getReferencia());}
 @Test void falhaAuditoriaReverteEstoqueEMovimento()throws Exception {var m=material();var s=login(Perfil.ADMIN);long count=movimentos.count();doThrow(new IllegalStateException("Falha de auditoria simulada")).when(audit).save(argThat(e->e!=null&&e.getEvento().equals("ESTOQUE_ENTRADAESTOQUE")));mvc.perform(put("/estoques/entrada").session(s.session()).header("X-CSRF-TOKEN",s.token()).param("produtoId",m.produto().getId().toString()).param("almoxarifadoId",m.local().getId().toString()).param("quantidade","2").param("solicitanteId",m.pessoa().getId().toString()).param("responsavelId",m.pessoa().getId().toString())).andExpect(status().isInternalServerError());assertEquals(0,estoques.findById(m.estoque().getId()).orElseThrow().getQuantidade());assertEquals(count,movimentos.count());}
 @Test void headersECorrelationLimitado()throws Exception {mvc.perform(get("/auth/csrf").header("X-Request-ID","malicioso".repeat(200))).andExpect(status().isOk()).andExpect(header().string("X-Content-Type-Options","nosniff")).andExpect(header().string("X-Frame-Options","DENY")).andExpect(header().string("Referrer-Policy","no-referrer")).andExpect(header().string("X-Request-ID",org.hamcrest.Matchers.matchesPattern("[a-f0-9-]{36}")));}
 @Test void corsNaoLiberaOrigemArbitraria()throws Exception {mvc.perform(options("/produtos").header("Origin","https://origem-nao-autorizada.example").header("Access-Control-Request-Method","POST")).andExpect(status().isForbidden()).andExpect(header().doesNotExist("Access-Control-Allow-Origin"));}
 @Test void auditoriaPaginadaComLimite()throws Exception {var s=login(Perfil.ADMIN);mvc.perform(get("/auditoria").session(s.session()).param("tamanho","101")).andExpect(status().isBadRequest());mvc.perform(get("/auditoria").session(s.session()).param("tamanho","5")).andExpect(status().isOk()).andExpect(jsonPath("$.size").value(5));}

 @Test void bootstrapSeguroCriaSomentePrimeiroAdmin(){usuarios.deleteAll();new org.springframework.transaction.support.TransactionTemplate(transactions).executeWithoutResult(status->new BootstrapAdmin(usuarios,encoder,auditoria,true,"h2.first",TEST_PASSWORD).run(null));assertEquals(1,usuarios.count());var u=usuarios.findByUsername("h2.first").orElseThrow();assertEquals(Perfil.ADMIN,u.getPerfil());assertTrue(encoder.matches(TEST_PASSWORD,u.getSenhaHash()));assertTrue(audit.findAll().stream().anyMatch(e->e.getEvento().equals("ADMIN_BOOTSTRAP")));}
 @Test void rateLimitHttpBloqueiaTentativasRepetidas()throws Exception {for(int n=0;n<9;n++){var s=token(null);mvc.perform(post("/auth/login").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{\"username\":\"h2.rate\",\"password\":\"H2-invalid-passphrase!\"}")).andExpect(status().is(n<8?401:429));}}
 @Test void limitesAuditamReferenciaEEstadosSeguros()throws Exception {var m=material();var s=login(Perfil.ADMIN);mvc.perform(put("/estoques/"+m.estoque().getId()+"/limites").session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{\"estoqueMinimo\":2,\"estoqueMaximo\":9}")).andExpect(status().isOk());var e=audit.findAll().stream().filter(v->v.getEvento().equals("ESTOQUEINTELIGENTE_CONFIGURAR")).findFirst().orElseThrow();assertEquals(m.estoque().getId().toString(),e.getReferencia());assertNotNull(e.getAntes());assertTrue(e.getDepois().contains("estoqueMinimo=2.0"));}

 @Test void todasEscritasRegistradasExigemCsrfETodosEndpointsOperacionaisExigemSessao()throws Exception {
  var s=login(Perfil.ADMIN);int total=0,escritas=0;
  for(var entry:mappings.getHandlerMethods().entrySet()) {
   if(!entry.getValue().getBeanType().getPackageName().startsWith("br.com.almoxarifado"))continue;
   for(String path:entry.getKey().getPatternValues())for(var method:entry.getKey().getMethodsCondition().getMethods()) {
    total++;String uri=path.replaceAll("\\{[^}]+}","999999");
    if(!Set.of("GET","HEAD","OPTIONS").contains(method.name())) {
     escritas++;
     for(String token:new String[]{"","forjado"}) {
      var req=request(org.springframework.http.HttpMethod.valueOf(method.name()),uri).session(s.session()).contentType(MediaType.APPLICATION_JSON).content("{}");
      if(!token.isEmpty())req.header("X-CSRF-TOKEN",token);
      mvc.perform(req).andExpect(status().isForbidden());
     }
    }
    if(!path.equals("/auth/csrf")&&!path.equals("/auth/login"))mvc.perform(request(org.springframework.http.HttpMethod.valueOf(method.name()),uri).with(csrf()).contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isUnauthorized());
   }
  }
  assertEquals(85,total);assertEquals(36,escritas);
  for(String method:List.of("PATCH","DELETE"))mvc.perform(request(org.springframework.http.HttpMethod.valueOf(method),"/usuarios/999999").session(s.session())).andExpect(status().isForbidden());
 }
 @Test void corsAutorizadoNaoDispensaCsrf()throws Exception {
  mvc.perform(options("/produtos").header("Origin","https://bes-autorizada.example").header("Access-Control-Request-Method","POST").header("Access-Control-Request-Headers","X-CSRF-TOKEN,Content-Type")).andExpect(status().isOk()).andExpect(header().string("Access-Control-Allow-Origin","https://bes-autorizada.example")).andExpect(header().string("Access-Control-Allow-Credentials","true"));
  var s=login(Perfil.ADMIN);mvc.perform(post("/produtos").session(s.session()).header("Origin","https://bes-autorizada.example").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
  mvc.perform(get("/produtos").session(s.session()).header("Origin","https://nao-autorizada.example")).andExpect(status().isForbidden()).andExpect(header().doesNotExist("Access-Control-Allow-Origin"));
  assertThrows(IllegalStateException.class,()->new SegurancaConfig().cors("https://*.example"));
 }
 @Test void headersCspEHstsEmitidosSomenteHttps()throws Exception {
  mvc.perform(get("/auth/csrf")).andExpect(header().string("Content-Security-Policy","default-src 'none'; frame-ancestors 'none'")).andExpect(header().doesNotExist("Strict-Transport-Security"));
  mvc.perform(get("/auth/csrf").secure(true)).andExpect(header().string("Strict-Transport-Security",org.hamcrest.Matchers.containsString("max-age=")));
 }
 @Test void bypassServicesCriticosNegadoAntesDeValidacao()throws Exception {
  identidade(login(Perfil.CONSULTA));
  List<org.junit.jupiter.api.function.Executable> ataques=List.of(
   ()->estoqueService.entradaEstoque(1,1,1,1,1),()->estoqueService.saidaEstoque(1,1,1,1,1),
   ()->transferir.criar(null),()->limites.configurar(1,null),()->solicitar.aprovar(1,1),()->solicitar.rejeitar(1),
   ()->atender.iniciarSeparacao(1,1),()->atender.atender(1,null,"forjado"),()->necessidades.criar(null,"forjado"),()->gerir.criar(null));
  for(var ataque:ataques)assertThrows(AccessDeniedException.class,ataque);
 }
 @Test void gestorNaoAtribuiAdminEAlmoxarifeNaoGereUsuarios()throws Exception {
  for(var perfil:List.of(Perfil.GESTOR,Perfil.ALMOXARIFE)) {
   var s=login(perfil);Long id=usuarios.findByUsername(perfil.name().toLowerCase()).orElseThrow().getId();
   for(Long alvo:List.of(id,usuarios.findByUsername("admin").orElseThrow().getId(),999999L))mvc.perform(put("/usuarios/"+alvo).session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content("{\"perfil\":\"ADMIN\",\"ativo\":true,\"roles\":[\"ADMIN\"],\"authorities\":[\"USUARIO_GERENCIAR\"]}")).andExpect(status().isForbidden());
   identidade(s);assertThrows(AccessDeniedException.class,()->gerir.senha(id,TEST_PASSWORD));
  }
 }
 @Test void camposInternosExtrasNaoAlteramUsuario()throws Exception {
  var s=login(Perfil.ADMIN);Long id=usuarios.findByUsername("consulta").orElseThrow().getId();long version=usuarios.findById(id).orElseThrow().getAuthVersion();
  for(String campo:List.of("roles","authorities","atorAuditoria","bootstrapChave","versao","senhaHash")) {
   var body=new HashMap<String,Object>(Map.of("username","consulta","nomeExibicao","H2","perfil","CONSULTA","ativo",true));body.put(campo,"forjado");
   mvc.perform(put("/usuarios/"+id).session(s.session()).header("X-CSRF-TOKEN",s.token()).contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isBadRequest());
  }
  assertEquals(version,usuarios.findById(id).orElseThrow().getAuthVersion());
  String entidade=json.writeValueAsString(usuarios.findById(id).orElseThrow());assertFalse(entidade.contains("senhaHash"));assertFalse(entidade.contains(FIXTURE_HASH));assertFalse(entidade.contains("bootstrapChave"));
 }
 @Test void ultimoAdminProtegidoMesmoComOutroOperador()throws Exception {
  var operador=usuarios.saveAndFlush(new Usuario("operador",FIXTURE_HASH,"H2",Perfil.ADMIN));var s=login(Perfil.ADMIN);
  operador.alterar("operador","H2",Perfil.GESTOR,true,null);usuarios.saveAndFlush(operador);identidade(s);
  // Authorized service caller other than the target: last-admin rule must stand independently of self protection.
  var ctx=SecurityContextHolder.getContext();var a=ctx.getAuthentication();ctx.setAuthentication(org.springframework.security.authentication.UsernamePasswordAuthenticationToken.authenticated(new Identidade(operador.getId(),"operador",operador.getAuthVersion(),System.currentTimeMillis()),null,a.getAuthorities()));
  var input=new EntradasSeguranca.UsuarioInput();input.username="admin";input.nomeExibicao="H2";input.ativo=true;input.perfil=Perfil.CONSULTA;
  assertThrows(br.com.almoxarifado.exception.ConflitoException.class,()->gerir.alterar(usuarios.findByUsername("admin").orElseThrow().getId(),input));
  input.ativo=false;input.perfil=Perfil.ADMIN;assertThrows(br.com.almoxarifado.exception.ConflitoException.class,()->gerir.alterar(usuarios.findByUsername("admin").orElseThrow().getId(),input));
 }
 @Test void bootstrapIdempotenteEConcorrenteComLoginsDiferentes()throws Exception {
  usuarios.deleteAll();var barrier=new java.util.concurrent.CyclicBarrier(2);var pool=java.util.concurrent.Executors.newFixedThreadPool(2);
  try {
   List<java.util.concurrent.Future<Boolean>> results=new ArrayList<>();
   for(String username:List.of("bootstrap.a","bootstrap.b"))results.add(pool.submit(()->{
    try {new org.springframework.transaction.support.TransactionTemplate(transactions).executeWithoutResult(status->{assertEquals(0,usuarios.count());try{barrier.await(10,java.util.concurrent.TimeUnit.SECONDS);}catch(Exception e){throw new IllegalStateException(e);}new BootstrapAdmin(usuarios,encoder,auditoria,true,username,TEST_PASSWORD).run(null);});return true;}
    catch(org.springframework.dao.DataIntegrityViolationException e){return false;}
   }));
   int success=0;for(var result:results)if(result.get(30,java.util.concurrent.TimeUnit.SECONDS))success++;
   assertEquals(1,success);assertEquals(1,usuarios.count());
   var u=usuarios.findAll().get(0);String hash=u.getSenhaHash();long eventos=audit.count();
   new org.springframework.transaction.support.TransactionTemplate(transactions).executeWithoutResult(status->new BootstrapAdmin(usuarios,encoder,auditoria,true,"bootstrap.c",TEST_PASSWORD).run(null));
   assertEquals(1,usuarios.count());assertEquals(hash,usuarios.findById(u.getId()).orElseThrow().getSenhaHash());assertEquals(eventos,audit.count());
  }finally{pool.shutdownNow();}
 }
 @Test void falhasOperacionaisNaoRegistramSucessoNemPersistemEstoque()throws Exception {
  var m=material();var s=login(Perfil.ADMIN);identidade(s);long movimentosAntes=movimentos.count();long auditAntes=audit.count();long transferenciasAntes=transferencias.count();long atendimentoAntes=atendimentos.count();
  assertThrows(IllegalArgumentException.class,()->estoqueService.saidaEstoque(m.produto().getId(),m.local().getId(),1,m.pessoa().getId(),m.pessoa().getId()));
  var destino=new Almoxarifado();destino.setNome("H2 destino");destino=locais.saveAndFlush(destino);
  var input=new br.com.almoxarifado.dto.TransferenciaInput(m.local().getId(),destino.getId(),m.pessoa().getId(),null,List.of(new br.com.almoxarifado.dto.TransferenciaInput.Item(m.produto().getId(),1.0)));
  assertThrows(IllegalArgumentException.class,()->transferir.criar(input));assertFalse(estoques.existsByProdutoIdAndAlmoxarifadoId(m.produto().getId(),destino.getId()));
  var sol=solicitar.cadastrar(m.pessoa().getId(),m.local().getId());var item=solicitar.adicionarItem(sol.getId(),m.produto().getId(),2);solicitar.aprovar(sol.getId(),m.pessoa().getId());atender.iniciarSeparacao(sol.getId(),m.pessoa().getId());long antesAtender=audit.count();
  assertThrows(IllegalArgumentException.class,()->atender.atender(sol.getId(),new br.com.almoxarifado.dto.AtendimentoInput(m.pessoa().getId(),List.of(new br.com.almoxarifado.dto.AtendimentoInput.Item(item.getId(),1.0))),UUID.randomUUID().toString()));
  assertEquals(antesAtender,audit.count());assertTrue(antesAtender>auditAntes);assertEquals(movimentosAntes,movimentos.count());assertEquals(transferenciasAntes,transferencias.count());assertEquals(atendimentoAntes,atendimentos.count());assertEquals(0,estoques.findById(m.estoque().getId()).orElseThrow().getQuantidade());
  assertFalse(audit.findAll().stream().anyMatch(e->Set.of("ESTOQUE_SAIDAESTOQUE","TRANSFERENCIAESTOQUE_CRIAR","ATENDIMENTOSOLICITACAO_ATENDER").contains(e.getEvento())));
 }
 @Test void erroInternoNaoExpoeExcecaoNoLogOuResposta() {
  var logger=(ch.qos.logback.classic.Logger)org.slf4j.LoggerFactory.getLogger(br.com.almoxarifado.controller.RegraNegocioExceptionHandler.class);var appender=new ch.qos.logback.core.read.ListAppender<ch.qos.logback.classic.spi.ILoggingEvent>();appender.start();logger.addAppender(appender);
  try{var req=new org.springframework.mock.web.MockHttpServletRequest();req.setRequestURI("/usuarios");var response=new br.com.almoxarifado.controller.RegraNegocioExceptionHandler(auditoria).tratarInesperado(new IllegalStateException("H2-sensitive-marker"),req);assertFalse(response.getBody().toString().contains("H2-sensitive-marker"));assertEquals(1,appender.list.size());assertNull(appender.list.get(0).getThrowableProxy());assertFalse(appender.list.get(0).getFormattedMessage().contains("H2-sensitive-marker"));}finally{logger.detachAppender(appender);appender.stop();}
 }
 @Test void falhaAuditoriaDepoisDeTransferenciaEAtendimentoReverteTudo()throws Exception {
  var m=material();var e=m.estoque();e.setQuantidade(5);estoques.saveAndFlush(e);identidade(login(Perfil.ADMIN));
  var destino=new Almoxarifado();destino.setNome("H2 rollback destino");destino=locais.saveAndFlush(destino);
  var input=new br.com.almoxarifado.dto.TransferenciaInput(m.local().getId(),destino.getId(),m.pessoa().getId(),null,List.of(new br.com.almoxarifado.dto.TransferenciaInput.Item(m.produto().getId(),1.0)));
  long txAntes=transferencias.count(),movAntes=movimentos.count(),auditAntes=audit.count();
  doThrow(new IllegalStateException("H2 audit failure")).when(audit).save(argThat(x->x!=null&&x.getEvento().equals("TRANSFERENCIAESTOQUE_CRIAR")));
  assertThrows(IllegalStateException.class,()->transferir.criar(input));assertEquals(txAntes,transferencias.count());assertEquals(movAntes,movimentos.count());assertEquals(auditAntes,audit.count());assertEquals(5,estoques.findById(e.getId()).orElseThrow().getQuantidade());assertFalse(estoques.existsByProdutoIdAndAlmoxarifadoId(m.produto().getId(),destino.getId()));reset(audit);
  var sol=solicitar.cadastrar(m.pessoa().getId(),m.local().getId());var item=solicitar.adicionarItem(sol.getId(),m.produto().getId(),2);solicitar.aprovar(sol.getId(),m.pessoa().getId());atender.iniciarSeparacao(sol.getId(),m.pessoa().getId());long atendimentoAntes=atendimentos.count();long auditAtender=audit.count();
  doThrow(new IllegalStateException("H2 audit failure")).when(audit).save(argThat(x->x!=null&&x.getEvento().equals("ATENDIMENTOSOLICITACAO_ATENDER")));
  assertThrows(IllegalStateException.class,()->atender.atender(sol.getId(),new br.com.almoxarifado.dto.AtendimentoInput(m.pessoa().getId(),List.of(new br.com.almoxarifado.dto.AtendimentoInput.Item(item.getId(),1.0))),UUID.randomUUID().toString()));assertEquals(atendimentoAntes,atendimentos.count());assertEquals(movAntes,movimentos.count());assertEquals(auditAtender,audit.count());assertEquals(5,estoques.findById(e.getId()).orElseThrow().getQuantidade());assertEquals("EM_SEPARACAO",atender.operacao(sol.getId()).status());assertEquals(0,atender.operacao(sol.getId()).itens().get(0).quantidadeAtendida());
 }
 @Test void getEHeadNaoModificamDadosOperacionais()throws Exception {
  var s=login(Perfil.ADMIN);long auditAntes=audit.count(),movAntes=movimentos.count(),userAntes=usuarios.count(),stockAntes=estoques.count();
  for(var entry:mappings.getHandlerMethods().entrySet())if(entry.getValue().getBeanType().getPackageName().startsWith("br.com.almoxarifado")&&entry.getKey().getMethodsCondition().getMethods().contains(org.springframework.web.bind.annotation.RequestMethod.GET))for(String path:entry.getKey().getPatternValues())for(String method:List.of("GET","HEAD")) {
   var response=mvc.perform(request(org.springframework.http.HttpMethod.valueOf(method),path.replaceAll("\\{[^}]+}","999999")).session(s.session())).andReturn().getResponse();assertNotEquals(500,response.getStatus(),path);assertNotEquals(403,response.getStatus(),path);
  }
  assertEquals(auditAntes,audit.count());assertEquals(movAntes,movimentos.count());assertEquals(userAntes,usuarios.count());assertEquals(stockAntes,estoques.count());
 }
 @Test void atorForjadoEmBodyQueryHeaderNaoSubstituiContexto()throws Exception {
  var m=material();var s=login(Perfil.ADMIN);var destino=new Almoxarifado();destino.setNome("H2 ator destino");destino=locais.saveAndFlush(destino);Long ator=usuarios.findByUsername("admin").orElseThrow().getId();
  mvc.perform(put("/estoques/entrada").session(s.session()).header("X-CSRF-TOKEN",s.token()).header("X-Ator-Id","999999").param("atorId","999999").param("atorAuditoria","forjado").param("produtoId",m.produto().getId().toString()).param("almoxarifadoId",m.local().getId().toString()).param("quantidade","2").param("solicitanteId",m.pessoa().getId().toString()).param("responsavelId",m.pessoa().getId().toString())).andExpect(status().isOk());
  var body=Map.of("origemId",m.local().getId(),"destinoId",destino.getId(),"responsavelId",m.pessoa().getId(),"itens",List.of(Map.of("produtoId",m.produto().getId(),"quantidade",1)),"atorId",999999,"atorAuditoria","forjado");
  mvc.perform(post("/transferencias").session(s.session()).header("X-CSRF-TOKEN",s.token()).header("X-Ator-Id","999999").contentType(MediaType.APPLICATION_JSON).content(json.writeValueAsString(body))).andExpect(status().isOk());
  var eventos=audit.findAll().stream().filter(x->Set.of("ESTOQUE_ENTRADAESTOQUE","TRANSFERENCIAESTOQUE_CRIAR").contains(x.getEvento())).toList();assertEquals(2,eventos.size());for(var evento:eventos){assertEquals(ator,evento.getAtorId());assertEquals(m.pessoa().getId(),evento.getResponsavelOperacionalId());assertFalse(json.writeValueAsString(evento).contains("forjado"));}
 }

}
