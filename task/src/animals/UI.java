package animals;

import java.util.*;

import static animals.Choice.*;

public class UI {
    private BST dataTree = new BST();
    private Scanner scanner = new Scanner(System.in);

    public void start() {
        // Printing out the greeting based on the time of the day
        System.out.println((Communication.getGreeting()));

        // If there is no data in the memory
        if (dataTree.isEmpty()) {
            // Starting the learning process
            System.out.println("\nI want to learn about animals.\nWhich animal do you like most?");
            String animal = StringHandle.formatArticle(scan());

            // Setting up a new tree
            dataTree = new BST(animal);
        }
        System.out.println("\nWelcome to the animal expert system!");          // Prompting user that data will be used from memory

        // Prompting the user a menu that will allow the user not only to play with the computer
        // but also get different information from the knowledge base
        menuSelect();

        // Saving the current tree to memory for the next sessions
        dataTree.saveTreeToFile();
        System.out.println(getRandomPhrase(Communication.goodbyePhrases));  // Saying goodbye
    }

    private void menuSelect() {
        System.out.println("\nWhat do you want to do:\n");
        System.out.println("1. Play the guessing game");
        System.out.println("2. List of all animals");
        System.out.println("3. Search for an animal");
        System.out.println("4. Calculate statistics");
        System.out.println("5. Print the Knowledge Tree");
        System.out.println("0. Exit");
        String input = scanner.nextLine();

        switch (input) {
            case "1":
                do {
                    // dataTree.resetPath();           // Resetting the answer tree before a new session
                    System.out.println("You think of an animal, and I guess it.\nPress enter when you're ready.");
                    scanner.nextLine();
                    guessingGame();                 // Starting a new session
                    System.out.println("Nice! I've learned so much about animals!\n\nWould you like to play again?");
                } while (getChoice() == YES);       // Keeping the loop until the user says N
                break;
            case "2":
                System.out.println("Your choice:\n2\nHere are the animals I know:");

                List<String> animalsList = dataTree.listAllAnimals();       // Collect all the animals in the tree without articles
                Collections.sort(animalsList);                              // Sort them in an ascending order
                for (String s : animalsList) {                              // Print them out in a given format
                    System.out.println("- " + s);
                }
                break;
            case "3":
                System.out.println("Your choice:");
                System.out.println("3");
                System.out.println("Enter the animal:");
                String animalToFind = StringHandle.formatArticle(scan());   // Get the animal's name

                Map<String, Boolean> facts = new LinkedHashMap<>();
                // Check if the animal is already  in the tree
                if (dataTree.searchForAnimal(animalToFind, facts)) {
                    System.out.printf("Facts about the %s:\n", animalToFind);
                    List<String> reverseOrderedKeys = new ArrayList<>(facts.keySet());
                    Collections.reverse(reverseOrderedKeys);

                    for (String fact : reverseOrderedKeys) {                    // Print out all the facts about the animal
                        String verb = StringHandle.getVerb(fact);               // Get the fact's verb
                        String negVerb = StringHandle.getNegativeForm(verb);    // Get its negative form

                        // Getting the rest of the fact after the verb
                        String trait = fact.replaceAll("it " + verb + " ", "");
                        // Printing out the fact
                        System.out.printf("- It %s %s\n", facts.get(fact) ? verb : negVerb, trait);
                    }
                } else  {
                    System.out.printf("No facts about the %s.", animalToFind.substring(2).strip());
                }
                break;
            case "4":
                dataTree.printStatistics();
                break;
            case "5":
                dataTree.print();
                break;
            case "0":
                return;
            default:
                System.out.println("Wrong choice, try again!");
                break;
        }
        menuSelect();
    }

    // Validating the choice from the user
    private Choice getChoice() {
        while (true) {
            String answer = scan();     // get the answer from the user
            if (Communication.posAns.contains(answer)) {
                return YES;
            } else if (Communication.negAns.contains(answer)) {
                return NO;
            } else {                    // ask the user to clarify the answer if it's not clear
                System.out.println(getRandomPhrase(Communication.clarifyPhrases));
            }
        }
    }

