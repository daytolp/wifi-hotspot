package com.daytolp.app.services;

import com.daytolp.app.dtos.AccessPointProcessResponse;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    public AccessPointProcessResponse processFile(MultipartFile multipartFile);
}
