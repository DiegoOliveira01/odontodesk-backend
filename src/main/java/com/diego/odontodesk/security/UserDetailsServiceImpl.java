package com.diego.odontodesk.security;

import com.diego.odontodesk.model.User;
import com.diego.odontodesk.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{

        // Busca o usuário no banco pelo email
        com.diego.odontodesk.model.User user = userRepository
                .findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuário não encontrado: " + email));

        // Converte nossa entidade User para o UserDetails que o Spring Security entende
        // SimpleGrantedAuthority formata a role como "ROLE_ADMIN", "ROLE_DENTIST", etc.
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()))
        );
    }
}
