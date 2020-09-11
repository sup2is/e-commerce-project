package me.sup2is.order.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ModifyOrderRequestDto {

    private String address;

    List<ModifyOrderItemRequestDto> modifyOrderItems;
}
