package me.sup2is.member.domain.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import me.sup2is.member.domain.Member;

import javax.validation.constraints.*;

@Getter
@AllArgsConstructor
public class MemberRequestDto {

    @NotEmpty(message = "email must not be null")
    @Email(message = "invalid email format")
    private String email;

    @NotEmpty(message = "password must not be null")
    @Min(value = 6, message = "password must be greater than {value}")
    private String password;

    @NotEmpty(message = "name must not be null")
    private String name;

    private String address;

    private int zipCode;

    @NotEmpty(message = "phone must not be null")
    @Pattern(regexp="(^$|[0-9]{10})", message = "phone format is 010-xxxx-xxxx")
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
