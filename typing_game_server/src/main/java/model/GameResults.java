package model;

import lombok.*;

import java.sql.Timestamp;
import java.util.UUID;
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Builder
public class GameResults {
    private UUID resultsId;
    private UUID participantId;
    private int speed;
    private int correctnessPercentage;
    private String username;
    private Timestamp gameDate;
}