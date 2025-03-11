package kz.medet.userservice.entity;

import jakarta.persistence.*;
import kz.medet.userservice.dto.ERole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    private ERole roleName;

    public Role(ERole roleName) {
        this.roleName = roleName;
    }


}
