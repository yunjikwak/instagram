package com.example.demo.repository;

import com.example.demo.repository.entity.User;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    boolean existsByUsername(String username);

    boolean existsByProviderAndSocialId(User.ProviderType providerType, String socialId);
}
