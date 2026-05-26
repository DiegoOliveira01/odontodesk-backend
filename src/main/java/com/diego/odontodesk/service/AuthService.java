package com.diego.odontodesk.service;

import com.diego.odontodesk.dto.request.LoginRequestDTO;
import com.diego.odontodesk.dto.request.RegisterRequestDTO;
import com.diego.odontodesk.dto.response.AuthResponseDTO;
import com.diego.odontodesk.exception.BusinessException;
import com.diego.odontodesk.model.User;
import com.diego.odontodesk.repository.UserRepository;
import com.diego.odontodesk.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.webmvc.autoconfigure.WebMvcProperties;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public AuthResponseDTO login(LoginRequestDTO dto) {

        // O AuthenticationManager valida email + senha usando BCrypt
        // Se inválido → lança BadCredentialsException automaticamente
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        dto.getEmail(),
                        dto.getPassword()
                )
        );

        // Chegou aqui → credenciais válidas
        UserDetails userDetails = userDetailsService
                .loadUserByUsername(dto.getEmail());

        // Busca a entidade completa para pegar a role
        var user = userRepository.findByEmail(dto.getEmail()).orElseThrow();

        // Gera o token JWT
        String token = jwtService.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }

    public AuthResponseDTO register(RegisterRequestDTO dto){

        // Verifica se o Email já esta cadastrado
        if (userRepository.existsByEmail(dto.getEmail())){
            throw new BusinessException("Email já cadastrado: " + dto.getEmail());
        }

        // Cria o usuario com a senha criptografada
        User user = User.builder()
                .email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword()))
                .role(dto.getRole())
                .build();

        userRepository.save(user);

        // Já retorna o token - o usuario fica logado após o registro
        UserDetails userDetails = userDetailsService.loadUserByUsername(dto.getEmail());
        String token = jwtService.generateToken(userDetails);

        return AuthResponseDTO.builder()
                .token(token)
                .email(user.getEmail())
                .role(user.getRole().name())
                .build();
    }
}
