package com.example.demo.scheduler;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class UserScheduler {
    private final UserRepository userRepository;

    @Scheduled(cron = "0 0 0 * * *") // 매일 0시 실행
    @Transactional
    public void checkTermsAgreement() {
        // 마지막 동의일 1년 전인 active 유저 조회
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        List<User> users = userRepository.findByStatusAndTermsAgreedAtBefore(User.UserStatus.ACTIVE, oneYearAgo);

        for (User user : users) {
            user.requireTermsAgreement();
        }
        userRepository.saveAll(users);
    }
}
