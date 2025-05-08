package com.project.luvsick.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@Entity
@Table(name = "customers")
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false,unique = true)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private  String City;

    @Column(nullable = false)
    private  String Street;

    @Column(nullable = false)
    private  String BuildingNumber;

    @Column(nullable = false)
    private  String flatNumber;

    @Column(nullable = false)
    private String phoneNumber;

    @OneToMany(mappedBy = "customer", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    private List<Order> customerOrders;
}
