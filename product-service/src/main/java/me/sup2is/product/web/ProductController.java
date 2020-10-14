package me.sup2is.product.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.product.domain.Product;
import me.sup2is.product.domain.dto.MemberDto;
import me.sup2is.product.domain.dto.ProductStockDto;
import me.sup2is.product.service.MemberService;
import me.sup2is.product.service.ProductSearchService;
import me.sup2is.product.service.ProductService;
import me.sup2is.product.web.dto.*;
import me.sup2is.web.JsonResult;
import org.springframework.data.domain.PageRequest;
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
    private final ProductSearchService productSearchService;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> register(@RequestBody @Valid ProductRequestDto productRequestDto,
                                                  BindingResult bindingResult,
                                                  HttpServletRequest request) {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        MemberDto member = memberService.getMember(email);

        Product product = productService.register(member.getMemberId(), productRequestDto.toEntity(), productRequestDto.getCategories());
        return ResponseEntity.ok(new JsonResult<>(new ProductResponseDto(product)));
    }

    @PutMapping("/stock")
    public ResponseEntity<JsonResult<?>> modifyStock(@RequestBody List<ProductStockModifyRequestDto> productStockDto) {
        //todo message 기반으로 변경
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

    @GetMapping("/search")
    public ResponseEntity<JsonResult<?>> getProducts(@RequestParam(defaultValue = "0") int pageNo,
                                                     @RequestParam(defaultValue = "5") int pageSize,
                                                     ProductSpecificationQueryDto productSpecificationQuery){

        PageRequest pageRequest = ProductPageRequestDto.createProductPageRequest(pageNo, pageSize);
        List<Product> findProducts = productSearchService.findAllByQuery(pageRequest, productSpecificationQuery.createQueryMap());
        return ResponseEntity.ok(new JsonResult<>(ProductResponseDto.createProductsResponseDto(findProducts)));
    }

    @GetMapping("/{productId}/stock")
    public ResponseEntity<JsonResult<?>> getProductStock(@PathVariable Long productId){
        Product product = productService.findOne(productId);
        return ResponseEntity.ok(new JsonResult<>(ProductStockDto.createProductStockDto(product)));
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
