package me.sup2is.filter;

import lombok.RequiredArgsConstructor;
import me.sup2is.client.MemberServiceClient;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.service.MemberService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
@Component
public class JwtAuthenticateFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final MemberService memberService;
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final List<ExcludeURL> EXCLUDE_URL =
            Collections.unmodifiableList(
                    Arrays.asList(
                            ExcludeURL.API_MEMBER,
                            ExcludeURL.API_PRODUCT_ID,
                            ExcludeURL.API_PRODUCT_SEARCH,
                            ExcludeURL.AUTH_TOKEN
                    ));

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {

        String accessToken = extractTokenFromHeader(httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION));
        String email = extractEmailFromToken(accessToken);

        if(checkCurrentSecurityContext(email)) {
            SecurityContextHolder.clearContext();
        }

        if(validate(accessToken, email)){
            User user = memberService.getMember(email).toUser();
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(user, accessToken, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private boolean checkCurrentSecurityContext(String email) {
        return SecurityContextHolder.getContext().getAuthentication() != null
                && !getPrincipal().getUsername().equals(email);
    }

    private User getPrincipal() {
        return (User) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
    }

    private boolean validate(String accessToken, String email) {
        return email != null
            && SecurityContextHolder.getContext().getAuthentication() == null
            && jwtTokenUtil.validateToken(accessToken);
    }

    private String extractEmailFromToken(String token) {
        String username;
        try {
            username = jwtTokenUtil.getIdFromToken(token);
        }catch (Exception e) {
            throw new BadCredentialsException("Invalid Token");
        }
        return username;
    }

    private String extractTokenFromHeader(String requestTokenHeader) {
        if (requestTokenHeader != null && requestTokenHeader.startsWith(TOKEN_PREFIX)) {
            return  requestTokenHeader.substring(7);
        } else {
            throw new BadCredentialsException("Token is required");
        }
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return EXCLUDE_URL.stream().anyMatch(exclude ->
                (exclude.getUrl().equalsIgnoreCase(request.getServletPath()) ||
                    request.getServletPath().matches(exclude.getUrl())) &&
                exclude.getMethod().toString().equals(request.getMethod()));
    }
}
