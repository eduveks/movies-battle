package br.com.letscode.moviesbattle.controller;

import br.com.letscode.moviesbattle.controller.util.AuthenticatedController;
import br.com.letscode.moviesbattle.service.BattleRankService;
import br.com.letscode.moviesbattle.service.BattleMatchService;
import br.com.letscode.moviesbattle.service.exception.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class BattleController extends AuthenticatedController {
    private final BattleMatchService battleMatchService;
    private final BattleRankService battleRankService;

    @Operation(summary = "Will start a new game match for the logged user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The new game match started.")
    })
    @GetMapping({"/battle/match/start"})
    public ResponseEntity<?> start() throws BattleMatchAlreadyStartedException {
        battleMatchService.startMatch(getUser());
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "Creates a new round to choose between two movies.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All information from two random movies.")
    })
    @GetMapping({"/battle/match/round"})
    public BattleMatchService.MatchRound newRound() throws BattleMatchAlreadyEndedException, BattleMatchRoundNotSavedYetException, InterruptedException {
        return battleMatchService.newRound(getUser());
    }

    @Operation(summary = "Close the round with the user bet choice.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Result of the bid made.")
    })
    @PostMapping({"/battle/match/round"})
    public BattleMatchService.MatchRoundResult saveRound(@RequestBody MatchRoundSaveForm form) throws BattleMatchAlreadyEndedException, BattleMatchRoundNotStartedException, BattleMatchRoundInvalidBidException, BattleMatchNotStartedException, BattleMatchRoundNotSavedYetException {
        return battleMatchService.saveRound(getUser(), form.bid());
    }

    @Operation(summary = "Will end the current game match for the logged user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "The current game match ended.")
    })
    @GetMapping({"/battle/match/end"})
    public ResponseEntity<?> end() throws BattleMatchAlreadyEndedException, BattleMatchNotStartedException, BattleMatchRoundNotSavedYetException {
        battleMatchService.endMatch(getUser());
        return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);
    }

    @Operation(summary = "The rank with the score of each user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users and their scores. It is limited to a max of 10 records.")
    })
    @PostMapping({"/battle/rank"})
    public BattleRankService.Rank history(@RequestBody RankForm form) {
        return battleRankService.load(form.page());
    }

    @ExceptionHandler({
            BattleMatchAlreadyEndedException.class,
            BattleMatchAlreadyStartedException.class,
            BattleMatchNotStartedException.class,
            BattleMatchRoundInvalidBidException.class,
            BattleMatchRoundNotSavedYetException.class,
            BattleMatchRoundNotStartedException.class
    })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResult handleException(ServiceException e) {
        return new ErrorResult(true, e.getCode());
    }

    private record MatchRoundSaveForm(int bid) {}
    private record RankForm(int page) {}
}
