package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.models.AccessPoint;
import com.daytolp.app.repositories.AccessPointRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AccessPointServiceImpTest {

    @Mock
    private AccessPointRepository accessPointRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AccessPointServiceImp accessPointService;

    private AccessPoint accessPoint1;
    private AccessPoint accessPoint2;
    private AccessPointDTO accessPointDTO1;
    private AccessPointDTO accessPointDTO2;

    @BeforeEach
    void setUp() {
        accessPoint1 = new AccessPoint();
        accessPoint1.setId("1");
        accessPoint1.setProgram("WiFi CDMX");
        accessPoint1.setLatitude(19.4326);
        accessPoint1.setLongitude(-99.1332);
        accessPoint1.setMunicipality("Cuauhtémoc");

        accessPoint2 = new AccessPoint();
        accessPoint2.setId("2");
        accessPoint2.setProgram("WiFi Público");
        accessPoint2.setLatitude(19.4978);
        accessPoint2.setLongitude(-99.1269);
        accessPoint2.setMunicipality("Miguel Hidalgo");

        accessPointDTO1 = new AccessPointDTO();
        accessPointDTO1.setId("1");
        accessPointDTO1.setProgram("WiFi CDMX");
        accessPointDTO1.setLatitude(19.4326);
        accessPointDTO1.setLongitude(-99.1332);
        accessPointDTO1.setMunicipality("Cuauhtémoc");

        accessPointDTO2 = new AccessPointDTO();
        accessPointDTO2.setId("2");
        accessPointDTO2.setProgram("WiFi Público");
        accessPointDTO2.setLatitude(19.4978);
        accessPointDTO2.setLongitude(-99.1269);
        accessPointDTO2.setMunicipality("Miguel Hidalgo");
    }

    @Test
    void getAccessPointsTest() {
        int page = 0;
        int size = 10;

        Pageable pageable = PageRequest.of(page, size);
        List<AccessPoint> accessPoints = Arrays.asList(accessPoint1, accessPoint2);
        Page<AccessPoint> accessPointPage = new PageImpl<>(accessPoints, pageable, accessPoints.size());
        when(accessPointRepository.findAll(pageable)).thenReturn(accessPointPage);
        when(modelMapper.map(accessPoint1, AccessPointDTO.class)).thenReturn(accessPointDTO1);
        when(modelMapper.map(accessPoint2, AccessPointDTO.class)).thenReturn(accessPointDTO2);

        Page<AccessPointDTO> result = accessPointService.getAccessPoints(page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals("1", result.getContent().get(0).getId());
        assertEquals("2", result.getContent().get(1).getId());

        verify(accessPointRepository, times(1)).findAll(pageable);
        verify(modelMapper, times(2)).map(any(AccessPoint.class), eq(AccessPointDTO.class));
    }

    @Test
    void getAccesPointByIdExceptionTest() {
        String id = null;
        String messageExpected = "El ID del punto de acceso no debe ser nulo o vacío";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accessPointService.getAccesPointById(null);
        });
        String message = exception.getMessage();
        assertNotNull(message);
        assertEquals(messageExpected, message);
    }

    @Test
    void getAccesPointByIdTest() {
        when(accessPointRepository.findById(anyString())).thenReturn(Optional.of(accessPoint1));
        when(modelMapper.map(accessPoint1, AccessPointDTO.class)).thenReturn(accessPointDTO1);

        AccessPointDTO accessPoint = accessPointService.getAccesPointById("1");

        assertNotNull(accessPoint);
        assertEquals("WiFi CDMX", accessPoint.getProgram());
        assertEquals("1", accessPoint.getId());

        verify(accessPointRepository, times(1)).findById(anyString());
        verify(modelMapper, times(1)).map(any(AccessPoint.class), eq(AccessPointDTO.class));
    }

    @Test
    void getAccessPointsOrderProximitySuccessTest(){
        int page = 0, size = 10;
        double latitude = 19.4326,  longitude = -99.1332;

        Pageable pageable = PageRequest.of(page, size);
        List<AccessPoint> accessPoints = Arrays.asList(accessPoint1, accessPoint2);
        Page<AccessPoint> accessPointPage = new PageImpl<>(accessPoints, pageable, accessPoints.size());
        when(accessPointRepository.findAllOrderedByProximityWithDistance(latitude, longitude, pageable)).thenReturn(accessPointPage);
        when(modelMapper.map(accessPoint1, AccessPointDTO.class)).thenReturn(accessPointDTO1);
        when(modelMapper.map(accessPoint2, AccessPointDTO.class)).thenReturn(accessPointDTO2);

        Page<AccessPointDTO> result = accessPointService.getAccessPointsOrderProximity(latitude, longitude, page, size);

        assertNotNull(result);
        assertEquals(2, result.getContent().size());
        assertEquals(2, result.getTotalElements());
        assertEquals("Cuauhtémoc", result.getContent().get(0).getMunicipality());
        assertEquals("Miguel Hidalgo", result.getContent().get(1).getMunicipality());

    }


    @Test
    void getAccessPointsOrderProximityExceptionTest() {
        double latitude = -91,  longitude = -99.1332;
        int page = 0, size = 10;
        String messageExpected = "Latitud debe estar entre -90 y 90";

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            accessPointService.getAccessPointsOrderProximity(latitude, longitude, page, size);
        });
        String message = exception.getMessage();
        assertNotNull(message);
        assertEquals(messageExpected, message);
    }
}
