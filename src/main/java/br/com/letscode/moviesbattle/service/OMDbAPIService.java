package br.com.letscode.moviesbattle.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class OMDbAPIService {
    private static final int SEARCH_PAGE_SIZE = 10;

    private final RestTemplate restTemplate;

    private final String searchURL;
    private final String detailURL;

    private final API.SearchTerm[] searchTerms = new API.SearchTerm[] {
            new API.SearchTerm("que", 2834),
            new API.SearchTerm("vida", 1286),
            new API.SearchTerm("last", 8121),
            new API.SearchTerm("max", 797),
            new API.SearchTerm("mad", 818),
            new API.SearchTerm("big", 4316),
            new API.SearchTerm("low", 424),
            new API.SearchTerm("pig", 334),
            new API.SearchTerm("rat", 334),
            new API.SearchTerm("end", 3012),
            new API.SearchTerm("ring", 850),
            new API.SearchTerm("light", 2788),
            new API.SearchTerm("life", 12496),
            new API.SearchTerm("dead", 4662),
            new API.SearchTerm("fall", 1792),
            new API.SearchTerm("world", 7807),
            new API.SearchTerm("star", 3018),
            new API.SearchTerm("queen", 1791),
            new API.SearchTerm("king", 3193),
            new API.SearchTerm("lord", 601),
            new API.SearchTerm("met", 586),
            new API.SearchTerm("god", 2272),
            new API.SearchTerm("were", 887),
            new API.SearchTerm("lost", 4459),
            new API.SearchTerm("turn", 696),
            new API.SearchTerm("truth", 1696),
            new API.SearchTerm("sky", 1570),
            new API.SearchTerm("high", 2265),
            new API.SearchTerm("earth", 1927),
            new API.SearchTerm("space", 1951),
            new API.SearchTerm("sea", 2530),
    };

    public OMDbAPIService(@Value("${omdbapi.url}") String baseURL,
                          @Value("${omdbapi.key}") String apiKey,
                          RestTemplateBuilder restTemplateBuilder) {

        searchURL = UriComponentsBuilder.fromHttpUrl(baseURL)
                .queryParam("apikey", apiKey)
                .queryParam("s", "{term}")
                .queryParam("type", "movie")
                .queryParam("page", "{page}")
                .encode()
                .toUriString();

        detailURL = UriComponentsBuilder.fromHttpUrl(baseURL)
                .queryParam("apikey", apiKey)
                .queryParam("i", "{id}")
                .encode()
                .toUriString();

        this.restTemplate = restTemplateBuilder
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    public Movie next() throws InterruptedException {
        while (true) {
            API.SearchResult result = null;
            while (result == null || result.Search == null) {
                API.SearchTerm searchTerm = searchTerms[(int) Math.floor(Math.random() * searchTerms.length)];
                int page = Math.min(searchTerm.total / SEARCH_PAGE_SIZE, 10);
                result = restTemplate.getForObject(searchURL, API.SearchResult.class, searchTerm.term, page);
                if (result != null && result.Search == null) {
                    log.warn("Found no movies for the term \"{}\" on page {}.", searchTerm.term, page);
                    Thread.sleep(1000);
                }
            }
            API.SearchResultItem searchResultItem = result.Search.get((int) Math.floor(Math.random() * result.Search.size()));
            API.Detail detail = restTemplate.getForObject(detailURL, API.Detail.class, searchResultItem.imdbID);
            try {
                if (detail != null) {
                    return new Movie(detail.imdbID, detail.Title, Integer.parseInt(detail.Year),
                            Float.parseFloat(detail.imdbRating), Integer.parseInt(detail.imdbVotes.replace(",", "")),
                            detail.Genre, detail.Director, detail.Plot, detail.Poster);
                }
                return null;
            } catch (NumberFormatException e) {
                log.warn("Invalid rating \"{}\" for the movie \"{}\" with ID: {}", detail.imdbRating, detail.Title, searchResultItem.imdbID);
                Thread.sleep(1000);
            }
        }
    }

    public record Movie(String id, String title, int year, float rating, int votes, String genre, String director, String plot, String poster) {
        public Movie {
            Objects.requireNonNull(id);
            Objects.requireNonNull(title);
            Objects.requireNonNull(genre);
            Objects.requireNonNull(director);
            Objects.requireNonNull(plot);
            Objects.requireNonNull(poster);
        }
    }

    private static class API {
        private record SearchTerm(String term, int total) { }

        public record SearchResultItem(String Title, String Year, String imdbID) { }

        public record SearchResult(List<SearchResultItem> Search, String Response, String Error) { }

        public record Detail(String Title, String Year, String imdbID, String imdbRating, String imdbVotes, String Genre, String Director, String Plot, String Poster) { }
    }
}