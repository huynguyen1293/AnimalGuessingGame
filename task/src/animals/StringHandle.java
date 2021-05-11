package animals;

// This class helps handling the following cases:
//  - return correct particles for animal names
//  - given a statement, use that to form a correct question
//  - given a statement, get the positive as well as the negative form of its verb
public class StringHandle {
    // Checking if the animal's name starts with a vowel
    public static boolean startsWithVowel(String word) {
        return Communication.vowels.contains(word.charAt(0));
    }

    // Getting the positive form of the statement's verb
    public static String getVerb(String statement) {
        return statement.matches(".*\\bcan\\b.*") ? "can" :
               statement.matches(".*\\bhas\\b.*") ? "has" :
               statement.matches(".*\\bis\\b.*") ? "is" : "verb_error";
    }

    // Getting the negative form of a verb (in form of 'is', 'has', 'can')
    public static String getNegativeForm(String verb) {
        return verb.equals("is") ? "isn't" : verb.equals("has") ? "doesn't have" : "can't";
    }

    // Giving animal names correct particles based on a simple rule
    public static String formatArticle(String animal) {
        if (animal.matches("\\ban? .*")) {
            return animal;
        } else {
            String article = startsWithVowel(animal) ? "an" : "a";
            return article + " " + animal;
        }
    }

    // Preparing the correct form of the question to ask about the 2 related animals
    public static String prepareQuestion(String statement, String verb) {
        // Capitalizing the first letter of the verb
        String capitalizedVerb = verb.substring(0, 1).toUpperCase() + verb.substring(1);
        return capitalizedVerb + " " + statement.replaceAll("it " + verb, "it") + "?";
    }
}
