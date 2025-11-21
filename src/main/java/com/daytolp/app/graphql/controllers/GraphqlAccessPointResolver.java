package com.daytolp.app.graphql.controllers;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.dtos.AccessPointPageDTO;
import com.daytolp.app.services.AccessPointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;


@Controller
public class GraphqlAccessPointResolver {

    @Autowired
    AccessPointService accessPointService;

    @QueryMapping
    public AccessPointPageDTO getAccessPoints(@Argument int page, @Argument int size) {
        Page<AccessPointDTO> accessPointPage = accessPointService.getAccessPoints(page, size);
        return mapToAccessPointPage(accessPointPage);
    }

    @QueryMapping
    public AccessPointDTO getAccessPointById(@Argument String id) {
        return accessPointService.getAccesPointById(id);
    }

    @QueryMapping
    public  AccessPointPageDTO getAccessPointByMunicipality(@Argument String municipality,
                                                            @Argument int page, @Argument int size) {
        Page<AccessPointDTO> accessPointPage = accessPointService.getAccesPointByMunicipality(municipality, page, size);
        return mapToAccessPointPage(accessPointPage);
    }

    @QueryMapping
    public  AccessPointPageDTO getAccessPointsOrderProximity( @Argument double latitude, @Argument double longitude,
                                                    @Argument int page, @Argument int size) {
        Page<AccessPointDTO> accessPointPage = accessPointService.getAccessPointsOrderProximity(latitude, longitude, page, size);
        return mapToAccessPointPage(accessPointPage);
    }


    private AccessPointPageDTO mapToAccessPointPage(Page<AccessPointDTO> page) {
        return AccessPointPageDTO.builder()
                .content(page.getContent())
                .totalElements(Long.valueOf(page.getTotalElements()).intValue())
                .totalPages(page.getTotalPages())
                .size(page.getSize())
                .number(page.getNumber())
                .first(page.isFirst())
                .last(page.isLast())
                .empty(page.isEmpty())
                .build();
    }
}
