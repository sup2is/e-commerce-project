package me.sup2is.member.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.member.domain.Member;

@Getter
@AllArgsConstructor
public class MemberResponseDto {

    private String email;

    private String name;

    private String address;

    private int zipCode;

    private String phone;

    public static MemberResponseDto createMemberResponseDto(Member member) {
        return new MemberResponseDto(member.getEmail(),
                member.getName(),
                member.getAddress(),
                member.getZipCode(),
                member.getPhone());
    }

}
