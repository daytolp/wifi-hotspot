package com.daytolp.app.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AccessPointPageDTO {
    private List<AccessPointDTO> content;
    private Integer totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;
    private boolean first;
    private boolean last;
    private boolean empty;
}