package me.sup2is.config;

import lombok.RequiredArgsConstructor;
import me.sup2is.filter.ExceptionHandlerFilter;
import me.sup2is.filter.JwtAuthenticateFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.stereotype.Component;

@EnableWebSecurity
@Component
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ExceptionHandlerFilter exceptionHandlerFilter;
    private final JwtAuthenticateFilter jwtAuthenticateFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/member").permitAll()
                .antMatchers(HttpMethod.GET, "/api/product/search").permitAll()
                .antMatchers(HttpMethod.GET, "/api/product/{\\d+}").permitAll()
                .antMatchers("/auth/**").permitAll()

                .antMatchers(HttpMethod.POST, "/api/product").hasRole("SELLER")
                .antMatchers(HttpMethod.POST, "/api/product/stock").hasRole("SELLER")
                .antMatchers(HttpMethod.POST, "/api/product/category").hasRole("SELLER")
                .antMatchers(HttpMethod.PUT, "/api/product/{\\d+}").hasRole("SELLER")

                .antMatchers("/api/**").hasAnyRole("MEMBER", "SELLER")
            .and()
                .csrf().disable()
                .formLogin().disable()
                .cors().disable();

        http.addFilterBefore(exceptionHandlerFilter, SecurityContextPersistenceFilter.class);
        http.addFilterBefore(jwtAuthenticateFilter, UsernamePasswordAuthenticationFilter.class);
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}

