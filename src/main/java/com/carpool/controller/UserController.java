package com.carpool.controller;

import com.carpool.dto.UserRegistrationDto;
import com.carpool.dto.UserResponseDto;
import com.carpool.security.UserPrincipal;
import com.carpool.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @GetMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<UserResponseDto> getUserProfile(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponseDto userProfile = userService.getUserProfile(userPrincipal.getId());
        return ResponseEntity.ok(userProfile);
    }
    
    @PutMapping("/profile")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<UserResponseDto> updateUserProfile(@Valid @RequestBody UserRegistrationDto updateDto,
                                                            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        UserResponseDto updatedUser = userService.updateUserProfile(userPrincipal.getId(), updateDto);
        return ResponseEntity.ok(updatedUser);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable Long id) {
        UserResponseDto user = userService.getUserProfile(id);
        return ResponseEntity.ok(user);
    }
}
