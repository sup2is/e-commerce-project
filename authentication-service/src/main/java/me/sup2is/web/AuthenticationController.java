package me.sup2is.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtAuthenticationGenerator jwtAuthenticationGenerator;
    private final MemberService memberService;

    @PostMapping("/token")
    public ResponseEntity<JsonResult<AuthenticationResponseDto>> generateJwtToken
            (@RequestBody AuthenticationRequestDto authenticationRequestDto) {
        UserDetails userDetails = memberService.loadUserByUsername(authenticationRequestDto.getUsername());
        AuthenticationResponseDto jwtAuthenticationFromUserDetails =
                jwtAuthenticationGenerator.createJwtAuthenticationFromUserDetails(userDetails);
        return ResponseEntity.ok(new JsonResult<>(jwtAuthenticationFromUserDetails));
    }

}
