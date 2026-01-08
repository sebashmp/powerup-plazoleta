package com.pragma.powerup.infrastructure.out.feign;

import com.pragma.powerup.infrastructure.out.feign.dto.TraceRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "trace-service", url = "localhost:8084/trace")
public interface ITraceFeignClient {
    @PostMapping("/")
    void saveTrace(@RequestBody TraceRequestDto traceRequestDto);
}