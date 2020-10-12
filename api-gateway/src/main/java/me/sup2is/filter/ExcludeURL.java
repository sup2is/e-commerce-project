package me.sup2is.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpMethod;

@Getter
@AllArgsConstructor
public enum ExcludeURL {

    AUTH_TOKEN("/auth/token", HttpMethod.POST),
    API_MEMBER("/api/member", HttpMethod.POST),
    API_PRODUCT_SEARCH("/api/product/search", HttpMethod.GET),
    API_PRODUCT_ID("/api/product/\\d+", HttpMethod.GET);

    private String url;
    private HttpMethod method;

}
