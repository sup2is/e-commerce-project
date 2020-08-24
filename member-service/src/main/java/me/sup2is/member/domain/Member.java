package me.sup2is.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

    private String zipCode;

    private String phone;

    @OneToMany(mappedBy = "member")
    private List<Authority> authorities = new ArrayList<>();

    private boolean enable;
}
