package me.sup2is;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum JwtTokenType {
    AUTH(1 * 60 * 60),
    REFRESH(3 * 60 * 60);

    private long expiration;

}