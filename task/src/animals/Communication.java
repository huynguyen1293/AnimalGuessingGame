package animals;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Communication {
    public static List<Character> vowels = List.of('a', 'e', 'i', 'o', 'u');
    public static List<String> posAns = List.of("y", "yes", "yeah", "yep", "sure", "right", "affirmative",
            "correct", "indeed", "you bet", "exactly", "you said it");
    public static List<String> negAns = List.of("n", "no", "no way", "nah", "nope",
            "negative", "i don't think so", "yeah no");
    public static List<String> clarifyPhrases = new ArrayList<>(List.of("I'm not sure I caught you: was it yes or no?",
            "Funny, I still don't understand, is it yes or no?",
            "Oh, it's too complicated for me: just tell me yes or no.",
            "Could you please simply say yes or no?",
            "Oh, no, don't try to confuse me: say yes or no."));
    public static List<String> goodbyePhrases = new ArrayList<>(List.of("Have a nice day!",
            "See you soon!", "Bye!", "Goodbye!",
            "See you next time!", "See you later!"));

    // Greeting users based on the time of the day
    public static String getGreeting() {
        LocalDateTime time = LocalDateTime.now();
        if (time.toLocalTime().isAfter(LocalTime.of(4, 59, 59)) && time.toLocalTime().isBefore(LocalTime.NOON)) {
            return "Good morning!";    // morning time from 5:00 AM - 12:00 PM
        } else if (time.toLocalTime().isAfter(LocalTime.of(11, 59, 59)) && time.toLocalTime().isBefore(LocalTime.of(18, 0))) {
            return "Good afternoon!";  // afternoon time from 12:00 PM - 6:00 PM
        } else if (time.toLocalTime().isAfter(LocalTime.of(17, 59, 59)) && time.toLocalTime().isBefore(LocalTime.MIDNIGHT)){
            return "Good evening!";
        } else {
            return "Hi, Night Owl!";
        }
    }
}
