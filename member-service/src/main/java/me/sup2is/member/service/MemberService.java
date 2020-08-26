package me.sup2is.member.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.member.domain.Member;
import me.sup2is.member.exception.MemberNotFoundException;
import me.sup2is.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(Member member) {
        member.encryptPassword(passwordEncoder);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findOne(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("member not found"));
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        //email 받아서 user객체로 보내야함
        Member member = memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberNotFoundException("member not found"));

        User user = new User(member.getEmail()
                , member.getPassword()
                , member.getAuthorities());

        return user;
    }
}
