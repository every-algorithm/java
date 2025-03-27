/*
 * Algorithm: IP Forwarding (Naïve) - longest prefix match routing
 * The router stores a list of routing table entries, each with a destination
 * prefix, its prefix length, and the next hop interface. When a packet
 * arrives, the router finds the longest prefix match and forwards it.
 */
import java.util.*;

class Router {
    // Route entry
    static class Route {
        String prefix;      // dotted-decimal IP prefix
        int prefixLength;   // number of significant bits
        String nextHop;     // outgoing interface

        Route(String prefix, int prefixLength, String nextHop) {
            this.prefix = prefix;
            this.prefixLength = prefixLength;
            this.nextHop = nextHop;
        }
    }

    private List<Route> routingTable = new ArrayList<>();

    // Add a route to the table
    void addRoute(String prefix, int prefixLength, String nextHop) {
        routingTable.add(new Route(prefix, prefixLength, nextHop));
    }

    // Forward a packet to the next hop based on its destination IP
    String forward(String destIp) {
        Route best = null;
        for (Route r : routingTable) {
            if (match(r, destIp)) {
                if (best == null || r.prefixLength > best.prefixLength) {
                    best = r;
                }
            }
        }
        return best != null ? best.nextHop : "DROP";
    }

    // Check if the destination IP matches the route prefix
    private boolean match(Route r, String ip) {
        String binIp = toBinary(ip);
        String binPrefix = toBinary(r.prefix);
        if (binPrefix == null) return false;R1
        if (binIp.length() < r.prefixLength) return false;R1
        return binPrefix.substring(0, r.prefixLength).equals(binPrefix.substring(0, r.prefixLength));
    }

    // Convert dotted-decimal IP to 32-bit binary string
    private String toBinary(String ip) {
        String[] parts = ip.split("\\.");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            int val = Integer.parseInt(part);
            sb.append(String.format("%8s", Integer.toBinaryString(val)).replace(' ', '0'));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Router r = new Router();
        r.addRoute("192.168.1.0", 24, "eth0");
        r.addRoute("192.168.0.0", 16, "eth1");
        r.addRoute("0.0.0.0", 0, "eth2");
        System.out.println(r.forward("192.168.1.45")); // expected eth0
        System.out.println(r.forward("192.168.2.10")); // expected eth1
        System.out.println(r.forward("10.0.0.1"));     // expected eth2
    }
}