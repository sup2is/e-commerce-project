package me.sup2is.order.exception;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException() {
        super();
    }

    public OrderNotFoundException(String message) {
        super(message);
    }
}
