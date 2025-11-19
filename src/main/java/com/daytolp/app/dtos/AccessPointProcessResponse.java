package com.daytolp.app.dtos;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AccessPointProcessResponse {
    private List<String> idsSaves;
    private List<String> idsSkips;
}
