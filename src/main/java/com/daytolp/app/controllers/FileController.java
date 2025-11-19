package com.daytolp.app.controllers;

import com.daytolp.app.dtos.AccessPointProcessResponse;
import com.daytolp.app.services.FileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Controlador REST para subir archivos Excel con puntos de acceso WiFi.
 */
@Slf4j
@RestController
@RequestMapping("/upload-excel")
@Tag(name = "Carga de Archivo", description = "Operaciones de archivos Excel con puntos de acceso WiFi")
public class FileController {

    @Autowired
    private FileService fileService;

    /**
     * Registra puntos de acceso de acceso apartir de un archivo Excel (.xlsx).
     * @param multipartFile Objeto MultipartFile con el archivo Excel cargado
     * @return página con DTOs y metadatos de paginación
     */
    @Operation(summary = "Carga de puntos de aceso WIFI desde archivo Excel (.xlsx)",
            description = "Retorna un objeto con los puntos de acceso WiFi cargados y omitidos.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                    description = "Archivo procesado exitosamente",
                    content = @Content(mediaType = "application/json",
                                       schema = @Schema(implementation = AccessPointProcessResponse.class))),
            @ApiResponse(responseCode = "400", description = "Archivo inválido o error en el procesamiento"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<AccessPointProcessResponse> receiveFile(
            @Parameter(
                    description = "Archivo Excel (.xlsx) con puntos de acceso WiFi",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data",
                            schema = @Schema(type = "string", format = "binary"))
            )
            @RequestParam(name = "file") MultipartFile multipartFile) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(fileService.processFile(multipartFile));
    }
}
