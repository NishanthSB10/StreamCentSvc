package com.stream.cent.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

@JsonSerialize
@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RegisterUser {
    @JsonProperty(required = true)
    String userName;
    @JsonProperty(required = true)
    String email;
    @JsonProperty(required = true)
    String password;
}
