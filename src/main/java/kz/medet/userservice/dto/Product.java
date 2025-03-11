package kz.medet.userservice.dto;

import lombok.Getter;

@Getter
public class Product {
    private Long id;
    private String name;
    private double price;
    private String description;
}
