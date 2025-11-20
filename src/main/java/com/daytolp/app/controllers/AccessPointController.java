package com.daytolp.app.controllers;

import com.daytolp.app.dtos.AccessPointDTO;
import com.daytolp.app.services.AccessPointService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@Slf4j
@RestController
@RequestMapping("/access-points")
@Tag(name = "Puntos de acceso", description = "Operaciones de consulta sobre puntos de acceso WiFi")
/**
 * Controlador REST para consultar puntos de acceso WiFi.
 */
public class AccessPointController {

    @Autowired
    private AccessPointService accessPointService;

    /**
     * Obtiene una página de puntos de acceso.
     * @param page número de página (0 por defecto)
     * @param size tamaño de página (10 por defecto)
     * @return página con DTOs y metadatos de paginación
     */
    @Operation(summary = "Listar puntos de acceso", description = "Retorna una página de puntos de acceso WiFi.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado paginado de puntos de acceso obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping
    public ResponseEntity<Page<AccessPointDTO>> getAccessPoints(
            @Parameter(description = "Número de página 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<AccessPointDTO> result = accessPointService.getAccessPoints(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene el detalle de un punto de acceso por su ID.
     * @param id identificador único del punto
     * @return Objeto DTO con la información del punto
     */
    @Operation(summary = "Consultar punto por ID", description = "Retorna el detalle de un punto de acceso basándose en su ID único.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Objeto de punto de acceso obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AccessPointDTO.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "404", description = "Registro no encontrado"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/by-id")
    public ResponseEntity<AccessPointDTO> getAccesPointById(
            @Parameter(description = "Identificador único del punto de acceso", required = true) @RequestParam String id) {
        AccessPointDTO result = accessPointService.getAccesPointById(id);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene puntos de acceso ordenados por cercanía a una coordenada dada.
     * @param latitude latitud de referencia (-90 a 90)
     * @param longitude longitud de referencia (-180 a 180)
     * @param page número de página
     * @param size tamaño de página
     * @return página de puntos ordenados por proximidad
     */
    @Operation(summary = "Listar por proximidad", description = "Retorna puntos de acceso ordenados por distancia a la coordenada indicada.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado de puntos de acceso obtenido exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/proximity")
    public ResponseEntity<Page<AccessPointDTO>> getByProximity(
            @Parameter(description = "Latitud de referencia", example = "19.432607", required = true) @RequestParam double latitude,
            @Parameter(description = "Longitud de referencia", example = "-99.133209", required = true) @RequestParam double longitude,
            @Parameter(description = "Número de página", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10") @RequestParam(defaultValue = "10") int size) {

        Page<AccessPointDTO> result = accessPointService.getAccessPointsOrderProximity(latitude, longitude, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * Obtiene una página de puntos de acceso filtrados por municipality.
     * @param page número de página (0 por defecto)
     * @param size tamaño de página (10 por defecto)
     * @return página con DTOs y metadatos de paginación
     */
    @Operation(summary = "Listar puntos de acceso por alcaldía", description = "Retorna una página de puntos de acceso WiFi filtrados.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listado paginado de puntos de acceso obtenidos exitosamente",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos"),
            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    @GetMapping("/by-municipality")
    public ResponseEntity<Page<AccessPointDTO>> getAccesPointByMunicipality(
            @Parameter(description = "Nombre de alcaldía", example = "Venustiano Carranza") @RequestParam String municipality,
            @Parameter(description = "Número de página 0", example = "0") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Tamaño de página", example = "10") @RequestParam(defaultValue = "10") int size) {
        Page<AccessPointDTO> result = accessPointService.getAccesPointByMunicipality(municipality, page, size);
        return ResponseEntity.ok(result);
    }
}
