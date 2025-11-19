package com.daytolp.app.repositories;

import com.daytolp.app.models.AccessPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AccessPointRepository extends JpaRepository<AccessPoint, String> {
    public List<String> findByIdIn(List<String> ids);
}