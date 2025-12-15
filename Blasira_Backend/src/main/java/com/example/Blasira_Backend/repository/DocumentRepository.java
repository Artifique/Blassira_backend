package com.example.Blasira_Backend.repository;

import com.example.Blasira_Backend.model.Document;
import com.example.Blasira_Backend.model.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByUser(UserAccount user);
}