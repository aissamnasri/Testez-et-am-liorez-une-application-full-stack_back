package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
}
