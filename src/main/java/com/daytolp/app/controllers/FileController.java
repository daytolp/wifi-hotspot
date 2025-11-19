package com.daytolp.app.controllers;

import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.services.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequestMapping("/upload-excel")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping
    public ResponseEntity<AccessPointProcessResponse> receiveFile(@RequestParam(name = "file") MultipartFile multipartFile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileService.processFile(multipartFile));
    }
}
