package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.models.AccessPoint;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface AccessPointService {

    public AccessPointProcessResponse processAccessPoints(List<AccessPoint> accessPoints);

    public Page<AccessPoint> getAccessPoints(int page, int size);

    public Optional<AccessPoint> getAccesPointById(String id);

    public Page<AccessPoint> getAccesPointByMunicipality(String municipality);

    public Page<AccessPoint> getAccesPointOrderProximity();
}
