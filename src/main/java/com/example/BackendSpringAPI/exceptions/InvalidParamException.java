package com.example.BackendSpringAPI.exceptions;

public class InvalidParamException extends Exception{
    public InvalidParamException(String message){
        super(message);
    }
}
