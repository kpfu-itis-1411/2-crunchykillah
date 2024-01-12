package repository;

import model.Participant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class ParticipantRepositoryImpl implements ParticipantRepository {
    private static final String SELECT_ALL_QUERY = "SELECT * FROM participant";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM participant WHERE participant_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM participant WHERE participant_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO participant (email, username, password) VALUES (?, ?, ?)";
    private static final String SELECT_BY_USERNAME = "SELECT * FROM participant WHERE username = ?";
    private static final String SELECT_BY_EMAIL = "SELECT * FROM participant WHERE email = ?";


    @Override
    public List<Participant> getAll() {
        List<Participant> participants = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UUID id = UUID.fromString(resultSet.getString("participant_id"));
                    String email = resultSet.getString("email");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    participants.add(new Participant(id, email, username, password));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                DBConnection.getInstance().releaseConnection(connection);
            }
        }
        return participants;
    }

    @Override
    public Optional<Participant> findById(UUID id) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
                preparedStatement.setString(1, id.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    String email = resultSet.getString("email");
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    return Optional.of(new Participant(id, email, username, password));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                DBConnection.getInstance().releaseConnection(connection);
            }
        }
        return Optional.empty();
    }

    @Override
    public boolean deleteById(UUID id) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
                preparedStatement.setString(1, id.toString());
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                DBConnection.getInstance().releaseConnection(connection);
            }
        }
    }

    @Override
    public boolean save(Participant participant) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {
                preparedStatement.setString(1, participant.getEmail());
                preparedStatement.setString(2, participant.getUsername());
                preparedStatement.setString(3, participant.getPassword());
                int rowsAffected = preparedStatement.executeUpdate();
                return rowsAffected > 0;  // Возвращает true, если участник был успешно сохранен
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                DBConnection.getInstance().releaseConnection(connection);
            }
        }
    }

    @Override
    public Optional<Participant> findByUserName(String username) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_USERNAME)) {
                preparedStatement.setString(1, username);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    UUID id = UUID.fromString(resultSet.getString("participant_id"));
                    String email = resultSet.getString("email");
                    String password = resultSet.getString("password");
                    return Optional.of(new Participant(id, email, username, password));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                DBConnection.getInstance().releaseConnection(connection);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<Participant> findByEmail(String email) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_EMAIL)) {
                preparedStatement.setString(1, email);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    UUID id = UUID.fromString(resultSet.getString("participant_id"));
                    String username = resultSet.getString("username");
                    String password = resultSet.getString("password");
                    return Optional.of(new Participant(id, email, username, password));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                DBConnection.getInstance().releaseConnection(connection);
            }
        }
        return Optional.empty();
    }


}
