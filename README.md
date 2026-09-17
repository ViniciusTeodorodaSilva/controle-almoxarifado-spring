# 📦 Sistema de Controle de Almoxarifado Industrial

Sistema em desenvolvimento para controle de almoxarifado, criado a partir de necessidades observadas no ambiente industrial.

O projeto faz parte dos meus estudos de desenvolvimento Backend Java e tem como objetivo aplicar conhecimentos de programação na construção de uma solução para um problema real.

## 🎯 Objetivo do projeto

Desenvolver uma aplicação capaz de auxiliar no gerenciamento de materiais e produtos de um almoxarifado industrial, centralizando informações e facilitando o controle de estoque e movimentações.

A ideia surgiu a partir da minha experiência profissional na área industrial, onde o controle de materiais, ferramentas e equipamentos faz parte da rotina das equipes.

## ⚙️ Funcionalidades

O projeto está em desenvolvimento e será evoluído gradualmente.

Atualmente, a estrutura inicial contempla o gerenciamento de produtos e a separação das responsabilidades da aplicação em camadas.

Entre as funcionalidades planejadas estão:

- Cadastro de produtos
- Consulta de produtos
- Atualização de informações
- Controle de estoque
- Registro de entradas e saídas
- Controle de movimentações
- Controle de retirada e devolução de equipamentos
- Cadastro de funcionários
- Histórico de movimentações

## 🛠️ Tecnologias utilizadas

- Java
- Spring Boot
- Maven
- Programação Orientada a Objetos (POO)
- Git
- GitHub

## 🏗️ Estrutura do projeto

O projeto está organizado em camadas:

- `controller` — responsável pela entrada das requisições
- `service` — responsável pelas regras de negócio
- `repository` — responsável pelo acesso e persistência dos dados
- `model` — responsável pelas entidades/modelos da aplicação

Essa organização ajuda a separar as responsabilidades e facilita a manutenção e evolução do sistema.

## 📂 Estrutura atual

```text
src/main/java/br/com/almoxarifado
│
├── controller
│   └── ProdutoController.java
│
├── model
│   └── Produto.java
│
├── repository
│   └── ProdutoRepository.java
│
├── service
│   └── ProdutoService.java
│
└── ControleAlmoxarifadoApplication.java
