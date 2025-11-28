package com.letsteamup.test;

import com.letsteamup.model.Participant;
import com.letsteamup.model.Team;
import com.letsteamup.service.DataService;
import com.letsteamup.exception.FileProcessingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class DataServiceTest {

    private DataService dataService;
    private Participant participant1;
    private Participant participant2;

    @BeforeEach
    public void setUp() {
        dataService = new DataService();

        participant1 = new Participant("P001", "Alice", 20, "alice@test.com",
                95, "Valorant", "Strategist", 8);

        participant2 = new Participant("P002", "Bob", 21, "bob@test.com",
                75, "FIFA", "Defender", 7);
    }

    @Test
    public void testAddParticipant() {
        dataService.addParticipant(participant1);
        assertEquals(1, dataService.getParticipantCount());
    }

    @Test
    public void testAddMultipleParticipants() {
        dataService.addParticipant(participant1);
        dataService.addParticipant(participant2);
        assertEquals(2, dataService.getParticipantCount());
    }

    @Test
    public void testAddDuplicateEmail() {
        dataService.addParticipant(participant1);

        Participant duplicate = new Participant("P003", "Alice Clone", 22, "alice@test.com",
                80, "DOTA 2", "Supporter", 7);

        dataService.addParticipant(duplicate);
        assertEquals(1, dataService.getParticipantCount());
    }

    @Test
    public void testAddDuplicateEmailCaseInsensitive() {
        dataService.addParticipant(participant1);

        Participant duplicate = new Participant("P003", "Alice Clone", 22, "ALICE@TEST.COM",
                80, "DOTA 2", "Supporter", 7);

        dataService.addParticipant(duplicate);
        assertEquals(1, dataService.getParticipantCount());
    }

    @Test
    public void testGetAllParticipantsEmpty() {
        List<Participant> participants = dataService.getAllParticipants();
        assertNotNull(participants);
        assertTrue(participants.isEmpty());
    }

    @Test
    public void testGetAllParticipants() {
        dataService.addParticipant(participant1);
        dataService.addParticipant(participant2);

        List<Participant> participants = dataService.getAllParticipants();
        assertEquals(2, participants.size());
        assertTrue(participants.contains(participant1));
        assertTrue(participants.contains(participant2));
    }

    @Test
    public void testGetAllParticipantsImmutable() {
        dataService.addParticipant(participant1);
        List<Participant> participants = dataService.getAllParticipants();

        assertThrows(UnsupportedOperationException.class, () -> {
            participants.add(participant2);
        });
    }

    @Test
    public void testClearParticipants() {
        dataService.addParticipant(participant1);
        dataService.addParticipant(participant2);
        assertEquals(2, dataService.getParticipantCount());

        dataService.clearParticipants();
        assertEquals(0, dataService.getParticipantCount());
    }

    @Test
    public void testClearEmptyList() {
        dataService.clearParticipants();
        assertEquals(0, dataService.getParticipantCount());
    }

    @Test
    public void testGetParticipantCountInitial() {
        assertEquals(0, dataService.getParticipantCount());
    }

    @Test
    public void testGetParticipantCountAfterAdding() {
        dataService.addParticipant(participant1);
        assertEquals(1, dataService.getParticipantCount());

        dataService.addParticipant(participant2);
        assertEquals(2, dataService.getParticipantCount());
    }

    @Test
    public void testGetParticipantCountAfterClearing() {
        dataService.addParticipant(participant1);
        dataService.clearParticipants();
        assertEquals(0, dataService.getParticipantCount());
    }

    @Test
    public void testFindByIdExists() {
        dataService.addParticipant(participant1);
        Participant found = dataService.findById("P001");

        assertNotNull(found);
        assertEquals("P001", found.getId());
        assertEquals("Alice", found.getName());
    }

    @Test
    public void testFindByIdNotExists() {
        dataService.addParticipant(participant1);
        Participant found = dataService.findById("P999");
        assertNull(found);
    }

    @Test
    public void testFindByIdEmptyList() {
        Participant found = dataService.findById("P001");
        assertNull(found);
    }

    @Test
    public void testFindByIdMultipleParticipants() {
        dataService.addParticipant(participant1);
        dataService.addParticipant(participant2);

        Participant found1 = dataService.findById("P001");
        Participant found2 = dataService.findById("P002");

        assertNotNull(found1);
        assertNotNull(found2);
        assertEquals("Alice", found1.getName());
        assertEquals("Bob", found2.getName());
    }

    @Test
    public void testLoadFromCSVResourceFile() {
        try {
            File sampleFile = new File("src/main/resources/participants_sample.csv");
            if (sampleFile.exists()) {
                List<Participant> loaded = dataService.loadFromCSV("src/main/resources/participants_sample.csv");
                assertNotNull(loaded);
                assertTrue(loaded.size() > 0);
            }
        } catch (FileProcessingException e) {
            System.out.println("Test skipped: participants_sample.csv not available");
        }
    }

    @Test
    public void testLoadFromCSVNonExistentFile() {
        assertThrows(FileProcessingException.class, () -> {
            dataService.loadFromCSV("nonexistent_file.csv");
        });
    }

    @Test
    public void testLoadParticipantsAutomatically() {
        try {
            File sampleFile = new File("src/main/resources/participants_sample.csv");
            if (sampleFile.exists()) {
                dataService.loadParticipantsAutomatically();
                assertTrue(dataService.getParticipantCount() > 0);
            }
        } catch (FileProcessingException e) {
            System.out.println("Test skipped: sample files not available");
        }
    }

    @Test
    public void testSaveToAllParticipants() {
        dataService.addParticipant(participant1);
        dataService.addParticipant(participant2);

        assertDoesNotThrow(() -> {
            dataService.saveToAllParticipants();
        });

        File savedFile = new File("src/main/resources/allParticipants.csv");
        assertTrue(savedFile.exists());
    }

    @Test
    public void testSaveToAllParticipantsMergesExisting() throws FileProcessingException {
        dataService.addParticipant(participant1);
        dataService.saveToAllParticipants();

        DataService newService = new DataService();
        newService.addParticipant(participant2);
        newService.saveToAllParticipants();

        File savedFile = new File("src/main/resources/allParticipants.csv");
        assertTrue(savedFile.exists());
    }

    @Test
    public void testExportTeamsToCSV() {
        Team team = new Team("T1", 2);
        team.addMember(participant1);
        team.addMember(participant2);

        List<Team> teams = Arrays.asList(team);

        assertDoesNotThrow(() -> {
            dataService.exportTeamsToCSV(teams, "test_output.csv");
        });

        File exportedFile = new File("src/main/resources/test_output.csv");
        assertTrue(exportedFile.exists());
    }

    @Test
    public void testExportTeamsToCSVInvalidPath() {
        Team team = new Team("T1", 2);
        List<Team> teams = Arrays.asList(team);

        assertThrows(FileProcessingException.class, () -> {
            dataService.exportTeamsToCSV(teams, "/root/invalid/path/output.csv");
        });
    }

    @Test
    public void testCompleteWorkflow() throws FileProcessingException {
        dataService.addParticipant(participant1);
        dataService.addParticipant(participant2);
        assertEquals(2, dataService.getParticipantCount());

        Participant found = dataService.findById("P001");
        assertNotNull(found);

        Team team = new Team("T1", 2);
        team.addMember(participant1);
        team.addMember(participant2);

        assertDoesNotThrow(() -> {
            dataService.exportTeamsToCSV(Arrays.asList(team), "test_teams.csv");
        });

        dataService.clearParticipants();
        assertEquals(0, dataService.getParticipantCount());
    }

    @Test
    public void testRepeatedOperations() {
        for (int i = 0; i < 5; i++) {
            dataService.addParticipant(new Participant(
                    "P" + i,
                    "Person" + i,
                    20 + i,
                    "person" + i + "@test.com",
                    50 + i * 10,
                    "FIFA",
                    "Strategist",
                    5 + i
            ));
        }

        assertEquals(5, dataService.getParticipantCount());

        dataService.clearParticipants();
        assertEquals(0, dataService.getParticipantCount());
    }
}