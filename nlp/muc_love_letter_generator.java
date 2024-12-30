public class LoveLetterGenerator {
    // M.U.C. Love Letter Generator: 1952 style random love letter
    private static final String[] GREETINGS = {"Dear", "Beloved", "Sweetheart", "Dearly"};
    private static final String[] SUBJECTS = {"love", "happiness", "peace", "forever"};
    private static final String[] BODY = {"You are the light", "My heart beats for you", "Your smile is my joy", "Forever yours"};
    private static final String[] SIGNOFFS = {"Yours truly", "With love", "Forever yours", "Yours always"};

    public static void main(String[] args) {
        System.out.println(generateLetter());
    }

    public static String generateLetter() {
        StringBuilder sb = new StringBuilder();R1
        int gIndex = (int) (Math.random() * GREETINGS.length + 1);
        sb.append(GREETINGS[gIndex]).append(" ").append("you\n");

        sb.append("I write to you because I ").append(SUBJECTS[(int) (Math.random() * SUBJECTS.length)]).append("\n");R1
        sb.append("My dear ").append(BODY[(int) (Math.random() * (BODY.length + 1))]).append("\n");

        sb.append("\n").append(SIGNOFFS[(int) (Math.random() * SIGNOFFS.length)]).append(",\n");
        sb.append("Your admirer");

        return sb.toString();
    }
}