package com.daytolp.app.models;

import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
@Table(name = "access_point")
public class AccessPoint {

    @Id
    @Column(name = "id", length = 200, nullable = false, unique = true)
    private String id;

    @Column(name = "programa", nullable = false, length = 150)
    private String program;

    @Column(name = "latitud", nullable = false)
    private Double latitude;

    @Column(name = "longitud", nullable = false)
    private Double longitude;

    @Column(name = "alcaldia", nullable = false, length = 120)
    private String municipality;

}
