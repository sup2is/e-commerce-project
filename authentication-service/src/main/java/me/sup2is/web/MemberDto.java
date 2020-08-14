package me.sup2is.web;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@AllArgsConstructor
public class MemberDto {

    private final String memberId;
    private final String password;

}
