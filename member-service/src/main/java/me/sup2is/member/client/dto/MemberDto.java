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
public class MemberDto {

    private long memberId;

    private String email;

    private String password;

    private List<String> authorities;

    private String name;

    private String address;

    private int zipCode;

    private String phone;

    private boolean enable;

    public static MemberDto createMemberDto(Member member) {
        MemberDto memberDto = new MemberDto();
        memberDto.memberId = member.getId();
        memberDto.address = member.getAddress();
        memberDto.authorities = member.getAuthorities().stream()
                .map(a -> a.getAuth().name())
                .collect(Collectors.toList());
        memberDto.email = member.getEmail();
        memberDto.enable = member.isEnable();
        memberDto.name = member.getName();
        memberDto.password = member.getPassword();
        memberDto.phone = member.getPhone();
        memberDto.zipCode = member.getZipCode();
        return memberDto;
    }


}
