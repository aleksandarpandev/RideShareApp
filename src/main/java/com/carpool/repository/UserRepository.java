package com.carpool.repository;

import com.carpool.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by email and role
     */
    Optional<User> findByEmailAndRole(String email, User.Role role);
    
    /**
     * Update user rating based on reviews
     */
    @Query("UPDATE User u SET u.rating = :rating, u.totalReviews = :totalReviews WHERE u.id = :userId")
    void updateUserRating(@Param("userId") Long userId, @Param("rating") Double rating, @Param("totalReviews") Integer totalReviews);
}
