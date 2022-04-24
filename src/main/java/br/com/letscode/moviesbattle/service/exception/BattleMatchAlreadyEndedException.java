package br.com.letscode.moviesbattle.service.exception;

public class BattleMatchAlreadyEndedException extends Exception implements ServiceException {
    public BattleMatchAlreadyEndedException() {
        super();
    }

    public BattleMatchAlreadyEndedException(String message) {
        super(message);
    }

    public BattleMatchAlreadyEndedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleMatchAlreadyEndedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "already-ended";
    }
}
