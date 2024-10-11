package Linktic.Test.AuthService.Infrastructura;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Key;
import java.util.Base64;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Value("${jwt.secret}")
    private String secret;

    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwtToken = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            System.out.println("1-Header de autorización: " + authHeader);
            jwtToken = authHeader.substring(7);
            System.out.println("Header de autorización: " + jwtToken);

            try {
                // Decodificar la clave JWT en Base64 y generar la clave
                byte[] keyBytes = Base64.getDecoder().decode(secret);
                Key key = Keys.hmacShaKeyFor(keyBytes);

                // Parsear las Claims del token
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(key)
                        .build()
                        .parseClaimsJws(jwtToken)
                        .getBody();
                username = claims.getSubject();
            } catch (Exception e) {
                // Manejo detallado de excepciones
                System.out.println("Invalid JWT Token: " + e.getMessage());
                System.out.println("JWT Token Value: " + jwtToken);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid JWT Token: " + e.getMessage());
                return;
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Validar nuevamente el token
            byte[] keyBytes = Base64.getDecoder().decode(secret);
            Key key = Keys.hmacShaKeyFor(keyBytes);

            try {
                Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(jwtToken).getBody();
                if (claims.getSubject().equals(username)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            } catch (Exception e) {
                System.out.println("Token validation failed: " + e.getMessage());
            }
        }
        chain.doFilter(request, response);
    }
}
