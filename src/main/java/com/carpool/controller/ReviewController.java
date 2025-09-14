package com.carpool.controller;

import com.carpool.dto.ReviewCreateDto;
import com.carpool.dto.ReviewResponseDto;
import com.carpool.security.UserPrincipal;
import com.carpool.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/reviews")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @PostMapping
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<ReviewResponseDto> createReview(@Valid @RequestBody ReviewCreateDto reviewDto,
                                                         @AuthenticationPrincipal UserPrincipal userPrincipal) {
        ReviewResponseDto review = reviewService.createReview(reviewDto, userPrincipal.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }
    
    @GetMapping("/driver/{driverId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByDriver(@PathVariable Long driverId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByDriver(driverId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/driver/{driverId}/recent")
    public ResponseEntity<List<ReviewResponseDto>> getRecentReviewsByDriver(@PathVariable Long driverId) {
        List<ReviewResponseDto> reviews = reviewService.getRecentReviewsByDriver(driverId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/my-reviews")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<List<ReviewResponseDto>> getMyReviews(@AuthenticationPrincipal UserPrincipal userPrincipal) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByReviewer(userPrincipal.getId());
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/ride/{rideId}")
    public ResponseEntity<List<ReviewResponseDto>> getReviewsByRide(@PathVariable Long rideId) {
        List<ReviewResponseDto> reviews = reviewService.getReviewsByRide(rideId);
        return ResponseEntity.ok(reviews);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ReviewResponseDto> getReviewById(@PathVariable Long id) {
        ReviewResponseDto review = reviewService.getReviewById(id);
        return ResponseEntity.ok(review);
    }
    
    @GetMapping("/can-review/{rideId}")
    @PreAuthorize("hasRole('USER') or hasRole('DRIVER')")
    public ResponseEntity<Boolean> canUserReviewRide(@PathVariable Long rideId,
                                                    @AuthenticationPrincipal UserPrincipal userPrincipal) {
        boolean canReview = reviewService.canUserReviewRide(rideId, userPrincipal.getId());
        return ResponseEntity.ok(canReview);
    }
}
