package me.sup2is.member.web;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.JsonResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CredentialController {

    private final MemberService memberService;

    @PostMapping("/credential")
    public ResponseEntity<JsonResult<?>> getMember(@RequestBody @NonNull String email) {
        return ResponseEntity.ok(new JsonResult<>(memberService.loadUserByUsername(email)));
    }

}