package me.sup2is.member.service;

import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import me.sup2is.member.client.dto.MemberDto;
import me.sup2is.member.domain.Authority;
import me.sup2is.member.domain.Member;
import me.sup2is.member.domain.dto.ModifyMember;
import me.sup2is.member.exception.MemberNotFoundException;
import me.sup2is.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityService authorityService;
    private final CachedMemberService cachedMemberService;

    public void save(Member member) throws DuplicateMemberException {
        if(memberRepository.findByEmail(member.getEmail()).isPresent())
            throw new DuplicateMemberException(member.getEmail() + " is duplicated");

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

    @Transactional(readOnly = true)
    public MemberDto findMemberDto(String email) {
        Optional<MemberDto> cachedMember = cachedMemberService.findMember(email);
        return cachedMember.orElseGet(() -> {
            MemberDto memberDto = MemberDto.createMemberDto(this.findByEmail(email));
            cachedMemberService.caching(memberDto);
            return memberDto;
        });
    }

    public void modify(String email, ModifyMember modifyMember) {
        Member member = findByEmail(email);
        member.modify(modifyMember);
        member.encryptPassword(passwordEncoder);
        cachedMemberService.evictMember(member.getEmail());
    }
}
