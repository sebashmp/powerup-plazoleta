package com.pragma.powerup.infrastructure.out.feign.adapter;

import com.pragma.powerup.domain.model.TraceLogModel;
import com.pragma.powerup.domain.spi.ITraceExternalPort;
import com.pragma.powerup.infrastructure.out.feign.ITraceFeignClient;
import com.pragma.powerup.infrastructure.out.feign.dto.TraceRequestDto;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TraceExternalAdapter implements ITraceExternalPort {
    private final ITraceFeignClient traceFeignClient;

    @Override
    public void saveOrderTrace(TraceLogModel traceLogModel) {
        TraceRequestDto request = new TraceRequestDto(
                traceLogModel.getOrderId(),
                traceLogModel.getClientId(),
                traceLogModel.getPreviousStatus(),
                traceLogModel.getNewStatus(),
                traceLogModel.getEmployeeId()
        );
        traceFeignClient.saveTrace(request);
    }
}