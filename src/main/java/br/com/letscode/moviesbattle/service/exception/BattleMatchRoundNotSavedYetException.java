package br.com.letscode.moviesbattle.service.exception;

public class BattleMatchRoundNotSavedYetException extends Exception implements ServiceException {
    public BattleMatchRoundNotSavedYetException() {
        super();
    }

    public BattleMatchRoundNotSavedYetException(String message) {
        super(message);
    }

    public BattleMatchRoundNotSavedYetException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleMatchRoundNotSavedYetException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "round-not-saved";
    }
}
