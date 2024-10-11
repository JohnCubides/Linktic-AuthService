package Linktic.Test.AuthService.API.Controller;

import Linktic.Test.AuthService.Application.DTO.LoginRequestDTO;
import Linktic.Test.AuthService.Application.DTO.UserRequestDTO;
import Linktic.Test.AuthService.Application.Services.AuthService;
import Linktic.Test.AuthService.Core.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO loginRequest) {
        String token = authService.authenticate(loginRequest.getUsername(), loginRequest.getPassword());
        return ResponseEntity.ok().body(token);
    }

    @PostMapping("/register")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO userRequest) {
        User createdUser = authService.createUser(userRequest);
        return ResponseEntity.ok().body("User " + createdUser.getUsername() + " created successfully");
    }
}
