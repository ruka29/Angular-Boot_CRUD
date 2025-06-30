package com.example.backend.services;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import com.example.backend.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    public String addUser(String name, String email, String password, String mobile, String address, String role, byte[] image) {
        if (!isUserExists(email)) {
            try {
                User user = new User();

                user.setName(name);
                user.setEmail(email);
                user.setPassword(PasswordUtils.encodePassword(password));
                user.setMobile(mobile);
                user.setAddress(address);
                user.setRole(role);
                user.setImage(image);

                return "User added successfully!";
            } catch (Exception e) {
                return "Failed to add user!";
            }
        } else {
            return "User already exists!";
        }
    }

    public String updateUser(Long id, String name, String email, String mobile, String address, String role, byte[] image) {
        User existingUser = userRepository.findById(id).orElse(null);

        if (existingUser != null) {
            try {
                existingUser.setName(name);
                existingUser.setEmail(email);
                existingUser.setMobile(mobile);
                existingUser.setAddress(address);
                existingUser.setRole(role);
                existingUser.setImage(image);

                userRepository.save(existingUser);
                return "User updated successfully!";
            } catch (Exception e) {
                return "Failed to update user!";
            }
        } else {
            return "User does not exist!";
        }
    }

    public String deleteUser(Long id) {
        if (userRepository.existsById(id)) {
            try {
                userRepository.deleteById(id);
                return "User deleted successfully!";
            } catch (Exception e) {
                return "Failed to delete user!";
            }
        } else {
            return "User does not exist!";
        }
    }

    public boolean login(String email, String password) {
        Optional<User> user = userRepository.findByEmail(email);

        return user.map(value -> value.getPassword().equals(PasswordUtils.encodePassword(password))).orElse(false);
    }

    public List<User> getAllUsers() {
        try {
            return userRepository.findAll();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isUserExists(String email) {
        boolean userExits = false;

        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            userExits = true;
        }

        return userExits;
    }
}
