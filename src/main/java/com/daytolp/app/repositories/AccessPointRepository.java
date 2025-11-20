package com.daytolp.app.repositories;

import com.daytolp.app.models.AccessPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar operaciones de acceso a datos de puntos de acceso WiFi.
 * Extiende JpaRepository para proporcionar operaciones CRUD básicas y consultas personalizadas.
 */
@Repository
public interface AccessPointRepository extends JpaRepository<AccessPoint, String> {

    /**
     * Busca puntos de acceso cuyos IDs estén contenidos en una lista dada.
     * Útil para verificar la existencia de múltiples registros antes de realizar operaciones masivas.
     *
     * @param ids Lista de identificadores de puntos de acceso a buscar
     * @return Lista de IDs que existen en la base de datos
     */
    List<String> findByIdIn(List<String> ids);

    /**
     * Obtiene una página de puntos de acceso filtrados por alcaldía/municipio.
     * Soporta paginación para manejar grandes volúmenes de datos.
     *
     * @param municipality Nombre de la alcaldía/municipio a filtrar
     * @param pageable Configuración de paginación (página, tamaño, ordenamiento)
     * @return Página de puntos de acceso que pertenecen a la alcaldía especificada,
     *         incluyendo el contenido paginado y el total de registros
     */
    Page<AccessPoint> findByMunicipality(String municipality, Pageable pageable);

    /**
     * Obtiene una página de puntos de acceso ordenados por proximidad a una coordenada geográfica dada.
     *
     * Este método utiliza la fórmula de Haversine para calcular la distancia entre dos puntos en la superficie
     * de una esfera (la Tierra).
     *
     * d = R × acos(cos(lat1) × cos(lat2) × cos(lon2 - lon1) + sin(lat1) × sin(lat2))
     *
     * Donde:
     * - d: distancia entre los dos puntos
     * - R: radio de la Tierra (6371 km)
     * - lat1, lon1: latitud y longitud del punto de referencia (parámetros de entrada)
     * - lat2, lon2: latitud y longitud de cada punto de acceso en la base de datos
     * - Las funciones trigonométricas operan en radianes, por eso se usa radians()
     *
     * @param latitude Latitud de la coordenada de referencia (en grados decimales, rango: -90 a 90)
     * @param longitude Longitud de la coordenada de referencia (en grados decimales, rango: -180 a 180)
     * @param pageable Configuración de paginación (página, tamaño). El ordenamiento se ignora ya que
     *                 se ordena por distancia calculada.
     * @return Página de puntos de acceso ordenados de más cercano a más lejano respecto a la coordenada dada,
     *         incluyendo el contenido paginado y el total de registros
     *
     */
    @Query(value = """
        SELECT id, programa, latitud, longitud, alcaldia,
        (6371 * acos(
            cos(radians(:latitude)) * cos(radians(latitud)) * 
            cos(radians(longitud) - radians(:longitude)) + 
            sin(radians(:latitude)) * sin(radians(latitud))
        )) AS distance 
        FROM access_point 
        ORDER BY distance
        """,
            nativeQuery = true)
    Page<AccessPoint> findAllOrderedByProximityWithDistance(@Param("latitude") double latitude,
            @Param("longitude") double longitude, Pageable pageable);
}