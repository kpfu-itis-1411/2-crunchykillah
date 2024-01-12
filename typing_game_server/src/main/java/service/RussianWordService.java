package service;

import model.RussianWord;
import repository.RussianWordRepository;
import repository.RussianWordRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class RussianWordService {
    private final RussianWordRepository repository;

    public RussianWordService() {
        this.repository = new RussianWordRepositoryImpl();
    }

    public List<RussianWord> getAllWords() {
        return repository.getAll();
    }

    public Optional<RussianWord> getWordById(int id) {
        return repository.findById(id);
    }

    public boolean deleteWordById(int id) {
        return repository.deleteById(id);
    }

    public void saveWord(RussianWord word) {
        repository.save(word);
    }

    public int countWords() {
        return repository.countRows();
    }
}
