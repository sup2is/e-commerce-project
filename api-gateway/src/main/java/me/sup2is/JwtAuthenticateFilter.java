package me.sup2is;

import com.netflix.zuul.filters.ZuulServletFilter;
import lombok.RequiredArgsConstructor;
import me.sup2is.jwt.JwtTokenUtil;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@Component
public class JwtAuthenticateFilter extends ZuulServletFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final AuthServiceClient authServiceClient;
    private static final String TOKEN_PREFIX = "Bearer ";

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        String accessToken = extractTokenFromHeader(((HttpServletRequest)servletRequest).getHeader(HttpHeaders.AUTHORIZATION));
        String email = extractIdFromToken(accessToken);

        if(email != null
            && SecurityContextHolder.getContext().getAuthentication() == null
            && jwtTokenUtil.validateToken(accessToken)){

            Member member = authServiceClient.getMember(accessToken);
            UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(member, accessToken, member.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String extractIdFromToken(String token) {
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
}
