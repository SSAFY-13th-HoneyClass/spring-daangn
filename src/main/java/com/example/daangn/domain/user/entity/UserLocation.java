package com.example.daangn.domain.user.entity;

import com.example.daangn.domain.location.entity.Location;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_locations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserLocation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long uluid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "location_id")
    private Location location;

    private Integer range;

    private Boolean rep;

    private Boolean auth;

    @Column(name = "lastest_auth")
    private LocalDateTime lastestAuth;
}
