package me.sup2is.member.web;

import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import me.sup2is.member.domain.dto.MemberRequestDto;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.JsonResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> saveMember(
            @RequestBody @Valid MemberRequestDto memberRequestDto, BindingResult bindingResult) throws DuplicateMemberException {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        memberService.save(memberRequestDto.toEntity());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }
}
