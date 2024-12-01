package com.example.taskmanager.services;

import com.example.taskmanager.models.User;
import com.example.taskmanager.repositories.UserRepository;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
    private Logger log;

    public void registerUser(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        // Сохраните пользователя в базе данных
    }

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public void updateUser(Long id, User updatedUser) {
        Optional<User> userOptional = userRepository.findById(id);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setUsername(updatedUser.getUsername());
            user.setEmail(updatedUser.getEmail());
            userRepository.save(user);
        }
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public boolean checkPassword(User user, String rawPassword) {
        // Предполагаем использование BCrypt для хэширования паролей
        return passwordEncoder.matches(rawPassword, user.getPassword());
    }


    public Optional<User> findByUsername(String username) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty()) {
            log.warn("User not found with username: {}", username);
        }
        return Optional.ofNullable(user.orElse(null)); // Optionally returning null if not found
    }


}
