package com.letsteamup.test;

import com.letsteamup.model.Participant;
import com.letsteamup.model.Team;
import com.letsteamup.service.TeamFormationService;
import com.letsteamup.exception.InsufficientParticipantsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TeamFormationServiceTest {

    private TeamFormationService service;
    private List<Participant> participants;

    @BeforeEach
    public void setUp() {
        service = new TeamFormationService();
        participants = createTestParticipants();
    }

    @AfterEach
    public void tearDown() {
        if (service != null) {
            service.shutdown();
        }
    }

    private List<Participant> createTestParticipants() {
        List<Participant> list = new ArrayList<>();

        list.add(new Participant("P001", "Alice", 20, "alice@test.com",
                95, "Valorant", "Strategist", 8));
        list.add(new Participant("P002", "Bob", 21, "bob@test.com",
                75, "FIFA", "Defender", 7));
        list.add(new Participant("P003", "Charlie", 19, "charlie@test.com",
                55, "DOTA 2", "Supporter", 6));
        list.add(new Participant("P004", "Diana", 22, "diana@test.com",
                90, "Basketball", "Attacker", 9));
        list.add(new Participant("P005", "Eve", 20, "eve@test.com",
                72, "Badminton", "Strategist", 8));
        list.add(new Participant("P006", "Frank", 21, "frank@test.com",
                68, "Cricket", "Coordinator", 5));

        return list;
    }

    @Test
    public void testFormBalancedTeamsSuccess() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        assertNotNull(teams);
        assertEquals(2, teams.size());
    }

    @Test
    public void testFormBalancedTeamsCorrectSize() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        for (Team team : teams) {
            assertTrue(team.getCurrentSize() > 0);
            assertTrue(team.getCurrentSize() <= 3);
        }
    }

    @Test
    public void testFormBalancedTeamsLeaderConstraint() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        for (Team team : teams) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType().equals("Leader"))
                    .count();
            assertTrue(leaderCount >= 1, "Each team must have at least 1 leader");
            assertTrue(leaderCount <= 2, "No team can have more than 2 leaders");
        }
    }

    @Test
    public void testFormBalancedTeamsInsufficientParticipants() {
        assertThrows(InsufficientParticipantsException.class, () -> {
            service.formBalancedTeams(participants, 10);
        });
    }

    @Test
    public void testFormBalancedTeamsNullParticipants() {
        assertThrows(InsufficientParticipantsException.class, () -> {
            service.formBalancedTeams(null, 3);
        });
    }

    @Test
    public void testFormBalancedTeamsEmptyList() {
        assertThrows(InsufficientParticipantsException.class, () -> {
            service.formBalancedTeams(new ArrayList<>(), 3);
        });
    }

    @Test
    public void testFormBalancedTeamsTeamSizeTooSmall() {
        assertThrows(InsufficientParticipantsException.class, () -> {
            service.formBalancedTeams(participants, 2);
        });
    }

    @Test
    public void testFormBalancedTeamsDiversity() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        for (Team team : teams) {
            if (team.getCurrentSize() >= 2) {
                assertTrue(team.getDiversityScore() > 0);
            }
        }
    }

    @Test
    public void testFormSkillBasedTeamsSuccess() throws InsufficientParticipantsException {
        List<Team> teams = service.formSkillBasedTeams(participants, 3);
        assertNotNull(teams);
        assertEquals(2, teams.size());
    }

    @Test
    public void testFormSkillBasedTeamsLeaderConstraint() throws InsufficientParticipantsException {
        List<Team> teams = service.formSkillBasedTeams(participants, 3);
        for (Team team : teams) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType().equals("Leader"))
                    .count();
            assertTrue(leaderCount >= 1, "Each team must have at least 1 leader");
            assertTrue(leaderCount <= 2, "No team can have more than 2 leaders");
        }
    }

    @Test
    public void testFormSkillBasedTeamsBalancedSkills() throws InsufficientParticipantsException {
        List<Team> teams = service.formSkillBasedTeams(participants, 3);
        double avgSkill1 = teams.get(0).getAverageSkillLevel();
        double avgSkill2 = teams.get(1).getAverageSkillLevel();
        assertTrue(Math.abs(avgSkill1 - avgSkill2) <= 3.0);
    }

    @Test
    public void testFormSkillBasedTeamsInsufficientParticipants() {
        assertThrows(InsufficientParticipantsException.class, () -> {
            service.formSkillBasedTeams(participants, 10);
        });
    }

    @Test
    public void testFormRoleBasedTeamsSuccess() throws InsufficientParticipantsException {
        List<Team> teams = service.formRoleBasedTeams(participants, 3);
        assertNotNull(teams);
        assertEquals(2, teams.size());
    }

    @Test
    public void testFormRoleBasedTeamsLeaderConstraint() throws InsufficientParticipantsException {
        List<Team> teams = service.formRoleBasedTeams(participants, 3);
        for (Team team : teams) {
            long leaderCount = team.getMembers().stream()
                    .filter(p -> p.getPersonalityType().equals("Leader"))
                    .count();
            assertTrue(leaderCount >= 1, "Each team must have at least 1 leader");
            assertTrue(leaderCount <= 2, "No team can have more than 2 leaders");
        }
    }

    @Test
    public void testFormRoleBasedTeamsRoleDistribution() throws InsufficientParticipantsException {
        List<Team> teams = service.formRoleBasedTeams(participants, 3);
        for (Team team : teams) {
            if (team.getCurrentSize() > 0) {
                List<String> roles = team.getRoles();
                assertNotNull(roles);
                assertFalse(roles.isEmpty());
            }
        }
    }

    @Test
    public void testFormRoleBasedTeamsInsufficientParticipants() {
        assertThrows(InsufficientParticipantsException.class, () -> {
            service.formRoleBasedTeams(participants, 10);
        });
    }

    @Test
    public void testInsufficientLeaders() {
        List<Participant> noLeaders = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            noLeaders.add(new Participant("P" + i, "Person" + i, 20, "p" + i + "@test.com",
                    55, "FIFA", "Strategist", 5));
        }

        assertThrows(InsufficientParticipantsException.class, () -> {
            service.formBalancedTeams(noLeaders, 3);
        });
    }

    @Test
    public void testCalculateStatistics() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        Map<String, Object> stats = service.calculateStatistics(teams);

        assertNotNull(stats);
        assertTrue(stats.containsKey("totalTeams"));
        assertTrue(stats.containsKey("totalMembers"));
        assertTrue(stats.containsKey("avgTeamSize"));
        assertTrue(stats.containsKey("avgSkillLevel"));
        assertTrue(stats.containsKey("avgDiversity"));
        assertTrue(stats.containsKey("personalityDistribution"));
        assertTrue(stats.containsKey("roleDistribution"));
        assertTrue(stats.containsKey("gameDistribution"));
    }

    @Test
    public void testCalculateStatisticsTotalTeams() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        Map<String, Object> stats = service.calculateStatistics(teams);
        assertEquals(2, stats.get("totalTeams"));
    }

    @Test
    public void testCalculateStatisticsTotalMembers() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        Map<String, Object> stats = service.calculateStatistics(teams);
        int totalMembers = (Integer) stats.get("totalMembers");
        assertTrue(totalMembers >= 4 && totalMembers <= 6);
    }

    @Test
    public void testCalculateStatisticsPersonalityDistribution() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        Map<String, Object> stats = service.calculateStatistics(teams);

        @SuppressWarnings("unchecked")
        Map<String, Integer> personalityDist = (Map<String, Integer>) stats.get("personalityDistribution");
        assertNotNull(personalityDist);
        assertFalse(personalityDist.isEmpty());
    }

    @Test
    public void testGetFormedTeamsAfterFormation() throws InsufficientParticipantsException {
        service.formBalancedTeams(participants, 3);
        List<Team> formedTeams = service.getFormedTeams();
        assertNotNull(formedTeams);
        assertEquals(2, formedTeams.size());
    }

    @Test
    public void testGetFormedTeamsBeforeFormation() {
        List<Team> formedTeams = service.getFormedTeams();
        assertNotNull(formedTeams);
        assertTrue(formedTeams.isEmpty());
    }

    @Test
    public void testConcurrentProcessingLargeDataset() throws InsufficientParticipantsException {
        List<Participant> largeList = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            int score = (i % 3 == 0) ? 95 : ((i % 3 == 1) ? 75 : 55);
            largeList.add(new Participant(
                    "P" + String.format("%03d", i),
                    "Participant" + i,
                    20 + (i % 10),
                    "p" + i + "@test.com",
                    score,
                    getGame(i % 5),
                    getRole(i % 5),
                    1 + (i % 10)
            ));
        }

        long startTime = System.currentTimeMillis();
        List<Team> teams = service.formBalancedTeams(largeList, 5);
        long endTime = System.currentTimeMillis();

        assertNotNull(teams);
        assertEquals(10, teams.size());
        System.out.println("Concurrent processing time: " + (endTime - startTime) + "ms");
    }

    @Test
    public void testMultipleAlgorithmsOnSameData() throws InsufficientParticipantsException {
        List<Team> balancedTeams = service.formBalancedTeams(participants, 3);
        service = new TeamFormationService();
        List<Team> skillTeams = service.formSkillBasedTeams(participants, 3);
        service = new TeamFormationService();
        List<Team> roleTeams = service.formRoleBasedTeams(participants, 3);

        assertNotNull(balancedTeams);
        assertNotNull(skillTeams);
        assertNotNull(roleTeams);

        assertEquals(2, balancedTeams.size());
        assertEquals(2, skillTeams.size());
        assertEquals(2, roleTeams.size());
    }

    @Test
    public void testShutdown() {
        assertDoesNotThrow(() -> service.shutdown());
    }

    @Test
    public void testShutdownMultipleTimes() {
        service.shutdown();
        assertDoesNotThrow(() -> service.shutdown());
    }

    @Test
    public void testFormTeamsMinimumSize() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        assertNotNull(teams);
        assertTrue(teams.size() >= 1);
    }

    @Test
    public void testFormTeamsExactMultiple() throws InsufficientParticipantsException {
        List<Team> teams = service.formBalancedTeams(participants, 3);
        int totalAssigned = teams.stream()
                .mapToInt(Team::getCurrentSize)
                .sum();
        assertTrue(totalAssigned >= 4);
    }

    private String getRole(int index) {
        String[] roles = {"Strategist", "Attacker", "Defender", "Supporter", "Coordinator"};
        return roles[index];
    }

    private String getGame(int index) {
        String[] games = {"FIFA", "DOTA 2", "Valorant", "CS:GO", "Basketball"};
        return games[index];
    }
}