package com.daytolp.app.controllers;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.services.AccessPointService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/access-points")
public class AccessPointController {

    @Autowired
    private AccessPointService accessPointService;

    @GetMapping
    public ResponseEntity<Page<AccessPointDTO>> getAccessPoints(@RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {
        Page<AccessPointDTO> result = accessPointService.getAccessPoints(page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/by-id")
    public ResponseEntity<AccessPointDTO> getAccesPointById(@RequestParam String id) {
        AccessPointDTO result = accessPointService.getAccesPointById(id);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/proximity")
    public ResponseEntity<Page<AccessPointDTO>> getByProximity(@RequestParam double latitude,
                                                               @RequestParam double longitude, @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size) {

        Page<AccessPointDTO> result = accessPointService
                .getAccessPointsOrderProximity(latitude, longitude, page, size);
        return ResponseEntity.ok(result);
    }
}
