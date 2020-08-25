package me.sup2is.web;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    private final JwtAuthenticationGenerator jwtAuthenticationGenerator;
    private final MemberServiceClient memberServiceClient;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/token")
    public ResponseEntity<JsonResult<AuthenticationResponseDto>> generateJwtToken
            (@RequestBody AuthenticationRequestDto authenticationRequestDto) {
        User user = memberServiceClient.getMember(authenticationRequestDto.getUsername()).getData();
        if(!passwordEncoder.matches(authenticationRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Password not matched");
        }
        AuthenticationResponseDto jwtAuthenticationFromUserDetails =
                jwtAuthenticationGenerator.createJwtAuthenticationFromUserDetails(user);
        return ResponseEntity.ok(new JsonResult<>(jwtAuthenticationFromUserDetails));
    }
}
