package com.example.BackendSpringAPI.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evaluate")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Evaluate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String comment;

    private Float rate;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}

