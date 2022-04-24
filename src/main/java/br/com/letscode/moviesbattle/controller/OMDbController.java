package br.com.letscode.moviesbattle.controller;

import br.com.letscode.moviesbattle.service.OMDbAPIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/omdb")
public record OMDbController(OMDbAPIService omdbAPIService) {
    @Operation(summary = "Movie data information integrated with the OMDb API (https://www.omdbapi.com/).")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Gets one random movie with your data.")
    })
    @GetMapping({"/next"})
    public OMDbAPIService.Movie next() throws InterruptedException {
        return omdbAPIService.next();
    }
}
