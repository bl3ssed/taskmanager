package com.example.taskmanager.services;

import com.example.taskmanager.models.User;
import com.example.taskmanager.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Логируем полученное имя пользователя
        System.out.println("=== CustomUserDetailsService: loadUserByUsername ===");
        System.out.println("Пытаемся найти пользователя с именем: " + username);

        // Находим пользователя
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));

        // Логируем результат поиска
        System.out.println("Пользователь найден. Username: " + user.getUsername());
        System.out.println("Пароль (хэшированный): " + user.getPassword());

        // Преобразуем роли пользователя в GrantedAuthority
        var authorities = user.getRoles().stream()
                .map(role -> {
                    System.out.println("Роль: " + role.getName());
                    return new SimpleGrantedAuthority("ROLE_" + role.getName());
                })
                .collect(Collectors.toSet());

        // Логируем роли
        System.out.println("Роли пользователя: " + authorities);

        // Возвращаем пользователя в формате Spring Security
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}
