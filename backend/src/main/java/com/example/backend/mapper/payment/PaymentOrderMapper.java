package com.example.backend.mapper.payment;

import com.example.backend.dto.payment.PaymentOrderDto;
import com.example.backend.entity.payment.PaymentOrder;
import org.mapstruct.*;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE, componentModel = MappingConstants.ComponentModel.SPRING)
public interface PaymentOrderMapper {
    PaymentOrder toEntity(PaymentOrderDto paymentOrderDto);

    PaymentOrderDto toDto(PaymentOrder paymentOrder);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    PaymentOrder partialUpdate(PaymentOrderDto paymentOrderDto, @MappingTarget PaymentOrder paymentOrder);
}