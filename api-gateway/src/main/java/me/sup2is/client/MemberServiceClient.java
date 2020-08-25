package me.sup2is.client;

import me.sup2is.web.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "member-service", primary = false)
public interface MemberServiceClient {

    @PostMapping("/member")
    JsonResult<User> getMember(String email);

}
