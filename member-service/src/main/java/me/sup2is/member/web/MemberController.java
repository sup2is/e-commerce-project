package me.sup2is.member.web;

import javassist.bytecode.DuplicateMemberException;
import lombok.RequiredArgsConstructor;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.member.domain.Member;
import me.sup2is.member.domain.dto.MemberModifyRequestDto;
import me.sup2is.member.domain.dto.MemberRequestDto;
import me.sup2is.member.domain.dto.MemberResponseDto;
import me.sup2is.member.service.MemberService;
import me.sup2is.web.JsonResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class MemberController {

    private static final String TOKEN_PREFIX = "Bearer ";
    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> saveMember(
            @RequestBody @Valid MemberRequestDto memberRequestDto, BindingResult bindingResult) throws DuplicateMemberException {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        memberService.save(memberRequestDto.toEntity());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @PutMapping("/")
    public ResponseEntity<JsonResult<?>> modify(@RequestBody @Valid MemberModifyRequestDto memberRequestDto,
                                                BindingResult bindingResult,
                                                HttpServletRequest request) {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));

        memberService.modify(email, memberRequestDto.toModifyMember());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @GetMapping("/")
    public ResponseEntity<JsonResult<?>> getMember(HttpServletRequest request) {
        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        Member member = memberService.findByEmail(email);
        return ResponseEntity.ok(new JsonResult<>(MemberResponseDto.createMemberResponseDto(member)));
    }


    private String getEmailByToken(String header) {
        String accessToken = extractTokenFromHeader(header);
        return extractEmailFromToken(accessToken);
    }

    private String extractEmailFromToken(String token) {
        String username;
        try {
            username = jwtTokenUtil.getIdFromToken(token);
        }catch (Exception e) {
            throw new BadCredentialsException("Invalid Token");
        }
        return username;
    }

    private String extractTokenFromHeader(String requestTokenHeader) {
        if (requestTokenHeader != null && requestTokenHeader.startsWith(TOKEN_PREFIX)) {
            return  requestTokenHeader.substring(7);
        } else {
            throw new BadCredentialsException("Token is required");
        }
    }

}
