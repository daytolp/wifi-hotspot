package com.daytolp.app.dtos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AccessPointDTO {
    private String id;
    private String program;
    private Double latitude;
    private Double longitude;
    private String municipality;
}
