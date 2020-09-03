package me.sup2is.client.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
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

    public User toUser() {
        List<SimpleGrantedAuthority> authorities = this.authorities.stream()
                .map(s -> new SimpleGrantedAuthority("ROLE_" + s))
                .collect(Collectors.toList());
        return new User(this.email, this.password, authorities);
    }
}
