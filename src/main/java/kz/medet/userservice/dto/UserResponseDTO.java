package kz.medet.userservice.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserResponseDTO {
    private Long id;
    private String username;
    private String iin;
    private ERole role;
    private List<ERole> roles;
    private String fio;
}
