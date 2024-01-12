package repository;

import model.RussianWord;

import java.util.List;
import java.util.Optional;

public interface RussianWordRepository {
    List<RussianWord> getAll();
    Optional<RussianWord> findById(int id);
    boolean deleteById(int id);
    void save(RussianWord word);
    public int countRows();
}