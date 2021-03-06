package me.sup2is.member.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Authority implements GrantedAuthority {

    @Id
    @Column(name = "auth_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private Auth auth;

    @Override
    public String getAuthority() {
        return this.auth.name();
    }

    public static Authority createAuthority(Member member, Auth auth) {
        Authority authority = new Authority();
        authority.auth = auth;
        authority.member = member;
        return authority;
    }
}
