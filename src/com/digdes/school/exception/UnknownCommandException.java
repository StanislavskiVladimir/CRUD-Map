package com.digdes.school.exception;

public class UnknownCommandException extends CommandException{
    public UnknownCommandException(String message) {
        super("����������� ������� " + message);
    }
}
