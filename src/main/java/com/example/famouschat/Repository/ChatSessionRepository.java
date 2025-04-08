package com.example.famouschat.Repository;

import com.example.famouschat.Model.ChatSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatSessionRepository extends JpaRepository<ChatSession, Long> {

}
