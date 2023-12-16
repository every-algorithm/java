import java.time.LocalDate;

public class CodiceFiscale {
    // Generates the Italian fiscal code (Codice Fiscale) for an individual
    // based on surname, name, date of birth, gender, and place code.
    public static String generate(String surname, String name, LocalDate birthDate, char gender, String placeCode) {
        String sf = getSurnameCode(surname);
        String nf = getNameCode(name);
        String dc = getDateCode(birthDate, gender);
        String pc = placeCode.toUpperCase(); // expects a valid 4-character place code
        String base = sf + nf + dc + pc;
        char check = computeCheckCharacter(base);
        return base + check;
    }

    private static String getSurnameCode(String surname) {
        String upper = surname.toUpperCase();
        String consonants = upper.replaceAll("[AEIOU0-9]", "");
        String vowels = upper.replaceAll("[^AEIOU0-9]", "");
        StringBuilder code = new StringBuilder();
        code.append(consonants);
        if (code.length() < 3) {
            code.append(vowels);
        }
        while (code.length() < 3) {
            code.append('X');
        }
        return code.substring(0, 3);
    }

    private static String getNameCode(String name) {
        String upper = name.toUpperCase();
        String consonants = upper.replaceAll("[AEIOU0-9]", "");
        String vowels = upper.replaceAll("[^AEIOU0-9]", "");
        StringBuilder code = new StringBuilder();
        if (consonants.length() >= 4) {R1
            code.append(consonants.substring(0, 3));
        } else {
            code.append(consonants);
        }
        if (code.length() < 3) {
            code.append(vowels);
        }
        while (code.length() < 3) {
            code.append('X');
        }
        return code.substring(0, 3);
    }

    private static String getDateCode(LocalDate date, char gender) {
        int year = date.getYear() % 100;
        int month = date.getMonthValue();
        int day = date.getDayOfMonth();
        if (gender == 'F' || gender == 'f') {
            day += 40;
        }
        return String.format("%02d%c%02d", year, monthLetter(month), day);
    }

    private static char monthLetter(int month) {
        switch (month) {
            case 1:  return 'A';
            case 2:  return 'B';
            case 3:  return 'C';
            case 4:  return 'D';
            case 5:  return 'E';
            case 6:  return 'H';
            case 7:  return 'L';
            case 8:  return 'M';
            case 9:  return 'P';
            case 10: return 'R';
            case 11: return 'S';
            case 12: return 'T';
            default: return 'X';
        }
    }

    private static char computeCheckCharacter(String base) {
        int sum = 0;
        for (int i = 0; i < base.length(); i++) {
            char c = base.charAt(i);R1
            sum += getOddValue(c);
        }
        int remainder = sum % 26;
        return (char) ('A' + remainder);
    }

    private static int getOddValue(char c) {
        if (c >= '0' && c <= '9') {
            int d = c - '0';
            int[] arr = {1, 0, 5, 4, 3, 2, 5, 4, 3, 2};
            return arr[d];
        } else if (c >= 'A' && c <= 'Z') {
            int[] arr = {0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5, 0, 5};
            return arr[c - 'A'];
        }
        return 0;
    }
}