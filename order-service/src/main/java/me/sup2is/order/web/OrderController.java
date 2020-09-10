package me.sup2is.order.web;

import lombok.RequiredArgsConstructor;
import me.sup2is.jwt.JwtTokenUtil;
import me.sup2is.order.domain.dto.MemberDto;
import me.sup2is.order.domain.dto.ModifyOrderItemRequestDto;
import me.sup2is.order.domain.dto.ModifyOrderRequestDto;
import me.sup2is.order.domain.dto.OrderRequestDto;
import me.sup2is.order.exception.CancelFailureException;
import me.sup2is.order.exception.OrderNotFoundException;
import me.sup2is.order.exception.OutOfStockException;
import me.sup2is.order.service.MemberService;
import me.sup2is.order.service.OrderService;
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
public class OrderController {

    private static final String TOKEN_PREFIX = "Bearer ";
    private final OrderService orderService;
    private final JwtTokenUtil jwtTokenUtil;
    private final MemberService memberService;

    @PostMapping("/")
    public ResponseEntity<JsonResult<?>> order(@RequestBody @Valid OrderRequestDto orderRequestDto,
                                               BindingResult bindingResult,
                                               HttpServletRequest request) throws OutOfStockException {
        if(bindingResult.hasErrors())
            return new ResponseEntity<>(new JsonResult<>(bindingResult.getFieldErrors()), HttpStatus.BAD_REQUEST);

        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        MemberDto member = memberService.getMember(email);
        orderService.order(member.getMemberId(), orderRequestDto.toEntity());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @PostMapping("{orderId}/cancel")
    public ResponseEntity<JsonResult<?>> cancel(@PathVariable Long orderId, HttpServletRequest request)
            throws CancelFailureException, OrderNotFoundException, IllegalAccessException {

        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        MemberDto member = memberService.getMember(email);
        orderService.cancel(orderId, member.getMemberId());
        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
    }

    @PutMapping("{orderId}")
    public ResponseEntity<JsonResult<?>> modify(@PathVariable Long orderId,
                                                @RequestBody ModifyOrderRequestDto modifyOrderRequestDto,
                                                HttpServletRequest request)
            throws OrderNotFoundException, IllegalAccessException, OutOfStockException {

        String email = getEmailByToken(request.getHeader(HttpHeaders.AUTHORIZATION));
        MemberDto member = memberService.getMember(email);

        orderService.modify(member.getMemberId(),
                orderId,
                modifyOrderRequestDto.getAddress(),
                ModifyOrderItemRequestDto.toModifyOrderItems(modifyOrderRequestDto.getModifyOrderItems()));

        return ResponseEntity.ok(new JsonResult<>(JsonResult.Result.SUCCESS));
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
