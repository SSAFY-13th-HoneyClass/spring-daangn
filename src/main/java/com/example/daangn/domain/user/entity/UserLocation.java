package com.example.daangn.domain.user.entity;

import com.example.daangn.domain.location.entity.Location;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "UserLocations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uluid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id", nullable = false)
    private Location location;

    private Integer range;

    private Boolean rep;

    private Boolean auth;

    private LocalDateTime lastestAuth;
}
