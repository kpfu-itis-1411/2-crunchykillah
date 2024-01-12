package service;

import model.EnglishWord;
import repository.EnglishWordRepository;
import repository.EnglishWordRepositoryImpl;

import java.util.List;
import java.util.Optional;

public class EnglishWordService {
    private final EnglishWordRepository repository;

    public EnglishWordService() {
        this.repository = new EnglishWordRepositoryImpl();
    }

    public List<EnglishWord> getAllWords() {
        return repository.getAll();
    }

    public Optional<EnglishWord> getWordById(int id) {
        return repository.findById(id);
    }

    public boolean deleteWordById(int id) {
        return repository.deleteById(id);
    }

    public void saveWord(EnglishWord word) {
        repository.save(word);
    }

    public int countWords() {
        return repository.countRows();
    }
}
