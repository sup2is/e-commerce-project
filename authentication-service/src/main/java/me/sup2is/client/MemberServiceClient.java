package me.sup2is.client;

import me.sup2is.client.dto.MemberDto;
import me.sup2is.web.JsonResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "member-service", primary = false)
public interface MemberServiceClient {

    @PostMapping("/credential")
    MemberDto getMember(String email);

}
