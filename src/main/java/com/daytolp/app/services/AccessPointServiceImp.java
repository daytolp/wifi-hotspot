package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.models.AccessPoint;
import com.daytolp.app.repositories.AccessPointRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AccessPointServiceImp implements AccessPointService {

    @Autowired
    private AccessPointRepository accessPointRepository;

    @Override
    @Transactional
    public AccessPointProcessResponse processAccessPoints(List<AccessPoint> accessPoints) {
        List<String> ids = accessPoints.stream()
                .map(AccessPoint::getId)
                .collect(Collectors.toList());

        List<String> existingIds = accessPointRepository.findByIdIn(ids);
        List<AccessPoint> newAccessPoints = accessPoints.stream()
                .filter(ap -> !existingIds.contains(ap.getId()))
                .collect(Collectors.toList());
        accessPointRepository.saveAll(newAccessPoints);

        List<String> savedIds = newAccessPoints.stream()
                .map(AccessPoint::getId)
                .collect(Collectors.toList());

        return AccessPointProcessResponse.builder()
                .idsSaves(savedIds)
                .idsSkips(existingIds)
                .build();
    }

    @Override
    public Page<AccessPoint> getAccessPoints(int page, int size) {
        return null;
    }

    @Override
    public Optional<AccessPoint> getAccesPointById(String id) {
        return Optional.empty();
    }

    @Override
    public Page<AccessPoint> getAccesPointByMunicipality(String municipality) {
        return null;
    }

    @Override
    public Page<AccessPoint> getAccesPointOrderProximity() {
        return null;
    }
}
