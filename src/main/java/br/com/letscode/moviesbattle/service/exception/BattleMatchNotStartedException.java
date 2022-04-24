package br.com.letscode.moviesbattle.service.exception;

public class BattleMatchNotStartedException extends Exception implements ServiceException {
    public BattleMatchNotStartedException() {
        super();
    }

    public BattleMatchNotStartedException(String message) {
        super(message);
    }

    public BattleMatchNotStartedException(String message, Throwable cause) {
        super(message, cause);
    }

    public BattleMatchNotStartedException(Throwable cause) {
        super(cause);
    }

    @Override
    public String getCode() {
        return "not-started";
    }
}
