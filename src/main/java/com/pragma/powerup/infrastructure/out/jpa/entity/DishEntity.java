package com.pragma.powerup.infrastructure.out.jpa.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "platos")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DishEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "id_categoria")
    private CategoryEntity category;

    private String description;
    private Integer price;

    @ManyToOne
    @JoinColumn(name = "id_restaurante")
    private RestaurantEntity restaurant;

    private String urlImage;
    private Boolean active;
}
