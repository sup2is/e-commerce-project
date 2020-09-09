package me.sup2is.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
@ToString
public class MemberDto {

    private Long memberId;

    private String email;

    private String password;

    private List<String> authorities;

    private String name;

    private String address;

    private int zipCode;

    private String phone;

    private boolean enable;

}
