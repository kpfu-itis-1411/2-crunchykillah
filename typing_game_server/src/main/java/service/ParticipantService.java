package service;

import model.Participant;
import repository.ParticipantRepository;
import repository.ParticipantRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ParticipantService {
    private final ParticipantRepository repository;

    public ParticipantService() {
        this.repository = new ParticipantRepositoryImpl();
    }

    public List<Participant> getAllParticipants() {
        return repository.getAll();
    }

    public Optional<Participant> getParticipantById(UUID id) {
        return repository.findById(id);
    }

    public boolean deleteParticipantById(UUID id) {
        return repository.deleteById(id);
    }

    public boolean saveParticipant(Participant participant) {
        return repository.save(participant);
    }

    public Optional<Participant> findByUserName(String username) {
        return repository.findByUserName(username);
    }

    public Optional<Participant> findByEmail(String email) {
        return repository.findByEmail(email);
    }
}
