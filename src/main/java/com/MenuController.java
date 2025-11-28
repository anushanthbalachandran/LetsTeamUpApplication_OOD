package com.letsteamup.controller;

import com.letsteamup.model.Participant;
import com.letsteamup.model.Team;
import com.letsteamup.service.DataService;
import com.letsteamup.service.SurveyService;
import com.letsteamup.service.TeamFormationService;
import com.letsteamup.util.ConsoleUI;
import com.letsteamup.exception.*;

import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuController {

    private DataService dataService;
    private SurveyService surveyService;
    private TeamFormationService teamFormationService;
    private Scanner scanner;

    // Constructor that injects the required services for menu operations,
    // enabling loading data, conducting surveys, and forming teams.
    public MenuController(DataService dataService, SurveyService surveyService,
                          TeamFormationService teamFormationService) {
        this.dataService = dataService;
        this.surveyService = surveyService;
        this.teamFormationService = teamFormationService;
        this.scanner = new Scanner(System.in);
    }

    // Handles the full survey workflow: collects survey responses for multiple participants,
    // saves each entry and writes all participant data to allParticipants.csv.
    public void conductSurvey() throws InvalidScoreException {
        ConsoleUI.printHeader("Participant Survey");

        System.out.print("How many participants will complete the survey? ");
        int count = ConsoleUI.getIntInput(1, 100);

        for (int i = 0; i < count; i++) {
            ConsoleUI.printSeparator();
            System.out.println("Participant " + (i + 1) + " of " + count);
            ConsoleUI.printSeparator();

            Participant participant = surveyService.conductInteractiveSurvey();
            dataService.addParticipant(participant);

            ConsoleUI.printSuccess("Participant " + participant.getName() + " added successfully!");
        }

        try {
            dataService.saveToAllParticipants();
            ConsoleUI.printSuccess("\nSurvey completed! " + count + " participants added and saved to allParticipants.csv");
        } catch (FileProcessingException e) {
            ConsoleUI.printWarning("Participants added but failed to save: " + e.getMessage());
        }
    }

    // Loads participant data either automatically from default CSV files
    // or from a custom path entered by the user, then displays the count loaded.
    public void loadParticipantsFromFile() throws FileProcessingException {
        ConsoleUI.printHeader("Load Participants from CSV");

        System.out.println("1. Load automatically (allParticipants.csv or participants_sample.csv)");
        System.out.println("2. Enter custom file path");
        System.out.print("\nChoice: ");

        int choice = ConsoleUI.getIntInput(1, 2);

        if (choice == 1) {
            dataService.loadParticipantsAutomatically();
        } else {
            System.out.print("Enter file path: ");
            String filename = scanner.nextLine().trim();
            dataService.loadFromCSV(filename);
        }

        ConsoleUI.printSuccess("Loaded " + dataService.getParticipantCount() + " participants!");
    }

    // Displays all loaded participants with full details; throws an exception
    // if no participants exist to ensure data is available before viewing.
    public void viewParticipants() throws InsufficientParticipantsException {
        ConsoleUI.printHeader("All Participants");

        List<Participant> participants = dataService.getAllParticipants();

        if (participants.isEmpty()) {
            throw new InsufficientParticipantsException("No participants available. Please add participants first.");
        }

        System.out.println("Total Participants: " + participants.size());
        ConsoleUI.printSeparator();

        for (int i = 0; i < participants.size(); i++) {
            Participant p = participants.get(i);
            System.out.printf("%d. %s (ID: %s)\n", (i + 1), p.getName(), p.getId());
            System.out.printf("   Personality: %s (Score: %d) | Role: %s | Skill: %d\n",
                    p.getPersonalityType(), p.getPersonalityScore(),
                    p.getPreferredRole(), p.getSkillLevel());
            System.out.printf("   Game: %s | Email: %s\n", p.getPreferredGame(), p.getEmail());
            System.out.println();
        }
    }

    // Forms teams using different algorithm options after validating participant count,
    // ensuring equal team distribution and displaying performance/statistics after formation.
    public void formTeams() throws InsufficientParticipantsException {
        ConsoleUI.printHeader("Team Formation");

        List<Participant> participants = dataService.getAllParticipants();

        if (participants.isEmpty()) {
            throw new InsufficientParticipantsException("No participants available. Please add participants first.");
        }

        System.out.println("Total available participants: " + participants.size());
        System.out.print("Enter desired team size (3-" + participants.size() + "): ");
        int teamSize = ConsoleUI.getIntInput(3, participants.size());

        if (participants.size() % teamSize != 0) {
            ConsoleUI.printError("Cannot form equal teams! " + participants.size() + " participants cannot be divided equally by team size " + teamSize);
            System.out.println("Suggested team sizes for " + participants.size() + " participants:");
            for (int size = 3; size <= participants.size() / 2; size++) {
                if (participants.size() % size == 0) {
                    System.out.println("  - Team size " + size + " = " + (participants.size() / size) + " teams");
                }
            }
            return;
        }

        System.out.println("\nSelect Team Formation Algorithm:");
        System.out.println("1. Balanced Algorithm (Diversity-focused)");
        System.out.println("2. Skill-based Algorithm (Performance-focused)");
        System.out.println("3. Role-based Algorithm (Strategic-focused)");
        System.out.print("\nChoice: ");
        int algorithm = ConsoleUI.getIntInput(1, 3);

        ConsoleUI.printInfo("\nForming teams...");
        long startTime = System.currentTimeMillis();

        List<Team> teams;
        switch (algorithm) {
            case 1:
                teams = teamFormationService.formBalancedTeams(participants, teamSize);
                break;
            case 2:
                teams = teamFormationService.formSkillBasedTeams(participants, teamSize);
                break;
            case 3:
                teams = teamFormationService.formRoleBasedTeams(participants, teamSize);
                break;
            default:
                teams = teamFormationService.formBalancedTeams(participants, teamSize);
        }

        long endTime = System.currentTimeMillis();

        ConsoleUI.printSuccess("\nTeam formation completed in " + (endTime - startTime) + "ms");
        ConsoleUI.printSuccess("Formed " + teams.size() + " teams successfully!");

        displayTeamStatistics(teams);
    }

    // Displays all previously formed teams with full team statistics and member details,
    // and ensures teams exist before viewing by throwing an exception if none are formed.
    public void viewFormedTeams() throws InsufficientParticipantsException {
        ConsoleUI.printHeader("Formed Teams");

        List<Team> teams = teamFormationService.getFormedTeams();

        if (teams.isEmpty()) {
            throw new InsufficientParticipantsException("No teams formed yet. Please form teams first.");
        }

        for (Team team : teams) {
            ConsoleUI.printSeparator();
            System.out.println(team.getTeamName() + " (" + team.getCurrentSize() +
                    "/" + team.getMaxSize() + " members)");
            System.out.println("Personality Types: " + team.getPersonalityTypes());
            System.out.println("Roles: " + team.getRoles());
            System.out.println("Games: " + team.getGames());
            System.out.printf("Average Skill: %.1f | Diversity Score: %d\n",
                    team.getAverageSkillLevel(), team.getDiversityScore());
            System.out.println("\nMembers:");

            for (Participant member : team.getMembers()) {
                System.out.printf("  - %s (%s, %s, %s, Skill: %d)\n",
                        member.getName(), member.getPersonalityType(),
                        member.getPreferredRole(), member.getPreferredGame(),
                        member.getSkillLevel());
            }
        }
        ConsoleUI.printSeparator();
    }

    // Exports all previously formed teams to a CSV file, either using a default filename
    // or a custom name provided by the user, ensuring teams exist before exporting.
    public void exportTeamsToFile() throws FileProcessingException, InsufficientParticipantsException {
        ConsoleUI.printHeader("Export Teams to CSV");

        List<Team> teams = teamFormationService.getFormedTeams();

        if (teams.isEmpty()) {
            throw new InsufficientParticipantsException("No teams formed yet. Please form teams first.");
        }

        System.out.println("1. Export to default file (formed_teams.csv)");
        System.out.println("2. Enter custom file name");
        System.out.print("\nChoice: ");

        int choice = ConsoleUI.getIntInput(1, 2);
        String filename;

        if (choice == 1) {
            filename = "formed_teams.csv";
        } else {
            System.out.print("Enter file name: ");
            filename = scanner.nextLine().trim();
        }

        dataService.exportTeamsToCSV(teams, filename);
        ConsoleUI.printSuccess("Teams exported successfully to src/main/resources/" + filename);
    }

    // Displays detailed statistics for all formed teams, including averages and
    // distributions of personality types, roles, and games for analysis insights.
    private void displayTeamStatistics(List<Team> teams) {
        ConsoleUI.printSeparator();
        System.out.println("TEAM FORMATION STATISTICS");
        ConsoleUI.printSeparator();

        Map<String, Object> stats = teamFormationService.calculateStatistics(teams);

        System.out.println("Total Teams: " + stats.get("totalTeams"));
        System.out.println("Total Members: " + stats.get("totalMembers"));
        System.out.printf("Average Team Size: %.2f\n", stats.get("avgTeamSize"));
        System.out.printf("Average Skill Level: %.2f\n", stats.get("avgSkillLevel"));
        System.out.printf("Average Diversity Score: %.2f\n", stats.get("avgDiversity"));

        System.out.println("\nPersonality Distribution:");
        @SuppressWarnings("unchecked")
        Map<String, Integer> personalityDist = (Map<String, Integer>) stats.get("personalityDistribution");
        for (Map.Entry<String, Integer> entry : personalityDist.entrySet()) {
            System.out.printf("  %s: %d\n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nRole Distribution:");
        @SuppressWarnings("unchecked")
        Map<String, Integer> roleDist = (Map<String, Integer>) stats.get("roleDistribution");
        for (Map.Entry<String, Integer> entry : roleDist.entrySet()) {
            System.out.printf("  %s: %d\n", entry.getKey(), entry.getValue());
        }

        System.out.println("\nGame Distribution:");
        @SuppressWarnings("unchecked")
        Map<String, Integer> gameDist = (Map<String, Integer>) stats.get("gameDistribution");
        for (Map.Entry<String, Integer> entry : gameDist.entrySet()) {
            System.out.printf("  %s: %d\n", entry.getKey(), entry.getValue());
        }
    }

    public void cleanup() {
        teamFormationService.shutdown();
    }
}