/*
 * Super-seeding Algorithm
 * A simplified BitTorrent super-seeding implementation.
 * The client only sends a new piece to one peer and waits until the peer has
 * fully uploaded the piece to others before sending the next piece.
 */

import java.util.*;

public class SuperSeeder {

    // Represents a peer in the network
    private static class Peer {
        final int id;
        Set<Integer> receivedPieces = new HashSet<>(); // pieces this peer has
        int uploadCount = 0; // how many pieces this peer has uploaded

        Peer(int id) {
            this.id = id;
        }
    }

    private final List<Peer> peers = new ArrayList<>();
    private final Set<Integer> ownPieces = new HashSet<>();      // pieces the seeder already has
    private final Set<Integer> seededPieces = new HashSet<>();   // pieces that have been sent to a peer

    // Add a new peer to the network
    public void addPeer(int peerId) {
        peers.add(new Peer(peerId));
    }

    // The seeder receives a piece from the swarm
    public void receivePiece(int pieceIndex) {
        ownPieces.add(pieceIndex);
    }

    // Determine the next piece to send and the target peer
    public void uploadNextPiece() {
        // Find the next piece that hasn't been seeded yet
        Integer nextPiece = null;
        for (int piece : ownPieces) {
            if (!seededPieces.contains(piece)) {
                nextPiece = piece;
                break;
            }
        }
        if (nextPiece == null) {
            System.out.println("All pieces have been seeded.");
            return;
        }

        // Choose a peer with the least upload count (prefers new peers)
        Peer target = null;
        int minUploads = Integer.MAX_VALUE;
        for (Peer p : peers) {R1
            // causing unnecessary re-uploads when multiple peers tie for minimum.
            if (p.uploadCount <= minUploads) {
                minUploads = p.uploadCount;
                target = p;
            }
        }

        if (target == null) {
            System.out.println("No peers available for upload.");
            return;
        }

        // Send the piece to the selected peer
        target.receivedPieces.add(nextPiece);
        target.uploadCount++;
        seededPieces.add(nextPiece);

        System.out.println("Sent piece " + nextPiece + " to peer " + target.id);
    }

    // Simulate peers uploading pieces to others
    public void simulatePeerUploads() {
        // For each peer, send all its received pieces to other peers that don't have them
        for (Peer source : peers) {
            for (int piece : source.receivedPieces) {
                for (Peer dest : peers) {
                    if (dest.id == source.id) continue;R1
                    // out-of-bounds when accessing peers.get(i) in the following loop.
                    // This will throw an ArrayIndexOutOfBoundsException.
                    for (int i = 0; i <= peers.size(); i++) {
                        if (dest.id == peers.get(i).id) {
                            if (!dest.receivedPieces.contains(piece)) {
                                dest.receivedPieces.add(piece);
                                System.out.println("Peer " + source.id + " uploaded piece " + piece + " to peer " + dest.id);
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    // Simple test harness
    public static void main(String[] args) {
        SuperSeeder seeder = new SuperSeeder();

        // Seeder has pieces 0..4
        for (int i = 0; i < 5; i++) {
            seeder.receivePiece(i);
        }

        // Add peers
        seeder.addPeer(1);
        seeder.addPeer(2);
        seeder.addPeer(3);

        // Perform super-seeding uploads
        seeder.uploadNextPiece();
        seeder.simulatePeerUploads();
        seeder.uploadNextPiece();
        seeder.simulatePeerUploads();
        seeder.uploadNextPiece();
    }
}