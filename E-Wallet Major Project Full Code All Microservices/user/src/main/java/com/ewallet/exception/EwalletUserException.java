package com.ewallet.exception;

public class EwalletUserException extends RuntimeException{

    String type;

    public EwalletUserException(String type,String message){
        super(message);
        this.type=type;
    }

    public String getType(){
        return this.type;
    }
}