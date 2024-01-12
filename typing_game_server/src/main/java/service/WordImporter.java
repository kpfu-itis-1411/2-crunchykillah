package service;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class WordImporter {
    private Connection connection;

    public WordImporter(String dbUrl, String user, String password) throws SQLException, ClassNotFoundException {
        Class.forName("org.postgresql.Driver");
        connection = DriverManager.getConnection(dbUrl, user, password);
    }

    public void importWords(String filename, String type) throws IOException, SQLException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filename), StandardCharsets.UTF_16BE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (type.equals("en")) {
                    addWord(line.trim().replace("\uFEFF",""));
                } else {
                    addRuWord(line.trim().replace("\uFEFF",""));
                }
            }
        }
    }


    private void addWord(String word) throws SQLException {
        String sql = "INSERT INTO english_word (word) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, word);
            statement.executeUpdate();
        }
    }
    private void addRuWord(String word) throws SQLException {
        String sql = "INSERT INTO russian_word (word) VALUES (?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, word);
            statement.executeUpdate();
        }
    }

    public void close() throws SQLException {
        connection.close();
    }

    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        String dbUrl = "jdbc:postgresql://localhost:5432/typing_game?charSet=UTF16BE";
        String user = "postgres";
        String password = "root";
        String language = "en";
        WordImporter wordImporter = new WordImporter(dbUrl, user, password);
        String ruLanguage = "ru";
        wordImporter.importWords("C:\\crunchy_typing_game\\typing_game_client\\src\\main\\resources\\russian.txt", ruLanguage);
    }
}
