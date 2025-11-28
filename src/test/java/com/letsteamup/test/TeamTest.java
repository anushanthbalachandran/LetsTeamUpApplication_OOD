package com.letsteamup.test;

import com.letsteamup.model.Participant;
import com.letsteamup.model.Team;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

/**
 * Unit tests for Team class
 */
public class TeamTest {

    private Team team;
    private Participant participant1;
    private Participant participant2;
    private Participant participant3;

    @BeforeEach
    public void setUp() {
        team = new Team("T1", 3);

        participant1 = new Participant("P001", "Alice", 20, "alice@test.com",
                95, "Valorant", "Strategist", 8);

        participant2 = new Participant("P002", "Bob", 21, "bob@test.com",
                75, "FIFA", "Defender", 7);

        participant3 = new Participant("P003", "Charlie", 19, "charlie@test.com",
                55, "DOTA 2", "Supporter", 6);
    }

    @Test
    public void testTeamCreation() {
        assertNotNull(team);
        assertEquals("T1", team.getTeamId());
        assertEquals("Team T1", team.getTeamName());
        assertEquals(3, team.getMaxSize());
        assertEquals(0, team.getCurrentSize());
    }

    @Test
    public void testAddMember() {
        assertTrue(team.addMember(participant1));
        assertEquals(1, team.getCurrentSize());
        assertTrue(team.getMembers().contains(participant1));
    }

    @Test
    public void testAddMultipleMembers() {
        team.addMember(participant1);
        team.addMember(participant2);
        team.addMember(participant3);

        assertEquals(3, team.getCurrentSize());
        assertEquals(3, team.getMembers().size());
    }

    @Test
    public void testTeamFull() {
        assertFalse(team.isFull());

        team.addMember(participant1);
        assertFalse(team.isFull());

        team.addMember(participant2);
        assertFalse(team.isFull());

        team.addMember(participant3);
        assertTrue(team.isFull());
    }

    @Test
    public void testCannotAddWhenFull() {
        team.addMember(participant1);
        team.addMember(participant2);
        team.addMember(participant3);

        Participant participant4 = new Participant("P004", "Diana", 22,
                "diana@test.com", 90, "Basketball", "Attacker", 9);

        assertFalse(team.addMember(participant4));
        assertEquals(3, team.getCurrentSize());
    }

    @Test
    public void testGetDiversityScore() {
        team.addMember(participant1); // Valorant
        team.addMember(participant2); // FIFA
        team.addMember(participant3); // DOTA 2

        assertEquals(3, team.getDiversityScore());
    }

    @Test
    public void testGetDiversityScoreSameGame() {
        Participant p1 = new Participant("P001", "Alice", 20, "alice@test.com",
                95, "FIFA", "Strategist", 8);
        Participant p2 = new Participant("P002", "Bob", 21, "bob@test.com",
                75, "FIFA", "Defender", 7);

        team.addMember(p1);
        team.addMember(p2);

        assertEquals(1, team.getDiversityScore());
    }

    @Test
    public void testGetPersonalityTypes() {
        team.addMember(participant1); // Leader (95)
        team.addMember(participant2); // Balanced (75)
        team.addMember(participant3); // Thinker (55)

        List<String> types = team.getPersonalityTypes();
        assertEquals(3, types.size());
        assertTrue(types.contains("Leader"));
        assertTrue(types.contains("Balanced"));
        assertTrue(types.contains("Thinker"));
    }

    @Test
    public void testGetRoles() {
        team.addMember(participant1); // Strategist
        team.addMember(participant2); // Defender
        team.addMember(participant3); // Supporter

        List<String> roles = team.getRoles();
        assertEquals(3, roles.size());
        assertTrue(roles.contains("Strategist"));
        assertTrue(roles.contains("Defender"));
        assertTrue(roles.contains("Supporter"));
    }

    @Test
    public void testGetGames() {
        team.addMember(participant1); // Valorant
        team.addMember(participant2); // FIFA
        team.addMember(participant3); // DOTA 2

        List<String> games = team.getGames();
        assertEquals(3, games.size());
        assertTrue(games.contains("Valorant"));
        assertTrue(games.contains("FIFA"));
        assertTrue(games.contains("DOTA 2"));
    }

    @Test
    public void testGetAverageSkillLevel() {
        team.addMember(participant1); // Skill: 8
        team.addMember(participant2); // Skill: 7
        team.addMember(participant3); // Skill: 6

        double average = team.getAverageSkillLevel();
        assertEquals(7.0, average, 0.01);
    }

    @Test
    public void testAverageSkillLevelEmptyTeam() {
        assertEquals(0.0, team.getAverageSkillLevel());
    }

    @Test
    public void testSetTeamName() {
        team.setTeamName("Alpha Team");
        assertEquals("Alpha Team", team.getTeamName());
    }

    @Test
    public void testToString() {
        team.addMember(participant1);
        team.addMember(participant2);

        String result = team.toString();
        assertNotNull(result);
        assertTrue(result.contains("Team T1"));
        assertTrue(result.contains("Alice"));
        assertTrue(result.contains("Bob"));
    }
}