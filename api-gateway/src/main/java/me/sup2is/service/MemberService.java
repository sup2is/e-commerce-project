package me.sup2is.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.client.MemberServiceClient;
import me.sup2is.client.dto.MemberDto;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberServiceClient memberServiceClient;
    private final CachedMemberService cachedMemberService;

    public MemberDto getMember(String email) {
        Optional<MemberDto> cachedMember = cachedMemberService.findMember(email);
        return cachedMember.orElseGet(() -> {
            MemberDto memberDto = memberServiceClient.getMember(email);
            return memberDto;
        });
    }
}
