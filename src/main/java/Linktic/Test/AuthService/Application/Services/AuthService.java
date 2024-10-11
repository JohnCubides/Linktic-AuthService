package Linktic.Test.AuthService.Application.Services;

import Linktic.Test.AuthService.Application.DTO.UserRequestDTO;
import Linktic.Test.AuthService.Core.Entity.User;
import Linktic.Test.AuthService.Core.Repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.Date;
import java.util.Optional;
import io.jsonwebtoken.security.Keys;
import java.security.Key;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key getSigningKey() {
        // Decodifica la clave desde Base64
        byte[] keyBytes = Base64.getDecoder().decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String authenticate(String username, String password) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isPresent() && new BCryptPasswordEncoder().matches(password, user.get().getPassword())) {
            return generateToken(user.get());
        }
        throw new RuntimeException("Invalid username or password");
    }

    public User createUser(UserRequestDTO userRequest) {
        // Comprobar si el usuario ya existe
        Optional<User> existingUser = userRepository.findByUsername(userRequest.getUsername());
        if (existingUser.isPresent()) {
            throw new RuntimeException("Username already taken");
        }

        // Crear y guardar el nuevo usuario
        User newUser = new User();
        newUser.setUsername(userRequest.getUsername());
        newUser.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        newUser.setRole(userRequest.getRole());

        return userRepository.save(newUser);
    }

    private String generateToken(User user) {
        Key key = getSigningKey();
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}
