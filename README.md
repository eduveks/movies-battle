# Movies Battle

This is a game only implemented with the API and has no front-end yet.

Is developed with Spring Boot, JWT Authentication (JSON Web Token), OpenAPI definition, and unit tests with JUnit.

The OMDb API (https://www.omdbapi.com/) is consumed as the source for all movies information.

## Goal

The player should choose one movie from the two given and hope to hit the movie with the highest IMDB score.

The movies are randomly, and the score is calculated based on the rating X votes.

This project is a proposal for a challenge made by Let's Code (https://letscode.com.br/).

## Configuration

Before starting, based on the configuration examples, you must copy these files:

- From `src/main/resources/application-sample.properties`
    - To `src/main/resources/application.properties`
- From `src/test/resources/application-sample.properties`
    - To `src/test/resources/application.properties`

Or with the copy commands:

```
cp src/main/resources/application-sample.properties src/main/resources/application.properties
cp src/test/resources/application-sample.properties src/test/resources/application.properties
```

Now here, you should configure your OMDb API key in these files:

- `src/main/resources/application.properties`
- `src/test/resources/application.properties`

At the end of these files you will find this:

```
omdbapi.key= ~~~ YOUR KEY HERE ~~~
```

Then replace it with your key generated in: 
- https://www.omdbapi.com/

Manages the rest of the configuration as you prefer.

## Start

Make sure you use Java 17.

To start the project with the Gradle v7+:

```
gradle bootRun
```

### OpenAPI

After the start, this will be the OpenAPI definition URL:

- http://localhost:8088/openapi

### Login:

You have these users:
- `user1`, `user2`, `user3`, and `user4`.

The password is always `123` for all users, because security is very important. :sweat_smile:

```
curl -X POST -H 'Content-Type: application/json' \
   -d '{"username":"user1","password":"123"}' \
   http://localhost:8088/api/login
```

Returns your precious token.

> You should use the token given in the `Authorization` HTTP Header in all other services below.

### 1. Start a new Match

```
curl -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   http://localhost:8088/api/battle/match/start
```

No output is expected.

### 2. Create a new Round

This will create a new round in the current match.

```
curl -X POST -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   -H 'Content-Type: application/json' \
   http://localhost:8088/api/battle/match/round
```

It gives information about two movies to let your choose which of them has the highest rating X votes.

### 3. Save your Round bet

The `bid` value sets the number of your movie chosen, which should be `1` for the first, then `2` for the second.

```
curl -X POST -H 'Content-Type: application/json' \
   -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   -d '{"bid":1}' \
   http://localhost:8088/api/battle/match/round
```

> You can play many rounds as you want in the current match.

### 4. End the Match

If you lose three times in a row, then the match will end automatically, **END GAME**.

Otherwise, if you want to end it at any time:

```
curl -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   http://localhost:8088/api/battle/match/end
```

No output is expected.

### Rank

Here shows the rank score of all users.

```
curl -X POST -H 'Content-Type: application/json' \
   -H "Authorization: Bearer <YOUR_ACCESS_TOKEN>" \
   -d '{"page":1}' \
   http://localhost:8088/api/battle/rank
```

## Test

Port `8089` will be used to run the tests:

```
gradle test
```

Have fun.
