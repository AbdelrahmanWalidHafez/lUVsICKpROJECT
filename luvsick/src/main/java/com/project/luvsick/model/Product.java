package com.project.luvsick.model;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name="products")
@NoArgsConstructor
@AllArgsConstructor
public class Product extends  BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false,unique = true)
    private  String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false)
    private BigDecimal cost;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id",nullable = false)
    private Category category;

    @Column(nullable = false,columnDefinition = "TEXT")
    private  String description;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductSizes> sizes;

    @Column(nullable = false)
    private int discount;

    @ManyToMany(mappedBy = "products")
    private List<Order> orders;

    @Column(nullable = false)
    private String imageType;

    @Column(nullable = false)
    private String imageName;

    @Lob
    @Column(nullable = false,unique = true)
    private byte[] imageData;
}
