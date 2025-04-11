package com.bluestaq.elevator.exceptions;


public class ElevatorNotFoundException extends RuntimeException {
    public ElevatorNotFoundException(String s) {
        super(s);
    }
}
