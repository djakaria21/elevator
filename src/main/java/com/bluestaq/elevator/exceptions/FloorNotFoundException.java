package com.bluestaq.elevator.exceptions;

public class FloorNotFoundException extends RuntimeException{
    public FloorNotFoundException(String s) {
        super(s);
    }
}
