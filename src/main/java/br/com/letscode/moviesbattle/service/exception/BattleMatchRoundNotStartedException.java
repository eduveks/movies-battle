package br.com.letscode.moviesbattle.service.exception;

public class BattleMatchRoundNotStartedException extends Exception implements ServiceException {
    public BattleMatchRoundNotStartedException() {
        super();
    }

    public BattleMatchRoundNotStartedException(String message) {
        super(message);
    }

    public BattleMatchRoundNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleMatchRoundNotStartedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "round-not-started";
    }
}
