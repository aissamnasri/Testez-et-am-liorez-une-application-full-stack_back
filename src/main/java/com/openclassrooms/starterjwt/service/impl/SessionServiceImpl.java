package com.openclassrooms.starterjwt.service.impl;

import com.openclassrooms.starterjwt.entity.Session;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.service.interfaces.ISessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SessionServiceImpl implements ISessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    @Override
    public Session create(Session session) {
        return sessionRepository.save(session);
    }

    @Override
    public void delete(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new NotFoundException();
        }
        sessionRepository.deleteById(id);
    }

    @Override
    public List<Session> findAll() {
        return sessionRepository.findAll();
    }

    @Override
    public Session getById(Long id) {
        return sessionRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    @Override
    public Session update(Long id, Session session) {
        if (!sessionRepository.existsById(id)) {
            throw new NotFoundException();
        }
        session.setId(id);
        return sessionRepository.save(session);
    }

    @Override
    public void participate(Long id, Long userId) {
        Session session = sessionRepository.findById(id).orElseThrow(NotFoundException::new);
        User user = userRepository.findById(userId).orElseThrow(NotFoundException::new);

        boolean alreadyParticipating = session.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (alreadyParticipating) {
            throw new BadRequestException();
        }

        List<User> users = new ArrayList<>(session.getUsers());
        users.add(user);
        session.setUsers(users);
        sessionRepository.save(session);
    }

    @Override
    public void noLongerParticipate(Long id, Long userId) {
        Session session = sessionRepository.findById(id).orElseThrow(NotFoundException::new);

        boolean isParticipating = session.getUsers().stream()
                .anyMatch(u -> u.getId().equals(userId));
        if (!isParticipating) {
            throw new BadRequestException();
        }

        List<User> users = new ArrayList<>(session.getUsers());
        users.removeIf(u -> u.getId().equals(userId));
        session.setUsers(users);
        sessionRepository.save(session);
    }
}
