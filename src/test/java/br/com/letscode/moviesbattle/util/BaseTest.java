package br.com.letscode.moviesbattle.util;

import br.com.letscode.moviesbattle.config.JSONWebTokenConstants;
import br.com.letscode.moviesbattle.service.JSONWebTokenService;
import br.com.letscode.moviesbattle.service.OMDbAPIService;
import io.restassured.specification.RequestSpecification;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;

import java.util.Map;

import static io.restassured.RestAssured.given;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BaseTest {
    @LocalServerPort
    private int port;

    private RequestSpecification loggedClient;

    protected RequestSpecification restClient() {
        return given()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .port(port);
    }

    protected RequestSpecification restLogged() {
        return loggedClient;
    }

    protected RequestSpecification restLogin(String username, String password) {
        var login = Map.of(
                JSONWebTokenConstants.INPUT_USERNAME_KEY, username,
                JSONWebTokenConstants.INPUT_PASSWORD_KEY, password
        );

        String token = restClient()
                .basePath(JSONWebTokenConstants.LOGIN_URL)
                .body(login)
                .when()
                .post()
                .then()
                .statusCode(200)
                .extract().as(JSONWebTokenService.AccessToken.class)
                .token();

        loggedClient = restClient()
                .headers(JSONWebTokenConstants.HEADER_KEY, JSONWebTokenConstants.TOKEN_PREFIX + token);

        return loggedClient;
    }
}
