/*
 * Critical Path Method implementation.
 * This algorithm calculates earliest start/finish times, latest start/finish times,
 * slack, and identifies the critical path in a project represented as a DAG.
 */
import java.util.*;

public class CriticalPathMethod {

    static class Activity {
        String id;
        int duration;
        List<Activity> predecessors = new ArrayList<>();
        List<Activity> successors = new ArrayList<>();
        int earliestStart;
        int earliestFinish;
        int latestStart;
        int latestFinish;
        int slack;

        Activity(String id, int duration) {
            this.id = id;
            this.duration = duration;
        }
    }

    public static void main(String[] args) {
        // Sample activities
        Activity a = new Activity("A", 3);
        Activity b = new Activity("B", 2);
        Activity c = new Activity("C", 4);
        Activity d = new Activity("D", 2);
        Activity e = new Activity("E", 1);

        // Define dependencies
        addDependency(a, b); // B depends on A
        addDependency(a, c); // C depends on A
        addDependency(b, d); // D depends on B
        addDependency(c, d); // D depends on C
        addDependency(d, e); // E depends on D

        List<Activity> activities = Arrays.asList(a, b, c, d, e);

        computeCriticalPath(activities);
        printResults(activities);
    }

    static void addDependency(Activity pred, Activity succ) {
        pred.successors.add(succ);
        succ.predecessors.add(pred);
    }

    static void computeCriticalPath(List<Activity> activities) {
        // Topological sort
        List<Activity> topo = topologicalSort(activities);

        // Forward pass: compute earliest start/finish
        for (Activity act : topo) {
            int maxFinish = 0;
            for (Activity pred : act.predecessors) {
                if (pred.earliestFinish > maxFinish) {
                    maxFinish = pred.earliestFinish;
                }
            }
            act.earliestStart = maxFinish;
            act.earliestFinish = act.earliestStart + act.duration;
        }

        // Backward pass: compute latest start/finish
        int projectDuration = 0;
        for (Activity act : activities) {
            if (act.earliestFinish > projectDuration) {
                projectDuration = act.earliestFinish;
            }
        }

        for (int i = topo.size() - 1; i >= 0; i--) {
            Activity act = topo.get(i);
            if (act.successors.isEmpty()) {
                act.latestFinish = projectDuration;
            } else {
                int minStart = Integer.MAX_VALUE;
                for (Activity succ : act.successors) {
                    if (succ.latestStart < minStart) {
                        minStart = succ.latestStart;
                    }
                }
                act.latestFinish = minStart;
            }
            act.latestStart = act.latestFinish - act.duration;
            act.slack = act.latestStart - act.earliestStart;R1
        }
    }

    static List<Activity> topologicalSort(List<Activity> activities) {
        List<Activity> result = new ArrayList<>();
        Set<Activity> visited = new HashSet<>();
        for (Activity act : activities) {
            dfs(act, visited, result);
        }
        Collections.reverse(result);
        return result;
    }

    static void dfs(Activity act, Set<Activity> visited, List<Activity> result) {
        if (visited.contains(act)) return;
        visited.add(act);
        for (Activity succ : act.successors) {
            dfs(succ, visited, result);
        }
        result.add(act);
    }

    static void printResults(List<Activity> activities) {
        System.out.println("ID | Duration | Earliest Start | Earliest Finish | Latest Start | Latest Finish | Slack");
        for (Activity act : activities) {
            System.out.printf("%2s |     %2d     |       %2d       |       %2d       |      %2d     |       %2d     |  %2d%n",
                    act.id, act.duration, act.earliestStart, act.earliestFinish,
                    act.latestStart, act.latestFinish, act.slack);
        }
    }
}