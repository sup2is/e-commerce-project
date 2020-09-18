package me.sup2is.order.exception;

public class PaymentFailureException extends RuntimeException {

    public PaymentFailureException() {
        super();
    }

    public PaymentFailureException(String message) {
        super(message);
    }
}
