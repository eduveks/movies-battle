# Movies Battle

Este é um jogo que é apenas implementado via API e não tem front-end ainda.

É desenvolvido com Spring Boot, Autenticação JWT (JSON Web Token), definição OpenAPI, e testes unitários com JUnit.

A OMDb API (https://www.omdbapi.com/) é consumida como fonte para a informação dos filmes.

## Objetivo

O jogador deve escolher um filme a partir de dois dados com a esperança de ser o filme com a maior pontuação no IMDB.

Os filmes são randômicos, e a pontuação é baseada no cálculo da avaliação X votos.

Este projeto é uma proposta para um desafio feito pela Let's Code (https://letscode.com.br/).

## Configuração

Antes de começar, com base nos exemplos de configuração, deve realizar a cópia dos arquivos:

- De `src/main/resources/application-sample.properties`
  - Para `src/main/resources/application.properties`
- De `src/test/resources/application-sample.properties`
  - Para `src/test/resources/application.properties`

Ou com os comandos de cópia:

```
cp src/main/resources/application-sample.properties src/main/resources/application.properties
cp src/test/resources/application-sample.properties src/test/resources/application.properties
```

Agora aqui, você deve configurar a sua chave de API do OMDb nestes arquivos:

- `src/main/resources/application.properties`
- `src/test/resources/application.properties`

Onde no fim destes arquivos de configuração você vai encontrar isto:

```
omdbapi.key= ~~~ YOUR KEY HERE ~~~
```

Então substitua com a sua chave gerada em:
- https://www.omdbapi.com/

Faça a gestão do resto da configuração como você preferir.

## Iniciar

Certifique que utiliza o Java 17.

Para iniciar o projeto com o Gradle v7+:

```
gradle bootRun
```

### OpenAPI

Após iniciar, está será a URL da definição do OpenAPI:

- http://localhost:8088/openapi

### Login

Você tem estes usuários:
- `user1`, `user2`, `user3`, e `user4`.

A senha é sempre `123` para todos os usuários, porque a segurança é muito importante. :sweat_smile:

```
curl -X POST -H 'Content-Type: application/json' \
   -d '{"username":"user1","password":"123"}' \
   http://localhost:8088/api/login
```

Retorna o seu precioso token.

> Você deve utilizar o token fornecido no cabeçalho HTTP `Authorization` em todos os serviços abaixo.

### 1. Iniciar uma Partida

```
curl -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   http://localhost:8088/api/battle/match/start
```

Nenhuma saída de dados é esperada.

### 2. Iniciar uma Rodada

Isto irá criar uma nova rodada na partida atual.

```
curl -X POST -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   -H 'Content-Type: application/json' \
   http://localhost:8088/api/battle/match/round
```

É fornecida a informação de dois filmes para você poder escolher entre qual deles tem a maior avaliação X votos.

### 3. Salva a aposta na Rodada

O valor do `bid` define o número do filme escolhido, o qual deve ser `1` para o primeiro e `2` para o segundo.

```
curl -X POST -H 'Content-Type: application/json' \
   -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   -d '{"bid":1}' \
   http://localhost:8088/api/battle/match/round
```

> Você pode jogar quantas rodadas você quiser na partida atual.

### 4. Finalizar a Partida

Se você perder 3 vezes seguidas, então a partida será concluída automaticamente, **FIM DE JOGO**.

Caso contrário, se você desejar encerrar a qualquer momento:

```
curl -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   http://localhost:8088/api/battle/match/end
```

Nenhuma saída de dados é esperada.

### Classificação

Aqui é apresentada a classificação da pontuação de todos os jogadores.

```
curl -X POST -H 'Content-Type: application/json' \
   -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   -d '{"page":1}' \
   http://localhost:8088/api/battle/rank
```

## Teste

Será utilizado o porto `8089` para executar os testes:

```
gradle test
```

Divirta-se.
