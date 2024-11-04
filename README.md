# Expense Tracker Restful Api

A API de Rastreamento de Despesas permite que os usuários gerenciem suas despesas, com funcionalidades para cadastro,
autenticação, e operações CRUD (criar, ler, atualizar e excluir) sobre as despesas. Cada usuário possui seu próprio
conjunto de despesas, acessível somente após autenticação.

## Funcionalidades

A API oferece as seguintes funcionalidades:

* **Cadastro de Usuário**: Permite que novos usuários se cadastrem.
* **Autenticação e Sessão**: Gera e valida tokens JWT (JSON Web Token) para autenticação e manutenção da sessão.
* **Listagem e Filtro de Despesas**: Exibe e filtra despesas com as seguintes opções:
    * Última semana
    * Último mês
    * Últimos 3 meses
    * Por categoria
    * Por data que o usuario informa
* **Personalizado** (definindo uma data inicial e uma data final)
* **Adicionar** Nova Despesa: Permite adicionar novas despesas ao banco de dados.
* **Remover Despesas**: Permite excluir despesas existentes.
* **Atualizar Despesas**: Permite modificar despesas existentes.

## Categorias de Despesas

* Alimentação
* Transporte
* Lazer
* Saúde
* Moradia
* Educação
* Outros

## Requisitos

- Ter o [java](https://www.java.com/download/ie_manual.jsp) instalado em sua maquina para rodar o programa
  e também o [maven](https://maven.apache.org/install.html).

## Instalação

- Clone o repositório

```git
  git clone https://github.com/pedro-henrique1/ExpenseTracker.git
  cd ExpenseTracker
```

- Configure o arquivo com as variáveis de ambiente

```
  cd src/main/resources
  
  cp example.application.properties  application.properties
```


- Instalar Dependência:

```
  mvn clean install
  mvn spring-boot:run

```

## Endpoints

### Expense Controller
* **POST /v1/expense/create**: Cria uma nova despesa.
* **DELETE /v1/expense/{id}**: Deleta uma despesa pelo ID.
* **PATCH /v1/expense/{id}**: Atualiza uma despesa com base no ID.
* **GET /v1/expense/all**: Retorna todas as despesas do usuário autenticado.
### Authentication Controller
* **POST /v1/auth/register**: Registra um novo usuário.
* **POST /v1/auth/login**: Realiza o login do usuário e retorna um token JWT para autenticação.
### Expense Filter Controller
* **GET /v1/expense/filter/last-three-months**: Retorna as despesas dos últimos 3 meses.
* **GET /v1/expense/filter/filter**: Busca as despesas com base na categoria especificada.
* **GET /v1/expense/filter/date**: Busca as despesas com base em uma data específica.
* **GET /v1/expense/filter/between**: Busca as despesas dentro de um intervalo de datas (início e fim).

## Autenticação
A autenticação nesta API é baseada em JWT (JSON Web Tokens). Após o login, o usuário recebe um token que deve ser incluído no cabeçalho de cada requisição protegida para acessar as informações pessoais de despesas. O token é necessário para garantir que somente o usuário autenticado tenha acesso às suas próprias despesas.


## Licença
Este projeto está licenciado sob a licença MIT. Sinta-se à vontade para usar, modificar e distribuir conforme os termos da licença.









