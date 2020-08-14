package me.sup2is.web;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * mock 객체임
 * 추후 member service 통합시에 적용할 예정
 */
@ToString
@Getter
@AllArgsConstructor
public class MemberInfo {

    private String email;
    private String password;

}
