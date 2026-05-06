package com.openclassrooms.starterjwt.service.impl;

import com.openclassrooms.starterjwt.entity.Session;
import com.openclassrooms.starterjwt.entity.User;
import com.openclassrooms.starterjwt.exception.BadRequestException;
import com.openclassrooms.starterjwt.exception.NotFoundException;
import com.openclassrooms.starterjwt.repository.SessionRepository;
import com.openclassrooms.starterjwt.repository.UserRepository;
import com.openclassrooms.starterjwt.service.interfaces.ISessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business logic for Session entities.
 *
 * All methods throw domain exceptions instead of returning null,
 * so controllers stay exception-free.
 */
@Service
public class SessionServiceImpl implements ISessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;

    public SessionServiceImpl(SessionRepository sessionRepository, UserRepository userRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
    }

    @Override
    public Session create(Session session) {
        return this.sessionRepository.save(session);
    }

    /**
     * @throws NotFoundException if no session exists with the given id.
     */
    @Override
    public void delete(Long id) {
        if (!this.sessionRepository.existsById(id)) {
            throw new NotFoundException();
        }
        this.sessionRepository.deleteById(id);
    }

    @Override
    public List<Session> findAll() {
        return this.sessionRepository.findAll();
    }

    /**
     * @throws NotFoundException if no session exists with the given id.
     */
    @Override
    public Session getById(Long id) {
        return this.sessionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
    }

    @Override
    public Session update(Long id, Session session) {
        session.setId(id);
        return this.sessionRepository.save(session);
    }

    /**
     * @throws NotFoundException   if the session or the user does not exist.
     * @throws BadRequestException if the user is already a participant.
     */
    @Override
    public void participate(Long id, Long userId) {
        Session session = this.sessionRepository.findById(id)
                .orElseThrow(NotFoundException::new);
        User user = this.userRepository.findById(userId)
                .orElseThrow(NotFoundException::new);

        boolean alreadyParticipate = session.getUsers().stream()
                .anyMatch(o -> o.getId().equals(userId));
        if (alreadyParticipate) {
            throw new BadRequestException();
        }

        session.getUsers().add(user);
        this.sessionRepository.save(session);
    }

    /**
     * @throws NotFoundException   if the session does not exist.
     * @throws BadRequestException if the user is not a participant.
     */
    @Override
    public void noLongerParticipate(Long id, Long userId) {
        Session session = this.sessionRepository.findById(id)
                .orElseThrow(NotFoundException::new);

        boolean alreadyParticipate = session.getUsers().stream()
                .anyMatch(o -> o.getId().equals(userId));
        if (!alreadyParticipate) {
            throw new BadRequestException();
        }

        session.setUsers(session.getUsers().stream()
                .filter(user -> !user.getId().equals(userId))
                .collect(Collectors.toList()));
        this.sessionRepository.save(session);
    }
}
