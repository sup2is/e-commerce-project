package me.sup2is.order.client;

import me.sup2is.order.domain.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "member-service", primary = false)
public interface MemberServiceClient {

    @PostMapping("/credential")
    MemberDto getMember(String email);

}
