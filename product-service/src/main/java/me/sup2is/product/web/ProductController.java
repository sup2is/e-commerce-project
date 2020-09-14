package me.sup2is.product.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.dto.*;
import me.sup2is.product.service.MemberService;
import me.sup2is.product.service.ProductService;
import me.sup2is.product.web.dto.ProductModifyRequestDto;
import me.sup2is.product.web.dto.ProductRequestDto;
import me.sup2is.product.web.dto.ProductResponseDto;
import me.sup2is.web.JsonResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ProductController {

    private static final String TOKEN_PREFIX = "Bearer ";
    private final ProductService productService;
    private final MemberService memberService;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> register(@RequestBody @Valid ProductRequestDto productRequestDto,
                                                  BindingResult bindingResult,
                                                  HttpServletRequest request) {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        MemberDto member = memberService.getMember(email);

        productService.register(member.getMemberId(), productRequestDto.toEntity(), productRequestDto.getCategories());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @PutMapping("/stock")
    public ResponseEntity<JsonResult<?>> modifyStock(@RequestBody List<ProductStockDto> productStockDto) {
        productService.modifyStock(productStockDto);
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<JsonResult<?>> modify(@Valid @RequestBody ProductModifyRequestDto productModifyRequestDto,
                                                BindingResult bindingResult,
                                                @PathVariable Long productId,
                                                HttpServletRequest request) throws IllegalAccessException {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        MemberDto member = memberService.getMember(email);

        productService.modify(member.getMemberId(), productId, productModifyRequestDto.toProductModifyDto());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<JsonResult<?>> getProduct(@PathVariable Long productId){
        Product findProduct = productService.findOne(productId);
        return ResponseEntity.ok(new JsonResult<>(new ProductResponseDto(findProduct)));
    }


    private String getEmailByToken(String header) {
        String accessToken = extractTokenFromHeader(header);
        return extractEmailFromToken(accessToken);
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


}
