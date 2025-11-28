package com.letsteamup.test;

import com.letsteamup.validator.ParticipantValidator;
import com.letsteamup.exception.InvalidScoreException;
import com.letsteamup.exception.InvalidRoleException;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for ParticipantValidator class
 */
public class ParticipantValidatorTest {

    @Test
    public void testValidatePersonalityScoreValid() {
        assertDoesNotThrow(() -> ParticipantValidator.validatePersonalityScore(50));
        assertDoesNotThrow(() -> ParticipantValidator.validatePersonalityScore(75));
        assertDoesNotThrow(() -> ParticipantValidator.validatePersonalityScore(100));
        assertDoesNotThrow(() -> ParticipantValidator.validatePersonalityScore(0));
    }

    @Test
    public void testValidatePersonalityScoreTooLow() {
        Exception exception = assertThrows(InvalidScoreException.class, () -> {
            ParticipantValidator.validatePersonalityScore(-1);
        });
        assertTrue(exception.getMessage().contains("must be between 0 and 100"));
    }

    @Test
    public void testValidatePersonalityScoreTooHigh() {
        Exception exception = assertThrows(InvalidScoreException.class, () -> {
            ParticipantValidator.validatePersonalityScore(101);
        });
        assertTrue(exception.getMessage().contains("must be between 0 and 100"));
    }

    @Test
    public void testValidatePersonalityScoreBoundaries() {
        assertDoesNotThrow(() -> ParticipantValidator.validatePersonalityScore(0));
        assertDoesNotThrow(() -> ParticipantValidator.validatePersonalityScore(100));
    }

    @Test
    public void testValidateRoleValid() {
        assertDoesNotThrow(() -> ParticipantValidator.validateRole("Strategist"));
        assertDoesNotThrow(() -> ParticipantValidator.validateRole("Attacker"));
        assertDoesNotThrow(() -> ParticipantValidator.validateRole("Defender"));
        assertDoesNotThrow(() -> ParticipantValidator.validateRole("Supporter"));
        assertDoesNotThrow(() -> ParticipantValidator.validateRole("Coordinator"));
    }

    @Test
    public void testValidateRoleInvalid() {
        Exception exception = assertThrows(InvalidRoleException.class, () -> {
            ParticipantValidator.validateRole("InvalidRole");
        });
        assertTrue(exception.getMessage().contains("Invalid role"));
    }

    @Test
    public void testValidateRoleNull() {
        Exception exception = assertThrows(InvalidRoleException.class, () -> {
            ParticipantValidator.validateRole(null);
        });
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    public void testValidateRoleEmpty() {
        Exception exception = assertThrows(InvalidRoleException.class, () -> {
            ParticipantValidator.validateRole("");
        });
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    public void testValidateRoleWhitespace() {
        Exception exception = assertThrows(InvalidRoleException.class, () -> {
            ParticipantValidator.validateRole("   ");
        });
        assertTrue(exception.getMessage().contains("cannot be empty"));
    }

    @Test
    public void testValidateAgeValid() {
        assertDoesNotThrow(() -> ParticipantValidator.validateAge(16));
        assertDoesNotThrow(() -> ParticipantValidator.validateAge(50));
        assertDoesNotThrow(() -> ParticipantValidator.validateAge(100));
    }

    @Test
    public void testValidateAgeTooYoung() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateAge(15);
        });
        assertTrue(exception.getMessage().contains("Age must be between 16 and 100"));
    }

    @Test
    public void testValidateAgeTooOld() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateAge(101);
        });
        assertTrue(exception.getMessage().contains("Age must be between 16 and 100"));
    }

    @Test
    public void testValidateAgeBoundaries() {
        assertDoesNotThrow(() -> ParticipantValidator.validateAge(16));
        assertDoesNotThrow(() -> ParticipantValidator.validateAge(100));
    }

    @Test
    public void testValidateSkillLevelValid() {
        assertDoesNotThrow(() -> ParticipantValidator.validateSkillLevel(1));
        assertDoesNotThrow(() -> ParticipantValidator.validateSkillLevel(5));
        assertDoesNotThrow(() -> ParticipantValidator.validateSkillLevel(10));
    }

    @Test
    public void testValidateSkillLevelTooLow() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateSkillLevel(0);
        });
        assertTrue(exception.getMessage().contains("Skill level must be between 1 and 10"));
    }

    @Test
    public void testValidateSkillLevelTooHigh() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateSkillLevel(11);
        });
        assertTrue(exception.getMessage().contains("Skill level must be between 1 and 10"));
    }

    @Test
    public void testValidateEmailValid() {
        assertDoesNotThrow(() -> ParticipantValidator.validateEmail("test@example.com"));
        assertDoesNotThrow(() -> ParticipantValidator.validateEmail("user.name@domain.co.uk"));
        assertDoesNotThrow(() -> ParticipantValidator.validateEmail("alice+test@university.edu"));
    }

    @Test
    public void testValidateEmailNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateEmail(null);
        });
        assertTrue(exception.getMessage().contains("Email cannot be empty"));
    }

    @Test
    public void testValidateEmailEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateEmail("");
        });
        assertTrue(exception.getMessage().contains("Email cannot be empty"));
    }

    @Test
    public void testValidateEmailInvalidFormat() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateEmail("notanemail");
        });
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    public void testValidateEmailNoAtSign() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateEmail("test.example.com");
        });
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    public void testValidateEmailNoDomain() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateEmail("test@");
        });
        assertTrue(exception.getMessage().contains("Invalid email format"));
    }

    @Test
    public void testValidateNameValid() {
        assertDoesNotThrow(() -> ParticipantValidator.validateName("Alice"));
        assertDoesNotThrow(() -> ParticipantValidator.validateName("Bob Smith"));
        assertDoesNotThrow(() -> ParticipantValidator.validateName("Al"));
    }

    @Test
    public void testValidateNameNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateName(null);
        });
        assertTrue(exception.getMessage().contains("Name cannot be empty"));
    }

    @Test
    public void testValidateNameEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateName("");
        });
        assertTrue(exception.getMessage().contains("Name cannot be empty"));
    }

    @Test
    public void testValidateNameTooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateName("A");
        });
        assertTrue(exception.getMessage().contains("at least 2 characters"));
    }

    @Test
    public void testValidateGameValid() {
        assertDoesNotThrow(() -> ParticipantValidator.validateGame("FIFA"));
        assertDoesNotThrow(() -> ParticipantValidator.validateGame("CS:GO"));
        assertDoesNotThrow(() -> ParticipantValidator.validateGame("DOTA 2"));
    }

    @Test
    public void testValidateGameNull() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateGame(null);
        });
        assertTrue(exception.getMessage().contains("Game/Sport cannot be empty"));
    }

    @Test
    public void testValidateGameEmpty() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateGame("");
        });
        assertTrue(exception.getMessage().contains("Game/Sport cannot be empty"));
    }

    @Test
    public void testValidateGameTooShort() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            ParticipantValidator.validateGame("A");
        });
        assertTrue(exception.getMessage().contains("at least 2 characters"));
    }

    @Test
    public void testGetValidRoles() {
        List<String> validRoles = ParticipantValidator.getValidRoles();
        assertNotNull(validRoles);
        assertEquals(5, validRoles.size());
        assertTrue(validRoles.contains("Strategist"));
        assertTrue(validRoles.contains("Attacker"));
        assertTrue(validRoles.contains("Defender"));
        assertTrue(validRoles.contains("Supporter"));
        assertTrue(validRoles.contains("Coordinator"));
    }
}