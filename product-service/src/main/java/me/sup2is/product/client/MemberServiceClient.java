package me.sup2is.product.client;

import me.sup2is.product.domain.dto.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "member-service", primary = false)
public interface MemberServiceClient {

    @PostMapping("/credential")
    MemberDto getMember(String email);

}
