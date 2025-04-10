package com.bluestaq.elevator.exceptions;

public class SameFloorException extends RuntimeException{
    public SameFloorException(String s) {
        super(s);
    }
}
