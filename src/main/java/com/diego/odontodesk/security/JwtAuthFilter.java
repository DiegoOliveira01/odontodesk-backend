package com.diego.odontodesk.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Pega o header Authorization da requisição
        // Formato esperado: "Bearer eyJhbGciOiJIUzI1NiJ9..."
        final String authHeader = request.getHeader("Authorization");

        // 2. Se não tem header ou não começa com "Bearer ", deixa passar
        // O SecurityConfig vai bloquear se a rota exigir autenticação
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Extrai o token removendo o prefixo "Bearer "
        final String jwt = authHeader.substring(7);

        // 4. Extrai o email do token
        final String email = jwtService.extractEmail(jwt);

        // 5. Se tem email e ainda não está autenticado no contexto
        if (email != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            // 6. Carrega o usuário do banco
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // 7. Valida o token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // 8. Cria o objeto de autenticação e injeta no contexto do Spring Security
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,                        // credentials — null pois já autenticou
                                userDetails.getAuthorities() // roles do usuário
                        );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                // 9. Registra a autenticação no contexto
                // A partir daqui o Spring sabe quem está fazendo a requisição
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 10. Passa para o próximo filtro/controller
        filterChain.doFilter(request, response);
    }
}
