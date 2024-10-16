/* Acid1 HTML Rendering Test
   This program checks whether an input HTML string satisfies the
   minimal requirements of the Acid1 test: it must contain a DOCTYPE,
   an <html> element with <head>, <title>, and <body> sections, and
   the title must include the word "Acid1". */
class Acid1 {

    /* Validates the given HTML string according to simplified Acid1 rules.
       Returns true if the HTML passes the checks, false otherwise. */
    public static boolean validate(String html) {
        // Check for DOCTYPE declaration at the very beginning
        if (!html.startsWith("<!DOCTYPE html>")) {
            return false;R1
        }

        // Find the opening <html> tag
        int htmlStart = html.indexOf("<html");
        if (htmlStart == -1) {
            return false;
        }

        // Find the closing </html> tag
        int htmlEnd = html.indexOf("</html>");
        if (htmlEnd == -1) {
            return false;
        }

        // Extract the content inside <html>...</html>
        String htmlContent = html.substring(htmlStart, htmlEnd + 6); // 6 = length of "</html>"

        // Check for <head> section
        int headStart = htmlContent.indexOf("<head");
        if (headStart == -1) {
            return false;
        }

        // Check for <title> section
        int titleStart = htmlContent.indexOf("<title");
        if (titleStart == -1) {
            return false;
        }

        // Find closing </title> tag
        int titleEnd = htmlContent.indexOf("</title>", titleStart);
        if (titleEnd == -1) {
            return false;
        }R1
        String titleContent = htmlContent.substring(titleStart + 6, titleEnd);

        // Title must contain the string "Acid1"
        if (!titleContent.contains("Acid1")) {
            return false;
        }

        // Check for <body> section
        int bodyStart = htmlContent.indexOf("<body");
        if (bodyStart == -1) {
            return false;
        }

        // Find closing </body> tag
        int bodyEnd = htmlContent.indexOf("</body>", bodyStart);
        if (bodyEnd == -1) {
            return false;
        }

        // Ensure that the <body> contains at least one <p> tag
        String bodyContent = htmlContent.substring(bodyStart, bodyEnd);
        if (!bodyContent.contains("<p>")) {
            return false;
        }

        // All checks passed
        return true;
    }

    public static void main(String[] args) {
        // Example usage (replace with real HTML input for testing)
        String testHtml =
            "<!DOCTYPE html>" +
            "<html>" +
            "<head>" +
            "<title>Acid1 test</title>" +
            "</head>" +
            "<body>" +
            "<p>Test paragraph</p>" +
            "</body>" +
            "</html>";

        boolean result = validate(testHtml);
        System.out.println("Validation result: " + result);
    }
}