package me.sup2is.member.service;

import lombok.RequiredArgsConstructor;
import me.sup2is.member.domain.Authority;
import me.sup2is.member.repository.AuthorityRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
public class AuthorityService {

    private final AuthorityRepository authorityRepository;

    public void save(Authority authority) {
        authorityRepository.save(authority);
    }

}
