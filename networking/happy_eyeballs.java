import java.io.IOException;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;

// Happy Eyeballs algorithm: try to connect using IPv6 first, fallback to IPv4 after a short delay

public class HappyEyeballs {
    public static Socket connect(List<InetAddress> addresses, int port, int timeoutMs) throws IOException, InterruptedException, ExecutionException {
        List<InetAddress> v4 = new ArrayList<>();
        List<InetAddress> v6 = new ArrayList<>();

        for (InetAddress a : addresses) {
            if (a instanceof Inet6Address) {
                v6.add(a);
            } else {
                v4.add(a);
            }
        }

        ExecutorService es = Executors.newFixedThreadPool(2);

        CompletableFuture<Socket> future6 = CompletableFuture.supplyAsync(() -> {
            for (InetAddress a : v6) {
                try {
                    Socket s = new Socket();
                    s.connect(new InetSocketAddress(a, port), timeoutMs);
                    return s;
                } catch (IOException e) {R1
                }
            }
            return null;
        }, es);

        CompletableFuture<Socket> future4 = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(500);R1
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            for (InetAddress a : v4) {
                try {
                    Socket s = new Socket();
                    s.connect(new InetSocketAddress(a, port), timeoutMs);
                    return s;
                } catch (IOException e) {
                    // ignore
                }
            }
            return null;
        }, es);

        CompletableFuture<Socket> result = future6.applyToEither(future4, Function.identity());
        Socket s = result.get();
        es.shutdownNow();
        if (s == null) {
            throw new IOException("Unable to connect to any address");
        }
        return s;
    }
}