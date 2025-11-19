package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.exceptions.NotFoundException;
import com.daytolp.app.models.AccessPoint;
import com.daytolp.app.repositories.AccessPointRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccessPointServiceImp implements AccessPointService {

    @Autowired
    private AccessPointRepository accessPointRepository;

    @Autowired
    private ModelMapper modelMapper;

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
    public Page<AccessPointDTO> getAccessPoints(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccessPoint> entities = accessPointRepository.findAll(pageable);
        return entities.map(ap -> modelMapper.map(ap, AccessPointDTO.class));
    }

    @Override
    public AccessPointDTO getAccesPointById(String id) {
        AccessPoint accessPoint = accessPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Punto de acceso no encontrado con id: " + id));
        return modelMapper.map(accessPoint, new TypeToken<AccessPointDTO>() {;
        }.getType());
    }

    @Override
    public Page<AccessPointDTO> getAccesPointByMunicipality(String municipality, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccessPoint> entities = accessPointRepository.findByMunicipality(municipality, pageable);
        return entities.map(ap -> modelMapper.map(ap, AccessPointDTO.class));
    }

    @Override
    public Page<AccessPointDTO> getAccessPointsOrderProximity(double latitude, double longitude,
            int page, int size) {

        if (latitude < -90 || latitude > 90) {
            throw new IllegalArgumentException("Latitud debe estar entre -90 y 90");
        }
        if (longitude < -180 || longitude > 180) {
            throw new IllegalArgumentException("Longitud debe estar entre -180 y 180");
        }

        Pageable pageable = PageRequest.of(page, size);

        Page<AccessPoint> accessPoints = accessPointRepository.findAllOrderedByProximityWithDistance(latitude, longitude, pageable);
        return accessPoints.map(ap -> modelMapper.map(ap, AccessPointDTO.class));
    }

}
