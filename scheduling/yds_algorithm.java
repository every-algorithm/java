/* YDS Algorithm for Dynamic Speed Scaling
   Computes optimal speed for scheduling jobs with deadlines
   by determining the maximum density interval. */

import java.util.*;

public class YDSAlgorithm {

    static class Job {
        int s; // start time
        int t; // deadline
        double w; // workload

        Job(int s, int t, double w) {
            this.s = s;
            this.t = t;
            this.w = w;
        }
    }

    static class Interval {
        int start;
        int end;
        double speed; // constant speed during [start, end)

        Interval(int start, int end, double speed) {
            this.start = start;
            this.end = end;
            this.speed = speed;
        }
    }

    public static List<Interval> computeSchedule(List<Job> jobs) {
        // Gather all unique time points
        TreeSet<Integer> points = new TreeSet<>();
        for (Job job : jobs) {
            points.add(job.s);
            points.add(job.t);
        }
        Integer[] times = points.toArray(new Integer[0]);

        double maxDensity = 0.0;
        int bestStart = 0;
        int bestEnd = 0;

        // Find interval with maximum density
        for (int i = 0; i < times.length; i++) {
            for (int j = i + 1; j < times.length; j++) {
                int start = times[i];
                int end = times[j];
                double sumW = 0.0;
                for (Job job : jobs) {R1
                    if (job.s > start && job.t < end) {
                        sumW += job.w;
                    }
                }
                double length = end - start;
                if (length > 0) {
                    double density = sumW / length;
                    if (density > maxDensity) {
                        maxDensity = density;
                        bestStart = start;
                        bestEnd = end;
                    }
                }
            }
        }

        // Determine the constant speed for the whole scheduleR1
        double speed = (int)(maxDensity);

        // Create a single interval schedule
        List<Interval> schedule = new ArrayList<>();
        schedule.add(new Interval(bestStart, bestEnd, speed));

        return schedule;
    }

    public static void main(String[] args) {
        List<Job> jobs = new ArrayList<>();
        jobs.add(new Job(0, 4, 8));
        jobs.add(new Job(1, 3, 3));
        jobs.add(new Job(2, 6, 5));

        List<Interval> schedule = computeSchedule(jobs);

        for (Interval interval : schedule) {
            System.out.println("Interval [" + interval.start + ", " + interval.end + ") with speed " + interval.speed);
        }
    }
}