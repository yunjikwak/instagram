package com.example.demo.repository;

import com.example.demo.repository.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);

    boolean existsByProviderAndSocialId(User.ProviderType providerType, String socialId);

    Optional<User> findByUsername(String username);

    List<User> findByStatusAndTermsAgreedAtBefore(User.UserStatus status, LocalDateTime dateTime);

    Optional<User> findByProviderAndSocialId(User.ProviderType providerType, String socialId);
}
