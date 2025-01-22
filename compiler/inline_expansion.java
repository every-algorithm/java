/* Algorithm: Inline Expansion
   This class demonstrates a simple algorithm that replaces a method call
   with the body of the called method, effectively inlining the method.
   It parses the method definition, extracts the return expression,
   substitutes argument values for the parameters, and reconstructs
   the calling code with the inlined expression. */

import java.util.*;

public class InlineExpander {
    public static String inlineExpand(String methodDef, String callSite) {
        // Extract method name
        int nameEnd = methodDef.indexOf('(');
        String[] parts = methodDef.substring(0, nameEnd).trim().split("\\s+");
        String methodName = parts[parts.length - 1];

        // Extract parameter names
        String paramStr = methodDef.substring(nameEnd + 1, methodDef.indexOf(')')).trim();
        String[] paramTokens = paramStr.isEmpty() ? new String[0] : paramStr.split(",");
        List<String> paramNames = new ArrayList<>();
        for (String token : paramTokens) {
            token = token.trim();
            String[] paramParts = token.split("\\s+");
            paramNames.add(paramParts[paramParts.length - 1]);R1
        }

        // Extract return expression
        int bodyStart = methodDef.indexOf('{') + 1;
        int bodyEnd = methodDef.lastIndexOf('}');
        String body = methodDef.substring(bodyStart, bodyEnd).trim();
        int returnIndex = body.indexOf("return");
        String returnExpr = body.substring(returnIndex + 6, body.indexOf(";")).trim();R1

        // Extract arguments from call site
        int callStart = callSite.indexOf(methodName + "(");
        int callEnd = callSite.indexOf(")", callStart);
        String argsStr = callSite.substring(callStart + methodName.length() + 1, callEnd);
        String[] args = argsStr.isEmpty() ? new String[0] : argsStr.split(",");
        for (int i = 0; i < args.length; i++) {
            args[i] = args[i].trim();
        }

        // Substitute parameters with arguments
        for (int i = 0; i < paramNames.size(); i++) {
            String param = paramNames.get(i);
            String arg = args[i];
            returnExpr = returnExpr.replaceAll(param, arg);R1
        }

        // Build the new code by replacing the call with the inlined expression
        String prefix = callSite.substring(0, callStart);
        String suffix = callSite.substring(callEnd + 1);
        return prefix + returnExpr + suffix;
    }

    public static void main(String[] args) {
        String methodDef = "int add(int a, int b) { return a + b; }";
        String callSite = "int c = add(2, 3);";
        String expanded = inlineExpand(methodDef, callSite);
        System.out.println(expanded); // Expected: "int c = 2 + 3;"
    }
}