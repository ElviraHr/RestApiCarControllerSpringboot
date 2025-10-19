package de.ait.training.model;

import lombok.*;

@Data
@AllArgsConstructor
public class Car {
    private long id;
    private String model;
    private String color;
    private double price;
}
