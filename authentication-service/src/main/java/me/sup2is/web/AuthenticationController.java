package me.sup2is.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.jwt.JwtTokenUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthenticationController {

    @GetMapping("/auth")
    public ResponseEntity<JsonResult> generateJwtToken(@RequestBody MemberDto memberDto) {
        return null;
    }

}
