package me.sup2is.member.domain;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import me.sup2is.member.domain.dto.ModifyMember;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String password;

    private String name;

    private String address;

    private int zipCode;

    private String phone;

    @OneToMany(mappedBy = "member")
    private List<Authority> authorities = new ArrayList<>();

    private boolean enable;

    public static Member createMember(Builder builder) {
        Member member = new Member();
        member.email = builder.email;
        member.password = builder.password;
        member.name = builder.name;
        member.address = builder.address;
        member.zipCode = builder.zipCode;
        member.phone = builder.phone;
        member.enable = builder.enable;
        //todo role 구분해야함 일단 member로 지정
        member.authorities = builder.authorities.stream()
                .map(a -> Authority.createAuthority(member, Auth.valueOf(a)))
                .collect(Collectors.toList());
        return member;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public void modify(ModifyMember modifyMember) {
        this.password = modifyMember.getPassword();
        this.address = modifyMember.getAddress();
        this.name = modifyMember.getName();
        this.phone = modifyMember.getPhone();
        this.zipCode = modifyMember.getZipCode();
    }

    @lombok.Builder
    public static class Builder {

        private String email;
        private String password;
        private String name;
        private String address;
        private int zipCode;
        private String phone;
        private boolean enable;
        private List<String> authorities;

        public Member toEntity() {
            return Member.createMember(this);
        }
    }
}
