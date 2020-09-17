package me.sup2is.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ModifyMember {

    private String password;

    private String name;

    private String address;

    private int zipCode;

    private String phone;


}
