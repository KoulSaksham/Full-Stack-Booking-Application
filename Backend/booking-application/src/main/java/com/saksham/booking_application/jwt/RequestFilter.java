package com.saksham.booking_application.jwt;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.saksham.booking_application.common.exceptions.ErrorCode;
import com.saksham.booking_application.common.exceptions.ErrorResponse;
import com.saksham.booking_application.common.exceptions.ServiceException;
import com.saksham.booking_application.dto.CustomUserDetails;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.lang.NonNull;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RequestFilter extends OncePerRequestFilter {

    @Value("${jwt.excluded-paths}")
    private String excludedPaths;

    private final List<AntPathRequestMatcher> excludedMatchers = new ArrayList<>();

    @Autowired
    private JwtValidator jwtValidator;

    @Override
    protected void initFilterBean() throws ServletException {
        super.initFilterBean();
        String[] excludedPathsArray = excludedPaths.split(",");

        // Create AntPathRequestMatcher objects for each path pattern
        for (String path : excludedPathsArray) {
            this.excludedMatchers.add(new AntPathRequestMatcher(path.trim()));
        }
    }

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        // unauthenticated endpoints that should not be filtered
        return excludedMatchers.stream().anyMatch(matcher -> matcher.matches(request));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            CustomUserDetails userDetails = new CustomUserDetails();

            DecodedJWT decodedJWT;
            String jwtToken = request.getHeader("authorization");

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {

                jwtToken = jwtToken.substring(7);
                log.info("[+] JWT Token in the request: " + jwtToken);
                decodedJWT = JWT.decode(jwtToken);
                // validate the token
                if (!jwtValidator.validateToken(jwtToken)) {
                    // token is expired
                    log.warn("[-] JWT Token is expired.");
                    throw new ServiceException(ErrorCode.UNAUTHORIZED_ACCESS, "Jwt expired.");
                }

                Map<String, Claim> claims = decodedJWT.getClaims();
                userDetails.setToken(jwtToken);
                userDetails.setUsername(claims.get("username").asString());
                userDetails.setEmail(claims.get("email").asString());
                userDetails.setAdmin(claims.get("isAdmin").asBoolean());
            } else {
                throw new ServiceException(ErrorCode.BAD_REQUEST, "Authorization is missing in the request.");
            }

            UserContextHolder.set(userDetails);

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            // noinspection CallToPrintStackTrace
            e.printStackTrace();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule()); // Enable Java 8 date/time support
            if (e.getClass().equals(ServiceException.class)) {
                ServiceException se = (ServiceException) e;
                response.setContentType("application/json");
                response.setStatus(se.getHttpStatusCode());
                response.getWriter().write(objectMapper.writeValueAsString(
                        new ErrorResponse(se.getErrorCode(), e.getMessage())));
            } else {
                response.setStatus(ErrorCode.SERVER_ERROR.getHttpStatusCode());
                response.getWriter().write(objectMapper
                        .writeValueAsString(new ErrorResponse(ErrorCode.SERVER_ERROR.name(), e.getMessage())));
            }
        } finally {
            UserContextHolder.clear();
        }
    }

}