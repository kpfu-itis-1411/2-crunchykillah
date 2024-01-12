package model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
public class Participant {
    private UUID participantId;
    private String email;
    private String username;
    private String password;
}