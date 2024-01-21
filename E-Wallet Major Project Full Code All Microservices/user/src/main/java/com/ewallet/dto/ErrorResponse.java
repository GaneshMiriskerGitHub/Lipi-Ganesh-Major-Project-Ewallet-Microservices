package com.ewallet.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ErrorResponse {

    Map<String,String> error;

}