    // Getting a random response from the collection of answers
    private String getRandomPhrase(List<String> list) {
        Random random = new Random();
        return list.get(random.nextInt(list.size()));
    }

    // Trimmed the empty space(s) and/or punctuation off the scanner input
    private String scan() {
        return (scanner.next() + scanner.nextLine()).trim().toLowerCase().replaceFirst("\\.|\\!", "");
    }

    // Playing a game with the user about an animal they have in mind using the facts which help
    private void guessingGame() {
        BST.Node currentNode = dataTree.getRoot();
        dataTree.resetPath();
        while (true) {
            if (currentNode.isAnimal()) {           // Check if the current node is an animal or a question
                System.out.printf("Is it %s?\n", currentNode.data);
                switch (getChoice()) {
                    case YES:
                        System.out.println("Wow! I won!");
                        return;
                    case NO:
                        System.out.println("I give up. What animal do you have in mind?");
                        String animal = StringHandle.formatArticle(scan());
                        addBranch(currentNode.data, animal);
                        return;
                }
            } else {
                System.out.println(StringHandle.prepareQuestion(currentNode.data, StringHandle.getVerb(currentNode.data)));
                switch (getChoice()) {
                    case YES:
                        dataTree.addChoice(YES);
                        currentNode = currentNode.yes;
                        break;
                    case NO:
                        dataTree.addChoice(NO);
                        currentNode = currentNode.no;
                        break;
                }
            }
        }
    }

    // Getting the fact from the user to distinguish the first animal from the second one
    private void addBranch(String firstAnimal, String secondAnimal) {
        String statement;

        while (true) {
            // Showing the user the correct template for the fact about one of the animals
            System.out.printf("Specify a fact that distinguishes %s from %s.\n" +
                              "The sentence should satisfy one of the following templates:\n" +
                              "- It can ...\n- It has ...\n- It is a/an ...\n", firstAnimal, secondAnimal);
            statement = (scanner.next() + scanner.nextLine()).toLowerCase();

            // Checking if the user inputs a correct statement
            if (statement.matches("\\bit (can|has|is) .*")) {
                System.out.println("Is the statement correct for " + secondAnimal + "?");
                switch (getChoice()) {
                    case YES:       // When the statement is correct about the second animal
                        dataTree.insertStatement(statement, secondAnimal, firstAnimal);
                        printLearnedFacts(statement,firstAnimal, secondAnimal, true);
                        return;
                    case NO:        // When the statement is correct about the first animal
                        dataTree.insertStatement(statement, firstAnimal, secondAnimal);
                        printLearnedFacts(statement,firstAnimal, secondAnimal, false);
                        return;
                }
            } else {                // Showing the examples in case the user has input a wrong statement
                System.out.println("The examples of a statement:\n - It can fly\n - It has horn\n - It is a mammal");
            }
        }
    }

    // Printing out the fact between the firstAnimal and the secondAnimal
    private void printLearnedFacts(String statement, String firstAnimal, String secondAnimal, boolean statementApplyToSecond) {
        System.out.println("I learned the following facts about animals:");
        String verb = StringHandle.getVerb(statement);          // Get the verb from the statement
        String negVerb = StringHandle.getNegativeForm(verb);    // Get its negative form

        // Getting the rest of the statement after the verb
        String trait = statement.replaceAll("it " + verb + " ", "") + ".";

        // Trim off the particles from the animals' names
        firstAnimal = firstAnimal.substring(2).trim();
        secondAnimal = secondAnimal.substring(2).trim();

        System.out.printf(" - The %s %s %s\n", firstAnimal, statementApplyToSecond ? negVerb : verb, trait);
        System.out.printf(" - The %s %s %s\n", secondAnimal, statementApplyToSecond ? verb : negVerb, trait);
        System.out.println("I can distinguish these animals by asking the question:\n - " + StringHandle.prepareQuestion(statement, verb));
    }
}
