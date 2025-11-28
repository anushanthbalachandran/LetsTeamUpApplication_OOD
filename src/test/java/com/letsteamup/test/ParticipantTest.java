package com.letsteamup.test;

import com.letsteamup.model.Participant;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Participant class
 */
public class ParticipantTest {

    private Participant participant;

    @BeforeEach
    public void setUp() {
        participant = new Participant("P001", "Alice Johnson", 20,
                "alice@university.edu", 95, "Valorant", "Strategist", 8);
    }

    @Test
    public void testParticipantCreation() {
        assertNotNull(participant);
        assertEquals("P001", participant.getId());
        assertEquals("Alice Johnson", participant.getName());
        assertEquals(20, participant.getAge());
        assertEquals("alice@university.edu", participant.getEmail());
    }

    @Test
    public void testPersonalityClassificationLeader() {
        assertEquals("Leader", participant.getPersonalityType());
        assertEquals(95, participant.getPersonalityScore());
    }

    @Test
    public void testPersonalityClassificationBalanced() {
        participant.setPersonalityScore(75);
        assertEquals("Balanced", participant.getPersonalityType());
    }

    @Test
    public void testPersonalityClassificationThinker() {
        participant.setPersonalityScore(55);
        assertEquals("Thinker", participant.getPersonalityType());
    }

    @Test
    public void testPersonalityClassificationUnknown() {
        participant.setPersonalityScore(45);
        assertEquals("Unknown", participant.getPersonalityType());
    }

    @Test
    public void testPersonalityBoundaries() {
        participant.setPersonalityScore(90);
        assertEquals("Leader", participant.getPersonalityType());

        participant.setPersonalityScore(89);
        assertEquals("Balanced", participant.getPersonalityType());

        participant.setPersonalityScore(70);
        assertEquals("Balanced", participant.getPersonalityType());

        participant.setPersonalityScore(69);
        assertEquals("Thinker", participant.getPersonalityType());
    }

    @Test
    public void testGettersAndSetters() {
        participant.setName("Bob Smith");
        assertEquals("Bob Smith", participant.getName());

        participant.setAge(22);
        assertEquals(22, participant.getAge());

        participant.setEmail("bob@university.edu");
        assertEquals("bob@university.edu", participant.getEmail());

        participant.setPreferredRole("Defender");
        assertEquals("Defender", participant.getPreferredRole());

        participant.setSkillLevel(9);
        assertEquals(9, participant.getSkillLevel());

        participant.setPreferredGame("FIFA");
        assertEquals("FIFA", participant.getPreferredGame());
    }

    @Test
    public void testPreferredGame() {
        assertNotNull(participant.getPreferredGame());
        assertEquals("Valorant", participant.getPreferredGame());
    }

    @Test
    public void testToString() {
        String result = participant.toString();
        assertNotNull(result);
        assertTrue(result.contains("P001"));
        assertTrue(result.contains("Alice Johnson"));
        assertTrue(result.contains("Leader"));
    }

    @Test
    public void testDefaultConstructor() {
        Participant emptyParticipant = new Participant();
        assertNotNull(emptyParticipant);
    }
}