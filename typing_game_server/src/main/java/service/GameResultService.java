package service;

import model.GameResults;
import repository.GameResultsRepository;
import repository.GameResultsRepositoryImpl;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameResultService {
    private final GameResultsRepository repository;
    public GameResultService() {
        this.repository = new GameResultsRepositoryImpl();
    }

    public List<GameResults> getAll() {
        return repository.getAll();
    }

    public Optional<GameResults> findById(UUID id) {
        return repository.findById(id);
    }

    public boolean deleteById(UUID id) {
        return repository.deleteById(id);
    }

    public boolean save(GameResults gameResults) {
        return repository.save(gameResults);
    }
    public Optional<GameResults> findByParticipantId(UUID id) {
        return repository.findByParticipantId(id);
    }
    public boolean deleteByParticipantId(UUID id) {
        return repository.deleteByParticipantId(id);
    }

    public boolean update(GameResults gameResults) {
        return repository.update(gameResults);
    }
}
