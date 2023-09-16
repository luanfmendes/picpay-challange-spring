package br.com.challenge.picpay.exceptions;

public class TransactionAuthorizationException extends RuntimeException {

    public TransactionAuthorizationException() {
        super();
    }

    public TransactionAuthorizationException(String message) {
        super(message);
    }

    public TransactionAuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}

