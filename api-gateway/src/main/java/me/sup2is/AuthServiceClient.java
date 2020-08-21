package me.sup2is;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "authentication-service", primary = false)
public interface AuthServiceClient {

    @PostMapping("/authenticate")
    User getMember(String accessToken);

}
