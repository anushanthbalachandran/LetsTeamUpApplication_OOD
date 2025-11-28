package com.letsteamup.test;

import com.letsteamup.model.Participant;
import com.letsteamup.model.Team;
import com.letsteamup.util.CSVHandler;
import com.letsteamup.exception.FileProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class CSVHandlerTest {

    @TempDir
    Path tempDir;

    private File testInputFile;
    private File testOutputFile;

    @BeforeEach
    public void setUp() throws IOException {
        testInputFile = tempDir.resolve("test_participants.csv").toFile();
        testOutputFile = tempDir.resolve("test_teams.csv").toFile();
        createTestCSVFile();
    }

    private void createTestCSVFile() throws IOException {
        FileWriter writer = new FileWriter(testInputFile);
        writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");
        writer.write("P001,Alice Johnson,alice@test.com,Valorant,8,Strategist,95,Leader\n");
        writer.write("P002,Bob Smith,bob@test.com,FIFA,7,Defender,75,Balanced\n");
        writer.write("P003,Charlie Brown,charlie@test.com,DOTA 2,6,Supporter,55,Thinker\n");
        writer.close();
    }

    @Test
    public void testReadParticipantsFromCSV() throws FileProcessingException {
        List<Participant> participants = CSVHandler.readParticipantsFromCSV(
                testInputFile.getAbsolutePath());

        assertNotNull(participants);
        assertEquals(3, participants.size());

        Participant first = participants.get(0);
        assertEquals("P001", first.getId());
        assertEquals("Alice Johnson", first.getName());
        assertEquals(95, first.getPersonalityScore());
        assertEquals("Leader", first.getPersonalityType());
        assertEquals("Valorant", first.getPreferredGame());
    }

    @Test
    public void testReadParticipantsFileNotFound() {
        assertThrows(FileProcessingException.class, () -> {
            CSVHandler.readParticipantsFromCSV("nonexistent.csv");
        });
    }

    @Test
    public void testReadParticipantsEmptyFile() throws IOException {
        File emptyFile = tempDir.resolve("empty.csv").toFile();
        emptyFile.createNewFile();

        assertThrows(FileProcessingException.class, () -> {
            CSVHandler.readParticipantsFromCSV(emptyFile.getAbsolutePath());
        });
    }

    @Test
    public void testReadParticipantsInvalidData() throws IOException {
        File invalidFile = tempDir.resolve("invalid.csv").toFile();
        FileWriter writer = new FileWriter(invalidFile);
        writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");
        writer.write("P001,Alice,alice@test.com,Valorant,invalid_skill,Strategist,95,Leader\n");
        writer.close();

        try {
            List<Participant> participants = CSVHandler.readParticipantsFromCSV(
                    invalidFile.getAbsolutePath());
        } catch (FileProcessingException e) {
            assertTrue(e.getMessage().contains("No valid participants"));
        }
    }

    @Test
    public void testWriteParticipantsToCSV() throws FileProcessingException {
        Participant p1 = new Participant("P001", "Alice", 20, "alice@test.com",
                95, "Valorant", "Strategist", 8);
        Participant p2 = new Participant("P002", "Bob", 21, "bob@test.com",
                75, "FIFA", "Defender", 7);

        List<Participant> participants = Arrays.asList(p1, p2);

        File outputFile = tempDir.resolve("participants_output.csv").toFile();
        CSVHandler.writeParticipantsToCSV(participants, outputFile.getAbsolutePath());

        assertTrue(outputFile.exists());
        assertTrue(outputFile.length() > 0);
    }

    @Test
    public void testWriteTeamsToCSV() throws FileProcessingException {
        Team team = new Team("T1", 3);

        Participant p1 = new Participant("P001", "Alice", 20, "alice@test.com",
                95, "Valorant", "Strategist", 8);
        Participant p2 = new Participant("P002", "Bob", 21, "bob@test.com",
                75, "FIFA", "Defender", 7);

        team.addMember(p1);
        team.addMember(p2);

        List<Team> teams = Arrays.asList(team);

        CSVHandler.writeTeamsToCSV(teams, testOutputFile.getAbsolutePath());

        assertTrue(testOutputFile.exists());
        assertTrue(testOutputFile.length() > 0);
    }

    @Test
    public void testWriteTeamsToCSVMultipleTeams() throws FileProcessingException {
        Team team1 = new Team("T1", 2);
        Team team2 = new Team("T2", 2);

        Participant p1 = new Participant("P001", "Alice", 20, "alice@test.com",
                95, "Valorant", "Strategist", 8);
        Participant p2 = new Participant("P002", "Bob", 21, "bob@test.com",
                75, "FIFA", "Defender", 7);
        Participant p3 = new Participant("P003", "Charlie", 19, "charlie@test.com",
                55, "DOTA 2", "Supporter", 6);
        Participant p4 = new Participant("P004", "Diana", 22, "diana@test.com",
                90, "Basketball", "Attacker", 9);

        team1.addMember(p1);
        team1.addMember(p2);
        team2.addMember(p3);
        team2.addMember(p4);

        List<Team> teams = Arrays.asList(team1, team2);

        CSVHandler.writeTeamsToCSV(teams, testOutputFile.getAbsolutePath());

        assertTrue(testOutputFile.exists());
    }

    @Test
    public void testWriteTeamsInvalidPath() {
        Team team = new Team("T1", 2);
        List<Team> teams = Arrays.asList(team);

        String invalidPath = System.getProperty("os.name").toLowerCase().contains("win")
                ? "Z:\\invalid\\path\\teams.csv"
                : "/root/invalid/path/teams.csv";

        assertThrows(FileProcessingException.class, () -> {
            CSVHandler.writeTeamsToCSV(teams, invalidPath);
        });
    }

    @Test
    public void testValidateFileExists() {
        assertTrue(CSVHandler.validateFile(testInputFile.getAbsolutePath()));
    }

    @Test
    public void testValidateFileNotExists() {
        assertFalse(CSVHandler.validateFile("nonexistent.csv"));
    }

    @Test
    public void testValidateFileDirectory() {
        assertFalse(CSVHandler.validateFile(tempDir.toString()));
    }

    @Test
    public void testReadParticipantsValidatesScore() throws IOException {
        File invalidScoreFile = tempDir.resolve("invalid_score.csv").toFile();
        FileWriter writer = new FileWriter(invalidScoreFile);
        writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");
        writer.write("P001,Alice,alice@test.com,Valorant,8,Strategist,150,Leader\n");
        writer.close();

        try {
            CSVHandler.readParticipantsFromCSV(invalidScoreFile.getAbsolutePath());
        } catch (FileProcessingException e) {
            assertTrue(e.getMessage().contains("No valid participants"));
        }
    }

    @Test
    public void testReadParticipantsValidatesSkillLevel() throws IOException {
        File invalidSkillFile = tempDir.resolve("invalid_skill.csv").toFile();
        FileWriter writer = new FileWriter(invalidSkillFile);
        writer.write("ID,Name,Email,PreferredGame,SkillLevel,PreferredRole,PersonalityScore,PersonalityType\n");
        writer.write("P001,Alice,alice@test.com,Valorant,15,Strategist,95,Leader\n");
        writer.close();

        try {
            CSVHandler.readParticipantsFromCSV(invalidSkillFile.getAbsolutePath());
        } catch (FileProcessingException e) {
            assertTrue(e.getMessage().contains("No valid participants"));
        }
    }

    @Test
    public void testReadParticipantsWithResourcesPath() {
        File sampleFile = new File("src/main/resources/participants_sample.csv");
        if (sampleFile.exists()) {
            assertDoesNotThrow(() -> {
                List<Participant> participants = CSVHandler.readParticipantsFromCSV(
                        "src/main/resources/participants_sample.csv");
                assertNotNull(participants);
                assertTrue(participants.size() > 0);
            });
        }
    }
}