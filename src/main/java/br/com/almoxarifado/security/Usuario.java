package br.com.almoxarifado.security;
import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import br.com.almoxarifado.model.Funcionario;
import java.time.Instant;
@Entity @Table(name="bes_usuario",uniqueConstraints=@UniqueConstraint(name="uk_bes_usuario_username",columnNames="username"))
public class Usuario {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=80) private String username;
 @JsonIgnore @Column(name="senha_hash",nullable=false,length=100) private String senhaHash;
 @JsonIgnore @Column(name="bootstrap_chave",unique=true,length=20,updatable=false) private String bootstrapChave;
 @Column(name="nome_exibicao",nullable=false,length=120) private String nomeExibicao;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Perfil perfil;
 @Column(nullable=false) private boolean ativo=true;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="funcionario_id") private Funcionario funcionario;
 @Column(name="criado_em",nullable=false,updatable=false) private Instant criadoEm=Instant.now();
 @Column(name="atualizado_em",nullable=false) private Instant atualizadoEm=Instant.now();
 @Column(name="ultimo_login_em") private Instant ultimoLoginEm;
 @Column(name="auth_version",nullable=false) private long authVersion;
 @Version @Column(name="versao",nullable=false) private long versao;
 protected Usuario() {}
 public Usuario(String username,String hash,String nome,Perfil perfil){this.username=username;this.senhaHash=hash;this.nomeExibicao=nome;this.perfil=perfil;}
 public Long getId(){return id;} public String getUsername(){return username;} public String getSenhaHash(){return senhaHash;}
 public String getNomeExibicao(){return nomeExibicao;} public Perfil getPerfil(){return perfil;} public boolean isAtivo(){return ativo;}
 public Funcionario getFuncionario(){return funcionario;} public Instant getCriadoEm(){return criadoEm;} public Instant getAtualizadoEm(){return atualizadoEm;}
 public Instant getUltimoLoginEm(){return ultimoLoginEm;} public long getAuthVersion(){return authVersion;}
 void alterar(String login,String nome,Perfil novoPerfil,boolean ativo,Funcionario funcionario){this.username=login;this.nomeExibicao=nome;this.perfil=novoPerfil;this.ativo=ativo;this.funcionario=funcionario;this.authVersion++;this.atualizadoEm=Instant.now();}
 void senha(String hash){senhaHash=hash;authVersion++;atualizadoEm=Instant.now();}
 void login(){ultimoLoginEm=Instant.now();}
 void marcarBootstrap(){bootstrapChave="ADMIN_INICIAL";}
}
