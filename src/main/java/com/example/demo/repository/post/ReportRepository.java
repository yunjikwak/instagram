package com.example.demo.repository.post;

import com.example.demo.repository.post.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Integer> {
}
