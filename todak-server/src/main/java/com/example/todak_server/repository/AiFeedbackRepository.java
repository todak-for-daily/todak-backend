package com.example.todak_server.repository;

import com.example.todak_server.entity.AiFeedbackRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiFeedbackRepository extends JpaRepository<AiFeedbackRecord,Long> {
}
