package com.daytolp.app.dtos;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessPointDTO {
    private String id;
    private String program;
    private Double latitude;
    private Double longitude;
    private String municipality;
}
