package me.sup2is.member.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.member.domain.dto.MemberRequestDto;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/member")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<JsonResult<?>> saveMember(
            @RequestBody MemberRequestDto memberRequestDto) {
        memberService.save(memberRequestDto.toEntity());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }


}
