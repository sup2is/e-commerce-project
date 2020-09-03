package me.sup2is.member.domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class)
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
        Member member = builder.build();

        //todo role 구분해야함 일단 member로 지정
        member.authorities = Arrays.asList(Authority.createAuthority(member, Auth.MEMBER));

        return member;
    }

    public void encryptPassword(PasswordEncoder passwordEncoder) {
        this.password = passwordEncoder.encode(this.password);
    }

    public static class Builder {

        private String email;
        private String password;
        private String name;
        private String address;
        private int zipCode;
        private String phone;
        private boolean enable;
        private List<String> authorities;

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder password(String password) {
            this.password = password;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public Builder zipCode(int zipCode) {
            this.zipCode = zipCode;
            return this;
        }

        public Builder phone(String phone) {
            this.phone = phone;
            return this;
        }

        public Builder enable(boolean enable) {
            this.enable = enable;
            return this;
        }

        public Builder authorities(List<String> authorities) {
            this.authorities = authorities;
            return this;
        }

        private Member build() {
            Member member = new Member();
            member.email = this.email;
            member.password = this.password;
            member.name = this.name;
            member.address = this.address;
            member.zipCode = this.zipCode;
            member.phone = this.phone;
            member.enable = this.enable;
            member.authorities = this.authorities.stream()
                    .map(a -> Authority.createAuthority(member, Auth.valueOf(a)))
                    .collect(Collectors.toList());
            return member;
        }
    }
}
