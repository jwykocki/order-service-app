package com.jw.dto.response;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Setter
//REVIEW-VINI: Why do we need this class? could we return a list of OrderResponse?
public class OrdersResponse {
    List<OrderResponse> orders;
}
