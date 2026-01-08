package com.pragma.powerup.domain.spi;

import com.pragma.powerup.domain.model.TraceLogModel;

public interface ITraceExternalPort {
    void saveOrderTrace(TraceLogModel traceLogModel);
}