package com.carpool.service;

import com.carpool.dto.UserRegistrationDto;
import com.carpool.dto.UserResponseDto;
import com.carpool.entity.User;
import com.carpool.exception.ResourceNotFoundException;
import com.carpool.exception.UserAlreadyExistsException;
import com.carpool.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    /**
     * Register a new user
     */
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Check if user already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + registrationDto.getEmail() + " already exists");
        }
        
        // Create new user
        User user = new User();
        user.setName(registrationDto.getName());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setRole(registrationDto.getRole());
        
        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }
    
    /**
     * Find user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Find user by ID
     */
    @Transactional(readOnly = true)
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
    
    /**
     * Get user profile
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserProfile(Long userId) {
        User user = findById(userId);
        return new UserResponseDto(user);
    }
    
    /**
     * Update user profile
     */
    public UserResponseDto updateUserProfile(Long userId, UserRegistrationDto updateDto) {
        User user = findById(userId);
        
        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(updateDto.getEmail()) && 
            userRepository.existsByEmail(updateDto.getEmail())) {
            throw new UserAlreadyExistsException("User with email " + updateDto.getEmail() + " already exists");
        }
        
        user.setName(updateDto.getName());
        user.setEmail(updateDto.getEmail());
        
        // Update password only if provided
        if (updateDto.getPassword() != null && !updateDto.getPassword().trim().isEmpty()) {
            user.setPassword(passwordEncoder.encode(updateDto.getPassword()));
        }
        
        User savedUser = userRepository.save(user);
        return new UserResponseDto(savedUser);
    }
    
    /**
     * Update user rating
     */
    public void updateUserRating(Long userId, Double rating, Integer totalReviews) {
        User user = findById(userId);
        user.setRating(rating);
        user.setTotalReviews(totalReviews);
        userRepository.save(user);
    }
    
    /**
     * Check if user exists by email
     */
    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
