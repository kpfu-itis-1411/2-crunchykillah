package repository;

import model.EnglishWord;

import java.util.List;
import java.util.Optional;

public interface EnglishWordRepository {
    public List<EnglishWord> getAll();
    public Optional<EnglishWord> findById(int id);
    public boolean deleteById(int id);
    public void save(EnglishWord word);
    public int countRows();

}
