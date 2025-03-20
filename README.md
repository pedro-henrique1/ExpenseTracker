# Expense Tracker Restful Api


[JAVA_BADGE]:https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white
[SPRING_BADGE]: https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white
[Docker]:https://img.shields.io/badge/Docker-2CA5E0?style=for-the-badge&logo=docker&logoColor=white
[sql]:https://img.shields.io/badge/MySQL-005C84?style=for-the-badge&logo=mysql&logoColor=white
[swagger]:https://img.shields.io/badge/Swagger-85EA2D?style=for-the-badge&logo=Swagger&logoColor=white
[ci]:https://img.shields.io/github/actions/workflow/status/pedro-henrique1/ExpenseTracker/maven.yml?style=for-the-badge
[checks]:https://img.shields.io/github/check-runs/pedro-henrique1/ExpenseTracker/main?style=for-the-badge



![][JAVA_BADGE]
![][SPRING_BADGE]
![][Docker]
![][sql]
![][swagger]

![][checks]
![][ci]

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

- Docker e Docker Compose: Se ainda não estiverem instalados, baixe-os nos links abaixo:
    - [docker](https://docs.docker.com/get-started/get-docker/)
    - [docker compose](https://www.digitalocean.com/community/tutorials/how-to-install-and-use-docker-compose-on-ubuntu-20-04).

## Instalação

- Clone o repositório

```git
  git clone https://github.com/pedro-henrique1/ExpenseTracker.git
  cd ExpenseTracker
```

## Configurando o Ambiente

- Crie um arquivo .env a partir do exemplo fornecido:

```
  cp .env.example .env
```

- Em seguida, configure o arquivo de propriedades de aplicação:


```
  cd src/main/resources
  
  cp example.application.properties  application.properties
```

- Iniciando o projeto:

```
  docker-compose up --build
  
```

## Documentação com Swagger

Esta API possui documentação interativa gerada automaticamente pelo Swagger, o que facilita a visualização e o teste dos
endpoints.

Após iniciar a aplicação, acesse a documentação pelo seguinte endereço:

* **Swagger UI**: http://localhost:8080/swagger-ui/index.html

Através da interface Swagger, você pode explorar todos os endpoints disponíveis, visualizar parâmetros de requisição e
resposta, além de testar as requisições diretamente na interface.

## Endpoints

### Expense Controller

* **POST /v1/expense/create**: Cria uma nova despesa.
    * Exemplo de requisição
  ```json
    {
      "description": "Compra de supermercado",
      "price": 150.75,
      "date": "2025-02-04T15:30:00",  
      "paymentMethod": "CARTAO_CREDITO",
      "category": "ALIMENTACAO",
      "observation": "Compra mensal de alimentos"
    }
    ```



* **DELETE /v1/expense/{id}**: Deleta uma despesa pelo ID.
* **PATCH /v1/expense/{id}**: Atualiza uma despesa com base no ID.
* **GET /v1/expense/all**: Retorna todas as despesas do usuário autenticado.

### Authentication Controller

* **POST /v1/auth/register**: Registra um novo usuário.

``` json
{
  "email": "test@gmail.com",
  "password": "123"
}
```

* **POST /v1/auth/login**: Realiza o login do usuário e retorna um token JWT para autenticação.

``` json
{
  "email": "test@gmail.com",
  "password": "123"
}
```

Resposta:

``` json
{  
  "token": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0QGdtYWlsLmNvbSIsImlhdCI6MTczODcwMzAzMywiZXhwIjoxNzM4NzA2NjMzfQ.uExj9HpKIRwlVJdRjeGMw8UA4suHmOah-X0hZnfjEZc",
  "expiresIn": 3600000
}
```

### Expense Filter Controller

* **GET /v1/expense/filter/last-three-months**: Retorna as despesas dos últimos 3 meses.
* **GET /v1/expense/filter/filter**: Busca as despesas com base na categoria especificada.
* **GET /v1/expense/filter/date**: Busca as despesas com base em uma data específica.
* **GET /v1/expense/filter/between**: Busca as despesas dentro de um intervalo de datas (início e fim).

## Autenticação

A autenticação nesta API é baseada em JWT (JSON Web Tokens). Após o login, o usuário recebe um token que deve ser
incluído no cabeçalho de cada requisição protegida para acessar as informações pessoais de despesas. O token é
necessário para garantir que somente o usuário autenticado tenha acesso às suas próprias despesas.

## Licença

Este projeto está licenciado sob a licença [MIT](LICENSE). Sinta-se à vontade para usar, modificar e distribuir conforme
os termos da licença.









