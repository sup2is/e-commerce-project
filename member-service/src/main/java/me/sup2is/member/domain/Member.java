package me.sup2is.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
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
        return builder.build();
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

        private Member build() {
            Member member = new Member();
            member.email = this.email;
            member.password = this.password;
            member.name = this.name;
            member.address = this.address;
            member.zipCode = this.zipCode;
            member.phone = this.phone;
            return member;
        }
    }
}
