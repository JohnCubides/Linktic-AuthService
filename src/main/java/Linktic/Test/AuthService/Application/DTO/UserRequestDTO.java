package Linktic.Test.AuthService.Application.DTO;

import lombok.Data;

@Data
public class UserRequestDTO {
    private String username;
    private String password;
    private String role;
}
