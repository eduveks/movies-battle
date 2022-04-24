package br.com.letscode.moviesbattle.service.exception;

public class BattleMatchRoundInvalidBidException extends Exception implements ServiceException {
    public BattleMatchRoundInvalidBidException() {
        super();
    }

    public BattleMatchRoundInvalidBidException(String message) {
        super(message);
    }

    public BattleMatchRoundInvalidBidException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleMatchRoundInvalidBidException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "round-invalid-bid";
    }
}
