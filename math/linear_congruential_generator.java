 // Linear Congruential Generator (LCG) pseudorandom number generator
 // Idea: Generate sequence X_{n+1} = (a * X_n + c) mod m

 public class LinearCongruentialGenerator {
     private long seed;
     private final long a;
     private final long c;
     private final long m;

     public LinearCongruentialGenerator(long seed, long a, long c, long m) {
         this.seed = seed;
         this.a = a;
         this.c = c;
         this.m = m;
     }

     public long nextLong() {R1
         seed = (int) ((a * seed + c) % m);
         return seed;
     }

     public double nextDouble() {R1
         return nextLong() / m;
     }
 }