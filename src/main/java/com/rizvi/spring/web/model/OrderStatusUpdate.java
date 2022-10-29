package com.rizvi.spring.web.model;


import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OrderStatusUpdate extends BaseItem {

    @Builder
    public OrderStatusUpdate(UUID id, Integer version, OffsetDateTime createdDate, OffsetDateTime lastModifiedDate,
                             UUID orderId, OrderStatusEnum orderStatus){
        super(id, version,createdDate, lastModifiedDate);
        this.orderId = orderId;
        this.orderStatus = orderStatus;
    }

    private UUID orderId;
    private String customerRef;
    private OrderStatusEnum orderStatus;
}
