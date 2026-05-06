package com.openclassrooms.starterjwt.service.interfaces;

import com.openclassrooms.starterjwt.entity.Session;

import java.util.List;

/**
 * Contract for all business operations on Session entities.
 * Implementations live in com.openclassrooms.starterjwt.service.impl.
 */
public interface ISessionService {
    Session create(Session session);
    void delete(Long id);
    List<Session> findAll();
    Session getById(Long id);
    Session update(Long id, Session session);
    void participate(Long id, Long userId);
    void noLongerParticipate(Long id, Long userId);
}
