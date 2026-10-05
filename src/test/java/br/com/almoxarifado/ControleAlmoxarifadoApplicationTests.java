package br.com.almoxarifado;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@org.springframework.security.test.context.support.WithMockUser(authorities={"USUARIO_GERENCIAR","AUDITORIA_LER","PRODUTO_LER","PRODUTO_GERENCIAR","CATEGORIA_LER","CATEGORIA_GERENCIAR","UNIDADE_LER","UNIDADE_GERENCIAR","ESTOQUE_LER","ESTOQUE_MOVIMENTAR","ESTOQUE_TRANSFERIR","ESTOQUE_CONFIGURAR","SOLICITACAO_LER","SOLICITACAO_CRIAR","SOLICITACAO_APROVAR","SOLICITACAO_REJEITAR","SOLICITACAO_SEPARAR","SOLICITACAO_ATENDER","NECESSIDADE_COMPRA_LER","NECESSIDADE_COMPRA_CRIAR","MOVIMENTACAO_LER","FUNCIONARIO_LER","FUNCIONARIO_GERENCIAR","ALMOXARIFADO_LER","ALMOXARIFADO_GERENCIAR"})
@SpringBootTest
@ActiveProfiles("test")
class ControleAlmoxarifadoApplicationTests {

	@Test
	void contextLoads() {
	}

}
