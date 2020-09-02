package me.sup2is.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;


@ToString
@Getter
@AllArgsConstructor
public class AuthenticationRequestDto {
    private final String username;
    private final String password;
}
