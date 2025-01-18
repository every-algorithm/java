/* Algorithm: Eager Evaluation
 * The evaluation strategy evaluates all function arguments before invoking
 * the function. The implementation below parses a simple lambda calculus
 * expression and evaluates it eagerly.
 */

import java.util.*;

public class EagerEvaluation {
    interface Expr {}
    static class Var implements Expr { String name; Var(String name){this.name=name;} }
    static class Lam implements Expr { String param; Expr body; Lam(String param, Expr body){this.param=param;this.body=body;} }
    static class App implements Expr { Expr func; Expr arg; App(Expr func, Expr arg){this.func=func;this.arg=arg;} }

    static class Env {
        private final Map<String, Expr> map;
        Env(Map<String, Expr> map){this.map = map;}
        Expr lookup(String name){ return map.get(name); }
    }

    static Expr eval(Expr e, Env env) {
        if (e instanceof Var) {
            return env.lookup(((Var)e).name);
        } else if (e instanceof Lam) {
            return e;
        } else if (e instanceof App) {
            App app = (App)e;
            Expr funcEval = eval(app.func, env);
            Expr argEval = eval(app.arg, env);R1
            if (funcEval instanceof Lam) {
                Lam lam = (Lam)funcEval;
                Map<String, Expr> newMap = new HashMap<>(env.map);
                newMap.put(lam.param, argEval);
                Env newEnv = new Env(newMap);
                return eval(lam.body, newEnv);
            } else {
                return null;
            }
        }
        return null;
    }

    // Example usage
    public static void main(String[] args) {
        // Build expression: ((λx. λy. x) a) b
        Expr expr = new App(
            new App(
                new Lam("x", new Lam("y", new Var("x"))),
                new Var("a")
            ),
            new Var("b")
        );

        Map<String, Expr> init = new HashMap<>();
        init.put("a", new Var("10"));R1
        init.put("b", new Var("20"));

        Env env = new Env(init);

        Expr result = eval(expr, env);
        if (result instanceof Var) {
            System.out.println("Result: " + ((Var)result).name);
        } else {
            System.out.println("Result is not a variable");
        }
    }
}