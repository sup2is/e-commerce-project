package me.sup2is.product.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import me.sup2is.product.domain.dto.MemberDto;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CachedMemberService {

    private final ObjectMapper objectMapper;
    private final HashOperations<String, String, MemberDto> memberDtoHashOperations;
    private static final String KEY_PREFIX = "member:";
    private static final String HASH_KEY = "member";

    public Optional<MemberDto> findMember(String email) {
        return Optional.ofNullable(objectMapper.convertValue(
                memberDtoHashOperations.get(KEY_PREFIX + email, HASH_KEY)
                , MemberDto.class));
    }

    public void caching(MemberDto member) {
        memberDtoHashOperations.put(KEY_PREFIX + member.getEmail(), HASH_KEY, member);
    }
}
