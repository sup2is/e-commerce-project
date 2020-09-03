package me.sup2is.member.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.member.domain.Authority;
import me.sup2is.member.domain.Member;
import me.sup2is.member.exception.MemberNotFoundException;
import me.sup2is.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static java.util.stream.Collectors.toList;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;

    public void save(Member member) {
        for (Authority authority : member.getAuthorities())
            authorityService.save(authority);

        member.encryptPassword(passwordEncoder);
        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public Member findOne(Long id) {
        return memberRepository.findById(id).orElseThrow(
                () -> new MemberNotFoundException("member not found"));
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) throws UsernameNotFoundException {
        return memberRepository.findByEmail(email).orElseThrow(
                () -> new MemberNotFoundException("member not found"));
    }
}
