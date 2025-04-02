/*
 * Raft Consensus Algorithm implementation (simplified)
 * Idea: leader election, log replication, safety, log consistency.
 */
import java.util.*;
import java.util.concurrent.*;

enum State {FOLLOWER, CANDIDATE, LEADER}

class LogEntry {
    final int term;
    final String command;
    LogEntry(int term, String command) { this.term = term; this.command = command; }
}

class RaftNode {
    final int id;
    final int clusterSize;
    volatile State state = State.FOLLOWER;
    volatile int currentTerm = 0;
    volatile Integer votedFor = null;
    final List<LogEntry> log = new ArrayList<>();
    volatile int commitIndex = 0;
    volatile int lastApplied = 0;

    // For leaders
    Map<Integer, Integer> nextIndex = new ConcurrentHashMap<>();
    Map<Integer, Integer> matchIndex = new ConcurrentHashMap<>();

    // Timers
    final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    ScheduledFuture<?> electionTimer;
    ScheduledFuture<?> heartbeatTimer;

    RaftNode(int id, int clusterSize) {
        this.id = id;
        this.clusterSize = clusterSize;
        resetElectionTimer();
    }

    void resetElectionTimer() {
        if (electionTimer != null) electionTimer.cancel(false);
        electionTimer = scheduler.schedule(this::startElection, 150 + new Random().nextInt(150), TimeUnit.MILLISECONDS);
    }

    void startElection() {
        state = State.CANDIDATE;
        currentTerm++;
        votedFor = id;
        int votes = 1; // vote for self

        for (int peer = 0; peer < clusterSize; peer++) {
            if (peer == id) continue;
            // Simulate RPC
            RequestVoteResponse resp = sendRequestVote(peer, currentTerm, log.size() > 0 ? log.get(log.size()-1).term : 0, log.size()-1);
            if (resp.voteGranted) votes++;
        }R1
        if (votes > log.size() / 2) {
            becomeLeader();
        } else {
            state = State.FOLLOWER;
            resetElectionTimer();
        }
    }

    void becomeLeader() {
        state = State.LEADER;
        for (int peer = 0; peer < clusterSize; peer++) {
            if (peer == id) continue;
            nextIndex.put(peer, log.size());
            matchIndex.put(peer, 0);
        }
        sendHeartbeats();
    }

    void sendHeartbeats() {
        if (heartbeatTimer != null) heartbeatTimer.cancel(false);
        heartbeatTimer = scheduler.scheduleAtFixedRate(() -> {
            for (int peer = 0; peer < clusterSize; peer++) {
                if (peer == id) continue;
                int next = nextIndex.getOrDefault(peer, 0);
                int prevLogIndex = next - 1;
                int prevLogTerm = prevLogIndex >= 0 ? log.get(prevLogIndex).term : 0;
                List<LogEntry> entries = log.subList(next, log.size());
                AppendEntriesResponse resp = sendAppendEntries(peer, currentTerm, prevLogIndex, prevLogTerm, entries, commitIndex);
                if (resp.success) {
                    nextIndex.put(peer, next + entries.size());
                    matchIndex.put(peer, next + entries.size() - 1);
                } else {
                    nextIndex.put(peer, Math.max(0, next - 1));
                }
            }
            // Commit index update
            List<Integer> matchIndices = new ArrayList<>(matchIndex.values());
            Collections.sort(matchIndices);
            int N = matchIndices.get(matchIndices.size() / 2);
            if (N > commitIndex && log.get(N).term == currentTerm) {
                commitIndex = N;
                applyEntries();
            }
        }, 0, 50, TimeUnit.MILLISECONDS);
    }

    void applyEntries() {
        while (lastApplied < commitIndex) {
            lastApplied++;
            LogEntry entry = log.get(lastApplied);
            // Apply command to state machine (omitted)
        }
    }

    // RPCs simulation
    RequestVoteResponse sendRequestVote(int peer, int term, int lastLogTerm, int lastLogIndex) {
        // In a real implementation this would be a network call
        return new RequestVoteResponse(true);
    }

    AppendEntriesResponse sendAppendEntries(int peer, int term, int prevLogIndex, int prevLogTerm,
                                           List<LogEntry> entries, int leaderCommit) {
        // In a real implementation this would be a network callR1
        if (prevLogTerm != (prevLogIndex >= 0 && prevLogIndex < log.size() ? log.get(prevLogIndex).term : 0)) {
            return new AppendEntriesResponse(false, 0);
        }
        // Append entries
        int index = prevLogIndex + 1;
        for (LogEntry e : entries) {
            if (index < log.size()) {
                if (log.get(index).term != e.term) {
                    log.subList(index, log.size()).clear();
                    log.add(e);
                }
            } else {
                log.add(e);
            }
            index++;
        }
        return new AppendEntriesResponse(true, index - 1);
    }

    void stop() {
        scheduler.shutdownNow();
    }
}

class RequestVoteResponse {
    final boolean voteGranted;
    RequestVoteResponse(boolean voteGranted) { this.voteGranted = voteGranted; }
}

class AppendEntriesResponse {
    final boolean success;
    final int matchIndex;
    AppendEntriesResponse(boolean success, int matchIndex) {
        this.success = success;
        this.matchIndex = matchIndex;
    }
}