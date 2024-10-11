package Linktic.Test.AuthService.Application.DTO;


import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}