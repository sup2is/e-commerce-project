package me.sup2is.member.client.dto;

import lombok.*;
import me.sup2is.member.domain.Member;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@ToString
public class MemberClientDto {

    private String email;

    private String password;

    private List<String> authorities;

    private String name;

    private String address;

    private int zipCode;

    private String phone;

    private boolean enable;

    public static MemberClientDto createMemberClientDto(Member member) {
        MemberClientDto memberClientDto = new MemberClientDto();
        memberClientDto.address = member.getAddress();
        memberClientDto.authorities = member.getAuthorities().stream()
                .map(a -> a.getAuth().name())
                .collect(Collectors.toList());
        memberClientDto.email = member.getEmail();
        memberClientDto.enable = member.isEnable();
        memberClientDto.name = member.getName();
        memberClientDto.password = member.getPassword();
        memberClientDto.phone = member.getPhone();
        memberClientDto.zipCode = member.getZipCode();
        return memberClientDto;
    }


}
