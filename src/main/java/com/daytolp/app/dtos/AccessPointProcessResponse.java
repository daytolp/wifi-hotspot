package com.daytolp.app.dtos;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccessPointProcessResponse {
    private String status;
    private String message;
}
