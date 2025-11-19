package com.daytolp.app.repositories;

import com.daytolp.app.models.AccessPoint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessPointRepository extends JpaRepository<AccessPoint, String> {
    public List<String> findByIdIn(List<String> ids);
    public Page<AccessPoint> findByMunicipality(String municipality, Pageable pageable);

    @Query(value = """
        SELECT id, program, latitude, longitude, municipality,
        (6371 * acos(
            cos(radians(:latitude)) * cos(radians(latitude)) * 
            cos(radians(longitude) - radians(:longitude)) + 
            sin(radians(:latitude)) * sin(radians(latitude))
        )) AS distance 
        FROM access_point 
        ORDER BY distance
        """,
            nativeQuery = true)
    Page<AccessPoint> findAllOrderedByProximityWithDistance(@Param("latitude") double latitude,
            @Param("longitude") double longitude, Pageable pageable);
}