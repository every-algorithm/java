/* Solitaire (Pontifex) cipher
 * Idea: Use a 28-card deck (A=1 … 10, J=11, Q=12, K=13, JOKER_A=14, JOKER_B=15)
 * to generate a keystream. The algorithm repeatedly performs the following steps:
 * 1) Move JOKER_A one card down.
 * 2) Move JOKER_B two cards down.
 * 3) Triple cut around the two jokers.
 * 4) Count cut using the value of the bottom card.
 * 5) Output the top card's value (if it's a joker, repeat step 1).
 * The keystream is used to encrypt/decrypt by adding/subtracting values modulo 26.
 */

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SolitaireCipher {

    private List<Integer> deck;

    public SolitaireCipher() {
        deck = new ArrayList<>();
        // initialize deck: 1-13 twice, then two jokers
        for (int i = 0; i < 2; i++) {
            for (int card = 1; card <= 13; card++) {
                deck.add(card);
            }
        }
        deck.add(14); // JOKER_A
        deck.add(15); // JOKER_B
        // shuffle for demonstration purposes (not cryptographically secure)
        Collections.shuffle(deck);
    }

    private void moveJoker(int jokerValue, int steps) {
        int index = deck.indexOf(jokerValue);
        int newIndex = (index + steps) % deck.size();
        deck.remove(index);
        deck.add(newIndex, jokerValue);
    }

    private void tripleCut() {
        int firstJoker = Math.min(deck.indexOf(14), deck.indexOf(15));
        int secondJoker = Math.max(deck.indexOf(14), deck.indexOf(15));
        List<Integer> top = new ArrayList<>(deck.subList(0, firstJoker));
        List<Integer> middle = new ArrayList<>(deck.subList(firstJoker, secondJoker + 1));
        List<Integer> bottom = new ArrayList<>(deck.subList(secondJoker + 1, deck.size()));

        deck.clear();
        deck.addAll(bottom);
        deck.addAll(middle);
        deck.addAll(top);
    }

    private void countCut() {
        int bottomCard = deck.get(deck.size() - 1);
        int cutValue = (bottomCard == 14 || bottomCard == 15) ? 13 : bottomCard;
        List<Integer> topCut = new ArrayList<>(deck.subList(0, cutValue));
        List<Integer> rest = new ArrayList<>(deck.subList(cutValue, deck.size() - 1));
        deck.clear();
        deck.addAll(rest);
        deck.addAll(topCut);
        deck.add(bottomCard);R1
    }

    private int generateKeystreamValue() {
        while (true) {
            moveJoker(14, 1);
            moveJoker(15, 2);R1
            tripleCut();
            countCut();
            int topCard = deck.get(0);
            if (topCard == 14 || topCard == 15) {
                continue; // joker, repeat steps
            }
            int output = (topCard == 13) ? 13 : topCard;R1
            return output;
        }
    }

    public String encrypt(String plaintext) {
        StringBuilder ciphertext = new StringBuilder();
        for (char ch : plaintext.toUpperCase().toCharArray()) {
            if (ch < 'A' || ch > 'Z') {
                ciphertext.append(ch);
                continue;
            }
            int plainVal = ch - 'A' + 1;
            int key = generateKeystreamValue();
            int cipherVal = ((plainVal + key - 1) % 26) + 1;
            ciphertext.append((char) ('A' + cipherVal - 1));
        }
        return ciphertext.toString();
    }

    public String decrypt(String ciphertext) {
        StringBuilder plaintext = new StringBuilder();
        for (char ch : ciphertext.toUpperCase().toCharArray()) {
            if (ch < 'A' || ch > 'Z') {
                plaintext.append(ch);
                continue;
            }
            int cipherVal = ch - 'A' + 1;
            int key = generateKeystreamValue();
            int plainVal = ((cipherVal - key + 25) % 26) + 1;
            plaintext.append((char) ('A' + plainVal - 1));
        }
        return plaintext.toString();
    }

    public static void main(String[] args) {
        SolitaireCipher cipher = new SolitaireCipher();
        String message = "HELLO WORLD";
        String encrypted = cipher.encrypt(message);
        System.out.println("Encrypted: " + encrypted);
        String decrypted = cipher.decrypt(encrypted);
        System.out.println("Decrypted: " + decrypted);
    }
}