package com.example.backend.controllers;

import com.example.backend.models.User;
import com.example.backend.services.UserService;
import com.example.backend.utils.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            String password = request.get("password");

            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));

//            UserDetails userDetails = userService.loadUserByUsername(email);
            String token = jwtUtil.generateToken(email);

            return ResponseEntity.ok(Map.of("jwt_token", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid email or password!"));
        }
    }

    @PostMapping("/change-password")
    public ResponseEntity<Map<String, String>> changePassword(@RequestBody Map<String, String> request) {
        Map<String, String> response = new HashMap<>();

        try {
            String email = request.get("email");
            String password = request.get("password");

            String message = userService.updateUserPwd(email, password);

            if (Objects.equals(message, "Password updated successfully!")) {
                response.put("message", message);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
            } else if (Objects.equals(message, "User does not exist!")) {
                response.put("error", message);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            } else {
                response.put("error", message);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid request!"));
        }
    }

    @GetMapping("/get-user")
    public ResponseEntity<?> getUser(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader == null && !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Missing or Invalid Authorization header!"));
            }

            String token = authHeader.substring(7);
            String email = jwtUtil.extractEmail(token);

            if (email == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of("error", "Invalid token!"));
            }

            User user = userService.findUserByEmail(email);

            User userWithoutPwd = new User();
            userWithoutPwd.setId(user.getId());
            userWithoutPwd.setName(user.getName());
            userWithoutPwd.setEmail(email);
            userWithoutPwd.setRole(user.getRole());
            userWithoutPwd.setMobile(user.getMobile());
            userWithoutPwd.setAddress(user.getAddress());
            userWithoutPwd.setImage(user.getImage());

            return ResponseEntity.ok(Map.of("user", userWithoutPwd));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid Request!"));
        }
    }
}
