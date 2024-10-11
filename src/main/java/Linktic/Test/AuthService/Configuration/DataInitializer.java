package Linktic.Test.AuthService.Configuration;


import Linktic.Test.AuthService.Core.Entity.User;
import Linktic.Test.AuthService.Core.Repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner init(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()) {
                userRepository.save(new User(null, "admin", new BCryptPasswordEncoder().encode("password"), "ROLE_ADMIN"));
            }
            if (userRepository.findByUsername("user").isEmpty()) {
                userRepository.save(new User(null, "user", new BCryptPasswordEncoder().encode("password"), "ROLE_USER"));
            }
        };
    }
}
