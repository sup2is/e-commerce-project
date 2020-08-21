package me.sup2is;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "authentication-service", primary = false)
public interface AuthServiceClient {

    @PostMapping("/authenticate")
    Member getMember(String accessToken);

}
