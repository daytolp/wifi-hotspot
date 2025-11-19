package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.models.AccessPoint;
import org.springframework.data.domain.Page;

import java.util.List;

public interface AccessPointService {

    public AccessPointProcessResponse processAccessPoints(List<AccessPoint> accessPoints);

    public Page<AccessPointDTO>getAccessPoints(int page, int size);

    public AccessPointDTO getAccesPointById(String id);

    public Page<AccessPointDTO> getAccesPointByMunicipality(String municipality, int page, int size);

    public Page<AccessPointDTO> getAccessPointsOrderProximity(double latitude, double longitude, int page, int size);
}
