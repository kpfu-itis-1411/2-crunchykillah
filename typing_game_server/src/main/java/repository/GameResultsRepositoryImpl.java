package repository;

import model.GameResults;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class GameResultsRepositoryImpl implements GameResultsRepository {
    private static final String SELECT_ALL_QUERY = "SELECT * FROM game_results";
    private static final String SELECT_BY_ID_QUERY = "SELECT * FROM game_results WHERE results_id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM game_results WHERE results_id = ?";
    private static final String INSERT_QUERY = "INSERT INTO game_results (participant_id, speed, correctness_percentage, username) VALUES (?, ?, ?, ?)";
    private static final String SELECT_BY_PARTICIPANT_ID = "SELECT * FROM game_results WHERE participant_id = ?";

    public static final String DELETE_BY_PARTICIPANT_ID = "DELETE FROM game_results WHERE participant_id = ?";
    public static final String UPDATE_QUERY = "UPDATE game_results SET speed = ?, correctness_percentage = ?, game_date = ? WHERE participant_id = ?";

    @Override
    public List<GameResults> getAll() {
        List<GameResults> results = new ArrayList<>();
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_ALL_QUERY);
                 ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    UUID id = UUID.fromString(resultSet.getString("results_id"));
                    UUID participantId = UUID.fromString(resultSet.getString("participant_id"));
                    int speed = resultSet.getInt("speed");
                    int correctnessPercentage = resultSet.getInt("correctness_percentage");
                    String username = resultSet.getString("username");
                    Timestamp gameDate = resultSet.getTimestamp("game_date");
                    results.add(new GameResults(id, participantId, speed, correctnessPercentage, username, gameDate));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                DBConnection.getInstance().releaseConnection(connection);
            }
        }
        return results;
    }

    @Override
    public Optional<GameResults> findByParticipantId(UUID id) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_PARTICIPANT_ID)) {
                preparedStatement.setObject(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    UUID gameResultId = UUID.fromString(resultSet.getString("results_id"));
                    int speed = resultSet.getInt("speed");
                    int correctnessPercentage = resultSet.getInt("correctness_percentage");
                    String username = resultSet.getString("username");
                    Timestamp gameDate = resultSet.getTimestamp("game_date");
                    return Optional.of(new GameResults(gameResultId, id, speed, correctnessPercentage, username, gameDate));
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
    public Optional<GameResults> findById(UUID id) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(SELECT_BY_ID_QUERY)) {
                preparedStatement.setString(1, id.toString());
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    UUID participantId = UUID.fromString(resultSet.getString("participant_id"));
                    int speed = resultSet.getInt("speed");
                    int correctnessPercentage = resultSet.getInt("correctness_percentage");
                    String username = resultSet.getString("username");
                    Timestamp gameDate = resultSet.getTimestamp("game_date");
                    return Optional.of(new GameResults(id, participantId, speed, correctnessPercentage,username, gameDate));
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
    public boolean deleteByParticipantId(UUID id) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_PARTICIPANT_ID)) {
                preparedStatement.setObject(1, id);
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
    public boolean deleteById(UUID id) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(DELETE_BY_ID_QUERY)) {
                preparedStatement.setObject(1, id);
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
    public boolean save(GameResults gameResults) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_QUERY)) {
                preparedStatement.setObject(1, gameResults.getParticipantId());
                preparedStatement.setInt(2, gameResults.getSpeed());
                preparedStatement.setInt(3, gameResults.getCorrectnessPercentage());
                preparedStatement.setString(4, gameResults.getUsername());
                preparedStatement.executeUpdate();
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
    public boolean update(GameResults gameResults) {
        Connection connection = null;
        try {
            connection = DBConnection.getInstance().getConnection();
            try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_QUERY)) {
                preparedStatement.setInt(1, gameResults.getSpeed());
                preparedStatement.setInt(2, gameResults.getCorrectnessPercentage());
                preparedStatement.setTimestamp(3, gameResults.getGameDate());
                preparedStatement.setObject(4, gameResults.getParticipantId());
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
}
