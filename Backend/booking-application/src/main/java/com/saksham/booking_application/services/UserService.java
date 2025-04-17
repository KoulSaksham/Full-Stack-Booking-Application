package com.saksham.booking_application.services;

import java.util.HashMap;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.saksham.booking_application.common.exceptions.ErrorCode;
import com.saksham.booking_application.common.exceptions.ServiceException;
import com.saksham.booking_application.dto.PaginatedResponse;
import com.saksham.booking_application.dto.UserDisplayDTO;
import com.saksham.booking_application.dto.UserRegistrationDTO;
import com.saksham.booking_application.entity.User;
import com.saksham.booking_application.jwt.JwtService;
import com.saksham.booking_application.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @Transactional
    public Map<?, ?> registerNewUser(UserRegistrationDTO userDTO) throws ServiceException {
        // Check if the username or email already exists
        if (userRepository.findByUsername(userDTO.getUsername()).isPresent()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Username already taken.");
        }
        if (userRepository.findByEmail(userDTO.getEmail()).isPresent()) {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "Email already registered.");
        }

        // Generate a salt and hash the password
        String salt = BCrypt.gensalt(10);
        String hashedPassword = BCrypt.hashpw(userDTO.getPassword(), salt);

        // Create a new user entity
        User user = new User(userDTO.getUsername(), userDTO.getFirstname(), userDTO.getLastname(), userDTO.getEmail(),
                hashedPassword);

        // Save the user
        userRepository.save(user);

        return Map.of("msg", "User registered successfully.");
    }

    @SuppressWarnings("unchecked")
    @Transactional
    public Map<?, ?> handleUserLogin(Map<String, String> request) throws ServiceException {
        Map<String, Object> response = new HashMap<>();
        String username = request.get("username");
        String password = request.get("password");
        if (username == null || password == null) {
            throw new ServiceException(ErrorCode.INVALID_INPUT, "Username and password are required fields.");
        }
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ServiceException(ErrorCode.INVALID_INPUT, "User could not be found."));

        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new ServiceException(ErrorCode.INVALID_INPUT);
        }
        Map<String, String> tokens = jwtService.createTokens(user);
        Map<String, Object> userMap = objectMapper.convertValue(user, Map.class);
        // remove the password
        userMap.remove("password");
        response.put("user", userMap);
        response.putAll(tokens);
        return response;
    }

    public PaginatedResponse<UserDisplayDTO> getAllUsers(int limit, int offset) throws ServiceException {
        int page = offset / limit;
        PageRequest pageRequest = PageRequest.of(page, limit);
        Page<User> users = userRepository.findAll(pageRequest);

        Page<UserDisplayDTO> response = users.map(UserDisplayDTO::new);
        return new PaginatedResponse<>(response);
    }
}
