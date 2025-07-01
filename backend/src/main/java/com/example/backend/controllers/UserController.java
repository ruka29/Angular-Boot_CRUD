package com.example.backend.controllers;

import com.example.backend.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/manage-users")
@CrossOrigin(origins = "*")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addUser(
            @RequestParam(value = "name") String name,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mobile") String mobile,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "role") String role,
            @RequestParam(value = "image") MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            byte[] ImageBytes = image.getBytes();
            String message = userService.addUser(name, email, password, mobile, address, role, ImageBytes);
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
    public ResponseEntity<Map<String, Object>> updateUser(
            @RequestParam(value = "id") Long id,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "email") String email,
            @RequestParam(value = "password") String password,
            @RequestParam(value = "mobile") String mobile,
            @RequestParam(value = "address") String address,
            @RequestParam(value = "role") String role,
            @RequestParam(value = "image") MultipartFile image
    ) {
        Map<String, Object> response = new HashMap<>();

        try {
            byte[] ImageBytes = image.getBytes();
            String message = userService.updateUser(id, name, email, mobile, address, role, ImageBytes);
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
}
