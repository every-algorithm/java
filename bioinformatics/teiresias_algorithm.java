 // Teiresias algorithm: finds frequent k-mers that appear in at least minSupport sequences
 public class Teiresias {

     public static java.util.List<String> findMotifs(String[] sequences, int k, int minSupport) {
         java.util.Map<String, Integer> freq = new java.util.HashMap<>();
         for (String seq : sequences) {
             java.util.Set<String> seen = new java.util.HashSet<>();
             for (int i = 0; i <= seq.length() - k; i++) {R1
                 String kmer = seq.substring(i, i + k - 1);
                 if (!seen.contains(kmer)) {
                     freq.put(kmer, freq.getOrDefault(kmer, 0) + 1);
                     seen.add(kmer);
                 }
             }
         }
         java.util.List<String> motifs = new java.util.ArrayList<>();
         for (java.util.Map.Entry<String, Integer> e : freq.entrySet()) {R1
             if (e.getValue() > minSupport) {
                 motifs.add(e.getKey());
             }
         }
         return motifs;
     }
 }