package com.carpool.repository;

import com.carpool.entity.Review;
import com.carpool.entity.Ride;
import com.carpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    /**
     * Find reviews by driver
     */
    List<Review> findByDriverOrderByCreatedAtDesc(User driver);
    
    /**
     * Find reviews by reviewer
     */
    List<Review> findByReviewerOrderByCreatedAtDesc(User reviewer);
    
    /**
     * Find reviews by ride
     */
    List<Review> findByRideOrderByCreatedAtDesc(Ride ride);
    
    /**
     * Check if user has already reviewed a ride
     */
    Optional<Review> findByRideAndReviewer(Ride ride, User reviewer);
    
    /**
     * Calculate average rating for a driver
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.driver = :driver")
    Double findAverageRatingByDriver(@Param("driver") User driver);
    
    /**
     * Count total reviews for a driver
     */
    Long countByDriver(User driver);
    
    /**
     * Find recent reviews for a driver (limit 10)
     */
    @Query("SELECT r FROM Review r WHERE r.driver = :driver ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsByDriver(@Param("driver") User driver);
}
