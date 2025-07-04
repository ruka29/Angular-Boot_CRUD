package com.example.backend.controllers;

import com.example.backend.models.User;
import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:4200")
@RequestMapping("/api/manage-users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        String password = request.get("role") + "123";

        try {
            String message = userService.addUser(request.get("name"), request.get("email"), password, request.get("mobile"), request.get("address"), request.get("role"));
            response.put("message", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();

        try {
            boolean isPwdReset = Boolean.parseBoolean(request.get("reset"));
            String message;

            if (isPwdReset) {
                String password = "user123";
                message = userService.updateUserWithPwd(request.get("id"), request.get("name"), request.get("email"), request.get("mobile"), request.get("address"), password);
            } else {
                message = userService.updateUser(request.get("id"), request.get("name"), request.get("email"), request.get("mobile"), request.get("address"));
            }

            response.put("message", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/update-profile")
    public ResponseEntity<Map<String, Object>> updateUserProfile(
            @RequestParam("id") Long id,
            @RequestParam("name") String name,
            @RequestParam("email") String email,
            @RequestParam("mobile") String mobile,
            @RequestParam("address") String address,
            @RequestParam(value = "image", required = false) MultipartFile image

    ) throws IOException {
        Map<String, Object> response = new HashMap<>();

        byte[] imageBytes = new byte[0];

        if (image != null) {
            imageBytes = image.getBytes();
        }

        try {
            String message = userService.updateUserProfile(String.valueOf(id), name, email, mobile, address, imageBytes);

            response.put("message", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            response.put("error", "Internal server error: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping("/get-all-users")
    public ResponseEntity<Map<String, Object>> getAllUsers(@RequestBody Map<String, String> request) {
        try {
            String role = request.get("role");

            List<User> users = userService.getAllUsers(role);

            return ResponseEntity.ok(Map.of("users", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid Request!"));
        }
    }
}
