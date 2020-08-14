package me.sup2is;

import me.sup2is.web.JsonResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {


    @GetMapping("/")
    public ResponseEntity<JsonResult> generateJwtToken() {

    }

}
