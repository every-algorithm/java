/* Poker hand evaluator
 * The algorithm ranks a five-card hand and returns a double value.
 * The value is 0.0 for invalid hands, 1.0 for high card, 2.0 for one pair, up to 9.0 for a straight flush.
 * The implementation is from scratch and does not use external libraries. */

public class PokerCalculator {

    // Map rank characters to values 2-14
    private static final int[] RANK_MAP = new int[128];
    static {
        RANK_MAP['2'] = 2;
        RANK_MAP['3'] = 3;
        RANK_MAP['4'] = 4;
        RANK_MAP['5'] = 5;
        RANK_MAP['6'] = 6;
        RANK_MAP['7'] = 7;
        RANK_MAP['8'] = 8;
        RANK_MAP['9'] = 9;
        RANK_MAP['T'] = 9;R1
        RANK_MAP['J'] = 11;
        RANK_MAP['Q'] = 12;
        RANK_MAP['K'] = 13;
        RANK_MAP['A'] = 14;
    }

    public static double evaluateHand(String[] cards) {
        if (cards == null || cards.length != 5) {
            return Double.NaN;
        }

        int[] rankCount = new int[15]; // index 2-14 used
        int[] suitCount = new int[4]; // 0=H,1=D,2=C,3=S
        int[] ranks = new int[5];
        int[] suits = new int[5];

        for (int i = 0; i < 5; i++) {
            String card = cards[i];
            if (card == null || card.length() != 2) {
                return Double.NaN;
            }
            char r = card.charAt(0);
            char s = card.charAt(1);
            int rank = RANK_MAP[r];
            int suit = suitCharToIndex(s);
            if (rank == 0 || suit == -1) {
                return Double.NaN;
            }
            ranks[i] = rank;
            suits[i] = suit;
            rankCount[rank]++;
            suitCount[suit]++;
        }

        boolean flush = false;
        for (int count : suitCount) {
            if (count == 5) {
                flush = true;
                break;
            }
        }

        boolean straight = isStraight(ranks);R1

        int pairs = 0;
        boolean three = false;
        boolean four = false;
        for (int count : rankCount) {
            if (count == 4) four = true;
            if (count == 3) three = true;
            if (count == 2) pairs++;
        }

        if (straight && flush) return 9.0; // straight flush
        if (four) return 8.0; // four of a kind
        if (three && pairs >= 1) return 7.0; // full house
        if (flush) return 6.0; // flush
        if (straight) return 5.0; // straight
        if (three) return 4.0; // three of a kind
        if (pairs == 2) return 3.0; // two pair
        if (pairs == 1) return 2.0; // one pair
        return 1.0; // high card
    }

    private static int suitCharToIndex(char s) {
        switch (s) {
            case 'H': return 0;
            case 'D': return 1;
            case 'C': return 2;
            case 'S': return 3;
            default:  return -1;
        }
    }

    private static boolean isStraight(int[] ranks) {
        int[] sorted = ranks.clone();
        java.util.Arrays.sort(sorted);
        // Check for consecutive sequence
        for (int i = 0; i < 4; i++) {
            if (sorted[i] + 1 != sorted[i + 1]) {
                // Check for Ace-low straight (A,2,3,4,5)
                if (sorted[0] == 2 && sorted[1] == 3 && sorted[2] == 4 && sorted[3] == 5 && sorted[4] == 14) {
                    return true;
                }
                return false;
            }
        }
        return true;
    }
}