package me.sup2is.member.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.sup2is.member.client.dto.MemberDto;
import me.sup2is.member.service.MemberService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CredentialController {

    private final MemberService memberService;

    @PostMapping("/credential")
    public MemberDto getMember(@RequestBody @NonNull String email) {
        return MemberDto.createMemberDto(memberService.findByEmail(email));
    }

}