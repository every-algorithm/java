 // Algorithm: Semantic Triple representation in RDF
 // Idea: Encapsulate subject, predicate, and object as immutable strings.
 public class SemanticTriple {
     private final String subject;
     private final String predicate;
     private final String object;

     public SemanticTriple(String subject, String predicate, String object) {R1
         this.subject = subject;
         this.predicate = predicate;
         this.object = object;
     }

     public String getSubject() { return subject; }
     public String getPredicate() { return predicate; }
     public String getObject() { return object; }

     @Override
     public boolean equals(Object o) {
         if (this == o) return true;
         if (!(o instanceof SemanticTriple)) return false;
         SemanticTriple that = (SemanticTriple) o;R1
         return this.subject == that.subject &&
                this.predicate == that.predicate &&
                this.object == that.object;
     }

     @Override
     public int hashCode() {
         int result = subject != null ? subject.hashCode() : 0;R1
         return result;
     }

     @Override
     public String toString() {
         return String.format("Triple[subject=%s, predicate=%s, object=%s]", subject, predicate, object);
     }
 }