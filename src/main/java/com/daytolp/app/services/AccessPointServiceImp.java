package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.exceptions.NotFoundException;
import com.daytolp.app.models.AccessPoint;
import com.daytolp.app.repositories.AccessPointRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementación del servicio de gestión de puntos de acceso WiFi.
 */
@Service
public class AccessPointServiceImp implements AccessPointService {

    @Autowired
    private AccessPointRepository accessPointRepository;

    @Autowired
    private ModelMapper modelMapper;

    /**
     * Obtiene una lista paginada de todos los puntos de acceso WiFi.
     *
     * @param page Número de página a recuperar (basado en cero, primera página = 0)
     * @param size Cantidad de elementos por página
     * @return Objeto Page que contiene una página de AccessPointDTOs
     */
    @Override
    public Page<AccessPointDTO> getAccessPoints(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<AccessPoint> entities = accessPointRepository.findAll(pageable);
        return entities.map(ap -> modelMapper.map(ap, AccessPointDTO.class));
    }

    /**
     * Consulta la información completa de un punto de acceso WiFi específico dado su ID.
     *
     * @param id Identificador único del punto de acceso a buscar. No debe ser null o vacío.
     * @return Objeto AccessPointDTO con toda la información del punto de acceso.
     */
    @Override
    public AccessPointDTO getAccesPointById(String id) {
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("El ID del punto de acceso no debe ser nulo o vacío");
        }
        AccessPoint accessPoint = accessPointRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Punto de acceso no encontrado con id: " + id));
        return modelMapper.map(accessPoint, AccessPointDTO.class);
    }

    /**
     * Obtiene una lista paginada de puntos de acceso WiFi filtrados por alcaldía.
     *
     * @param municipality Nombre de la alcaldía o municipio a filtrar. No debe ser null o vacío.
     * @param page Número de página a recuperar (basado en cero, primera página = 0)
     * @param size Cantidad de elementos por página
     * @return Objeto Page que contiene una página de AccessPointDTOs
     */
    @Override
    public Page<AccessPointDTO> getAccesPointByMunicipality(String municipality, int page, int size) {
        if (municipality == null || municipality.isEmpty()) {
            throw new IllegalArgumentException("El nombre del municipio no debe ser nulo o vacío");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<AccessPoint> entities = accessPointRepository.findByMunicipality(municipality, pageable);
        return entities.map(ap -> modelMapper.map(ap, AccessPointDTO.class));
    }

    /**
     * Obtiene una lista paginada de puntos de acceso WiFi ordenados por proximidad a una coordenada dada.
     *
     * @param latitude Latitud de la coordenada de referencia en grados decimales.
     *                 Debe estar en el rango [-90, 90].
     * @param longitude Longitud de la coordenada de referencia en grados decimales.
     *                  Debe estar en el rango [-180, 180].
     * @param page Número de página a recuperar (basado en cero, primera página = 0)
     * @param size Cantidad de elementos por página
     * @return Objeto Page que contiene una página de AccessPointDTOs ordenados por proximidad
     *
     */
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
