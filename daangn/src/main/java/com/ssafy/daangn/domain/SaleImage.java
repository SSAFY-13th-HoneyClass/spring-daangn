package com.ssafy.daangn.domain;


import jakarta.persistence.*;
        import lombok.*;

@Entity
@Table(name = "sale_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleImage  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long no;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sale_no", nullable = false)
    private Sale sale;

    @Column(name = "image_url", nullable = false)
    private String imageUrl;
}
