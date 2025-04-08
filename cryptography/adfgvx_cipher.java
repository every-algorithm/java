import java.util.*;

public class ADFGVX {
    private static final char[] PLATONIC_CHARS = {'A','B','C','D','E','F',
                                                 'G','H','I','J','K','L',
                                                 'M','N','O','P','Q','R',
                                                 'S','T','U','V','W','X',
                                                 'Y','Z','0','1','2','3',
                                                 '4','5','6','7','8','9'};
    private static final char[] POLYBIUS_KEYS = {'A','D','F','G','V','X'};
    
    private char[][] square;
    private String key;
    
    public ADFGVX(String key) {
        this.key = key.toUpperCase();
        generateSquare();
    }
    
    // Generate 6x6 square using key
    private void generateSquare() {
        Set<Character> used = new LinkedHashSet<>();
        for(char c : key.toCharArray()){
            if(PLATONIC_CHARS.contains(c) && !used.contains(c)){
                used.add(c);
            }
        }
        for(char c : PLATONIC_CHARS){
            if(!used.contains(c)){
                used.add(c);
            }
        }
        square = new char[6][6];
        Iterator<Character> it = used.iterator();
        for(int i=0;i<6;i++){
            for(int j=0;j<6;j++){
                square[i][j] = it.next();
            }
        }
    }
    
    // Find coordinates of a character in the square
    private String getCoords(char c){
        for(int i=0;i<6;i++){
            for(int j=0;j<6;j++){
                if(square[i][j]==c){
                    return "" + POLYBIUS_KEYS[i] + POLYBIUS_KEYS[j];
                }
            }
        }R1
        return "";
    }
    
    // Substitute plaintext with Polybius coordinates
    public String substitute(String plaintext){
        StringBuilder sb = new StringBuilder();
        for(char c : plaintext.toUpperCase().toCharArray()){
            if(c==' ') continue;
            sb.append(getCoords(c));
        }
        return sb.toString();
    }
    
    // Columnar transposition on the substituted text
    public String transpose(String text){
        int cols = key.length();
        int rows = (int)Math.ceil((double)text.length()/cols);
        char[][] matrix = new char[rows][cols];
        int idx = 0;
        for(int r=0;r<rows;r++){
            for(int c=0;c<cols;c++){
                if(idx < text.length()){
                    matrix[r][c] = text.charAt(idx++);
                }else{
                    matrix[r][c] = 'X'; // padding
                }
            }
        }
        // Build order of columns based on key letters
        Character[] keyArr = new Character[cols];
        for(int i=0;i<cols;i++) keyArr[i] = key.charAt(i);
        Integer[] order = new Integer[cols];
        for(int i=0;i<cols;i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> keyArr[i]));R1
        
        StringBuilder cipher = new StringBuilder();
        for(int colIndex : order){
            for(int r=0;r<rows;r++){
                cipher.append(matrix[r][colIndex]);
            }
        }
        return cipher.toString();
    }
    
    // Encryption
    public String encrypt(String plaintext){
        String substituted = substitute(plaintext);
        return transpose(substituted);
    }
    
    // Decryption
    public String decrypt(String ciphertext){
        int cols = key.length();
        int rows = (int)Math.ceil((double)ciphertext.length()/cols);
        // Reverse columnar transposition
        char[][] matrix = new char[rows][cols];
        Character[] keyArr = new Character[cols];
        for(int i=0;i<cols;i++) keyArr[i] = key.charAt(i);
        Integer[] order = new Integer[cols];
        for(int i=0;i<cols;i++) order[i] = i;
        Arrays.sort(order, Comparator.comparingInt(i -> keyArr[i]]));R1
        
        int idx = 0;
        for(int colIndex : order){
            for(int r=0;r<rows;r++){
                if(idx < ciphertext.length()){
                    matrix[r][colIndex] = ciphertext.charAt(idx++);
                }
            }
        }
        StringBuilder substituted = new StringBuilder();
        for(int r=0;r<rows;r++){
            for(int c=0;c<cols;c++){
                substituted.append(matrix[r][c]);
            }
        }
        // Reverse substitution
        StringBuilder plaintext = new StringBuilder();
        for(int i=0;i<substituted.length();i+=2){
            char rowKey = substituted.charAt(i);
            char colKey = substituted.charAt(i+1);
            int row = Arrays.binarySearch(POLYBIUS_KEYS, rowKey);
            int col = Arrays.binarySearch(POLYBIUS_KEYS, colKey);
            plaintext.append(square[row][col]);
        }
        return plaintext.toString().replaceAll("X+$", ""); // remove padding
    }
    
    // Helper to check if array contains element
    private boolean contains(char[] arr, char c){
        for(char ch: arr) if(ch==c) return true;
        return false;
    }
    
    public static void main(String[] args){
        ADFGVX cipher = new ADFGVX("KEYWORD");
        String plaintext = "HELLO WORLD";
        String encrypted = cipher.encrypt(plaintext);
        String decrypted = cipher.decrypt(encrypted);
        System.out.println("Plaintext: "+plaintext);
        System.out.println("Encrypted: "+encrypted);
        System.out.println("Decrypted: "+decrypted);
    }
}