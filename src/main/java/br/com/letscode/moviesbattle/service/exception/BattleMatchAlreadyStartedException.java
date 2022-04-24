package br.com.letscode.moviesbattle.service.exception;

public class BattleMatchAlreadyStartedException extends Exception implements ServiceException {
    public BattleMatchAlreadyStartedException() {
        super();
    }

    public BattleMatchAlreadyStartedException(String message) {
        super(message);
    }

    public BattleMatchAlreadyStartedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleMatchAlreadyStartedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "already-started";
    }
}
