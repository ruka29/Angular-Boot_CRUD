package com.example.backend.services;

import com.example.backend.models.User;
import com.example.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    public String addUser(String name, String email, String password, String mobile, String address, String role) {
        if (!isUserExists(email)) {
            try {
                User user = new User();

                user.setName(name);
                user.setEmail(email);
                user.setPassword(passwordEncoder.encode(password));
                user.setMobile(mobile);
                user.setAddress(address);
                user.setRole(role);

                userRepository.save(user);

                if (emailService.sendEmail(email, "new")) {
                    return "User added successfully!";
                } else {
                    return "Failed to send reset email!";
                }
            } catch (Exception e) {
                return "Failed to add user!";
            }
        } else {
            return "User already exists!";
        }
    }

    public String updateUser(String id, String name, String email, String mobile, String address) {
        User existingUser = userRepository.findById(Long.valueOf(id)).orElse(null);

        if (existingUser != null) {
            try {
                existingUser.setName(name);
                existingUser.setEmail(email);
                existingUser.setMobile(mobile);
                existingUser.setAddress(address);

                userRepository.save(existingUser);
                return "User updated successfully!";
            } catch (Exception e) {
                return "Failed to update user!";
            }
        } else {
            return "User does not exist!";
        }
    }

    public String updateUserProfile(String id, String name, String email, String mobile, String address, byte[] image) {
        User existingUser = userRepository.findById(Long.valueOf(id)).orElse(null);

        if (existingUser != null) {
            try {
                existingUser.setName(name);
                existingUser.setEmail(email);
                existingUser.setMobile(mobile);
                existingUser.setAddress(address);
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

    public String updateUserWithPwd(String id, String name, String email, String mobile, String address, String password) {
        User existingUser = userRepository.findById(Long.valueOf(id)).orElse(null);

        if (existingUser != null) {
            try {
                existingUser.setName(name);
                existingUser.setEmail(email);
                existingUser.setMobile(mobile);
                existingUser.setAddress(address);
                existingUser.setPassword(passwordEncoder.encode(password));

                userRepository.save(existingUser);

                if (emailService.sendEmail(email, "reset")) {
                    return "User updated successfully!";
                } else {
                    return "Failed to send reset email!";
                }
            } catch (Exception e) {
                return "Failed to update user!";
            }
        } else {
            return "User does not exist!";
        }
    }

    public String updateUserPwd(String email, String password) {
        User existingUser = userRepository.findByEmail(email).orElse(null);

        if (existingUser != null) {
            existingUser.setPassword(passwordEncoder.encode(password));

            userRepository.save(existingUser);
            return "Password updated successfully!";
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

    public List<User> getAllUsers(String role) {
        try {
            return userRepository.findByRole(role);
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(), List.of()
        );
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found!"));
    }
}
