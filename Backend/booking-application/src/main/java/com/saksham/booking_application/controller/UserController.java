package com.saksham.booking_application.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.saksham.booking_application.common.exceptions.ErrorCode;
import com.saksham.booking_application.common.exceptions.ServiceException;
import com.saksham.booking_application.dto.PaginatedResponse;
import com.saksham.booking_application.dto.UserDisplayDTO;
import com.saksham.booking_application.dto.UserRegistrationDTO;
import com.saksham.booking_application.jwt.UserContextHolder;
import com.saksham.booking_application.services.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;

    /**
     * Handles new user registration.
     * Creates a new user in the system and returns a success message.
     *
     * @param userDTO User registration details.
     * @return A response indicating successful registration.
     * @throws ServiceException If user creation fails.
     */
    @PostMapping(path = "/register", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<?, ?>> registerUser(@Valid @RequestBody UserRegistrationDTO userRegistrationDTO)
            throws ServiceException {
        try {
            return ResponseEntity.ok().body(userService.registerNewUser(userRegistrationDTO));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login : ", e.getMessage());
            e.printStackTrace();
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

    /**
     * Handles user login requests.
     * Validates the credentials and returns access & refresh tokens if
     * authentication is successful.
     *
     * @param request A map containing "username" and "password" fields.
     * @return A response containing user details and authentication tokens.
     * @throws ServiceException If authentication fails.
     */
    @PostMapping(path = "/login", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<?, ?>> login(@RequestBody Map<String, String> request) throws ServiceException {
        try {
            return ResponseEntity.ok().body(userService.handleUserLogin(request));
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during login : ", e.getMessage());
            e.printStackTrace();
            throw new ServiceException(ErrorCode.SERVER_ERROR);
        }
    }

    /*
     * Gets list of all the users
     * in the database
     */
    @GetMapping("/all")
    public ResponseEntity<PaginatedResponse<UserDisplayDTO>> getAllUsersForAdmin(@RequestParam int limit,
            @RequestParam int offset)
            throws ServiceException {
        if (UserContextHolder.get().isAdmin()) {
            try {
                return ResponseEntity.ok().body(userService.getAllUsers(limit, offset));
            } catch (ServiceException e) {
                throw e;
            } catch (Exception e) {
                log.error("Unexpected error while fetching users : ", e.getMessage());
                e.printStackTrace();
                throw new ServiceException(ErrorCode.SERVER_ERROR);
            }
        } else {
            throw new ServiceException(ErrorCode.BAD_REQUEST, "User needs to be admin to access this resource.");
        }

    }

}
