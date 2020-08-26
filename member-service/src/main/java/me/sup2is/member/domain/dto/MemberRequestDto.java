package me.sup2is.member.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.member.domain.Member;

import javax.validation.constraints.Email;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Getter
@AllArgsConstructor
public class MemberRequestDto {

    @NotNull
    @Email
    private String email;

    @NotNull
    @Min(6)
    private String password;

    @NotNull
    private String name;

    private String address;

    private int zipCode;

    @NotNull
    @Pattern(regexp="(^$|[0-9]{10})")
    private String phone;

    public Member toEntity() {
        Member.Builder builder = new Member.Builder();
        return Member.createMember(builder.email(email)
                .zipCode(zipCode)
                .phone(phone)
                .password(password)
                .address(address)
                .name(name));
    }
}
