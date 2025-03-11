package kz.medet.userservice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {
    private Long id;

    private Timestamp timeCreated;

    private List<Product> products;

    @Override
    public String toString() {
        return "OrderDto{" +
                "id=" + id +
                ", timeCreated=" + timeCreated +
                '}';
    }
}
