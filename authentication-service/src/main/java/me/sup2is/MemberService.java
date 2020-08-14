package me.sup2is;

import me.sup2is.web.MemberInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Arrays;
import java.util.List;

public class MemberService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        if (StringUtils.isEmpty(email)) {
            throw new UsernameNotFoundException(email);
        }

        //mock 객체 사용
        MemberInfo findMember = new MemberInfo(email, "password");
        List<GrantedAuthority> grantedAuthorities = Arrays.asList(new SimpleGrantedAuthority("MEMBER"));

        return new User(findMember.getEmail(), findMember.getPassword(), grantedAuthorities);
    }

}
