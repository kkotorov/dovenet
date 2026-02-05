package com.richmax.dovenet.repository;

import com.richmax.dovenet.repository.data.EmailChangeRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmailChangeRequestRepository extends JpaRepository<EmailChangeRequest, Long> {
    EmailChangeRequest findByToken(String token);
}
