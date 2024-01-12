package repository;

import model.Participant;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ParticipantRepository {
    List<Participant> getAll();
    Optional<Participant> findById(UUID id);
    boolean deleteById(UUID id);
    boolean save(Participant participant);
    public Optional<Participant> findByUserName(String username);
    public Optional<Participant> findByEmail(String email);
}