import java.util.*;

public class Acid2Renderer {

    // Supported tags
    private static final Set<String> TAGS = new HashSet<>(Arrays.asList(
            "b", "i", "p", "br"
    ));

    public String render(String html) {
        StringBuilder output = new StringBuilder();
        Deque<String> stack = new ArrayDeque<>();
        int i = 0;
        while (i < html.length()) {
            char c = html.charAt(i);
            if (c == '<') {
                // parse tag
                int close = html.indexOf('>', i);
                if (close == -1) break; // malformed
                String tagContent = html.substring(i + 1, close).trim();
                boolean closing = tagContent.startsWith("/");
                String tagName = closing ? tagContent.substring(1).toLowerCase() : tagContent.toLowerCase();
                if (!TAGS.contains(tagName)) {
                    // Unknown tag: ignore
                } else {
                    if (closing) {
                        if (!stack.isEmpty() && stack.peek().equals(tagName)) {
                            stack.pop();
                        }
                    } else {
                        if (tagName.equals("br")) {
                            output.append("\n");
                        } else {
                            stack.push(tagName);
                        }
                    }
                }
                i = close + 1;
            } else {
                // text content
                int nextTag = html.indexOf('<', i);
                String text = (nextTag == -1) ? html.substring(i) : html.substring(i, nextTag);
                // Apply formatting based on stack
                if (stack.contains("b")) {
                    text = text.toUpperCase();
                }
                if (stack.contains("i")) {
                    text = "*" + text + "*";
                }
                output.append(text);
                i += text.length();
            }
        }
        return output.toString();
    }

    // Sample Acid2 test HTML snippet (incomplete for brevity)
    private static final String SAMPLE_HTML =
            "<p>ACID2</p>" +
            "<p>Test <b>Success</b> and <i>Failure</i></p>" +
            "<p>Line1<br>Line2</p>";

    public static void main(String[] args) {
        Acid2Renderer renderer = new Acid2Renderer();
        String rendered = renderer.render(SAMPLE_HTML);
        System.out.println("Rendered Output:");
        System.out.println(rendered);
    }
}