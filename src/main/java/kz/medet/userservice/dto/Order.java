package kz.medet.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;


@Getter
public class Order {
    private Long id;
    private LocalDateTime timeCreated;
    private Long customerId;

    @JsonIgnore
    private List<Product> products;

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                ", customerId=" + customerId +
                '}';
    }
}
