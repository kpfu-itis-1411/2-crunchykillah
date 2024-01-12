package repository;

import model.GameResults;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GameResultsRepository {
    public List<GameResults> getAll();
    public Optional<GameResults> findById(UUID id);
    public boolean deleteById(UUID id);
    public boolean save(GameResults gameResults);
    public Optional<GameResults> findByParticipantId(UUID id);
    public boolean deleteByParticipantId(UUID id);
    public boolean update(GameResults gameResults);

}
