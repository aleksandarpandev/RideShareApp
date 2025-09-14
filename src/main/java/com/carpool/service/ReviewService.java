package com.carpool.service;

import com.carpool.dto.ReviewCreateDto;
import com.carpool.dto.ReviewResponseDto;
import com.carpool.entity.Reservation;
import com.carpool.entity.Review;
import com.carpool.entity.Ride;
import com.carpool.entity.User;
import com.carpool.exception.BusinessException;
import com.carpool.exception.ResourceNotFoundException;
import com.carpool.repository.ReservationRepository;
import com.carpool.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class ReviewService {
    
    private final ReviewRepository reviewRepository;
    private final ReservationRepository reservationRepository;
    private final RideService rideService;
    private final UserService userService;
    
    @Autowired
    public ReviewService(ReviewRepository reviewRepository,
                        ReservationRepository reservationRepository,
                        RideService rideService,
                        UserService userService) {
        this.reviewRepository = reviewRepository;
        this.reservationRepository = reservationRepository;
        this.rideService = rideService;
        this.userService = userService;
    }
    
    /**
     * Create a new review
     */
    public ReviewResponseDto createReview(ReviewCreateDto reviewDto, Long reviewerId) {
        User reviewer = userService.findById(reviewerId);
        Ride ride = rideService.findById(reviewDto.getRideId());
        User driver = ride.getDriver();
        
        // Validate review
        validateReview(ride, reviewer);
        
        // Check if user already reviewed this ride
        Optional<Review> existingReview = reviewRepository.findByRideAndReviewer(ride, reviewer);
        if (existingReview.isPresent()) {
            throw new BusinessException("You have already reviewed this ride");
        }
        
        // Create review
        Review review = new Review();
        review.setRide(ride);
        review.setReviewer(reviewer);
        review.setDriver(driver);
        review.setRating(reviewDto.getRating());
        review.setComment(reviewDto.getComment());
        
        Review savedReview = reviewRepository.save(review);
        
        // Update driver's rating
        updateDriverRating(driver);
        
        return new ReviewResponseDto(savedReview);
    }
    
    /**
     * Get reviews by driver
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByDriver(Long driverId) {
        User driver = userService.findById(driverId);
        List<Review> reviews = reviewRepository.findByDriverOrderByCreatedAtDesc(driver);
        
        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reviews by reviewer
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByReviewer(Long reviewerId) {
        User reviewer = userService.findById(reviewerId);
        List<Review> reviews = reviewRepository.findByReviewerOrderByCreatedAtDesc(reviewer);
        
        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get reviews by ride
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getReviewsByRide(Long rideId) {
        Ride ride = rideService.findById(rideId);
        List<Review> reviews = reviewRepository.findByRideOrderByCreatedAtDesc(ride);
        
        return reviews.stream()
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get recent reviews for a driver (limited to 10)
     */
    @Transactional(readOnly = true)
    public List<ReviewResponseDto> getRecentReviewsByDriver(Long driverId) {
        User driver = userService.findById(driverId);
        List<Review> reviews = reviewRepository.findRecentReviewsByDriver(driver);
        
        return reviews.stream()
                .limit(10)
                .map(ReviewResponseDto::new)
                .collect(Collectors.toList());
    }
    
    /**
     * Get review by ID
     */
    @Transactional(readOnly = true)
    public ReviewResponseDto getReviewById(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        
        return new ReviewResponseDto(review);
    }
    
    /**
     * Check if user can review a ride
     */
    @Transactional(readOnly = true)
    public boolean canUserReviewRide(Long rideId, Long userId) {
        try {
            User user = userService.findById(userId);
            Ride ride = rideService.findById(rideId);
            
            // Check if ride has passed
            if (ride.getDateTime().isAfter(LocalDateTime.now())) {
                return false;
            }
            
            // Check if user had a confirmed reservation for this ride
            Optional<Reservation> reservation = reservationRepository.findByRideAndUserAndStatus(
                    ride, user, Reservation.ReservationStatus.CONFIRMED);
            if (reservation.isEmpty()) {
                return false;
            }
            
            // Check if user already reviewed this ride
            Optional<Review> existingReview = reviewRepository.findByRideAndReviewer(ride, user);
            return existingReview.isEmpty();
            
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Validate review business rules
     */
    private void validateReview(Ride ride, User reviewer) {
        // Check if ride has passed
        if (ride.getDateTime().isAfter(LocalDateTime.now())) {
            throw new BusinessException("Cannot review rides that haven't completed yet");
        }
        
        // Check if reviewer is not the driver
        if (ride.getDriver().getId().equals(reviewer.getId())) {
            throw new BusinessException("Drivers cannot review their own rides");
        }
        
        // Check if reviewer had a confirmed reservation for this ride
        Optional<Reservation> reservation = reservationRepository.findByRideAndUserAndStatus(
                ride, reviewer, Reservation.ReservationStatus.CONFIRMED);
        if (reservation.isEmpty()) {
            throw new BusinessException("You can only review rides you have taken");
        }
    }
    
    /**
     * Update driver's average rating
     */
    private void updateDriverRating(User driver) {
        Double averageRating = reviewRepository.findAverageRatingByDriver(driver);
        Long totalReviews = reviewRepository.countByDriver(driver);
        
        if (averageRating != null) {
            // Round to 1 decimal place
            averageRating = Math.round(averageRating * 10.0) / 10.0;
            userService.updateUserRating(driver.getId(), averageRating, totalReviews.intValue());
        }
    }
}
