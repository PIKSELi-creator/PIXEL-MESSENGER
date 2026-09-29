import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

public class RateLimiter {

    private static final long WINDOW_MS = 60_000L;
    private static final int MAX_REQUESTS = 30;

    private final Map<String, Window> clients =
            new ConcurrentHashMap<>();

    private final ScheduledExecutorService cleaner =
            Executors.newSingleThreadScheduledExecutor(
                    new DaemonThreadFactory()
            );

    public RateLimiter() {

        cleaner.scheduleAtFixedRate(
                this::cleanup,
                1,
                1,
                TimeUnit.MINUTES
        );
    }

    public boolean allow(String key) {

        long now =
                System.currentTimeMillis();

        Window window =
                clients.computeIfAbsent(
                        key,
                        ignored -> new Window(now)
                );

        synchronized (window) {

            if (now - window.startedAt >= WINDOW_MS) {

                window.startedAt = now;
                window.requests = 0;
            }

            if (window.requests >= MAX_REQUESTS) {
                return false;
            }

            window.requests++;

            return true;
        }
    }

    public void cleanup() {

        long now =
                System.currentTimeMillis();

        clients.entrySet().removeIf(
                entry ->
                        now -
                        entry.getValue().startedAt
                                >= WINDOW_MS * 2
        );
    }

    public void shutdown() {
        cleaner.shutdownNow();
    }

    private static class Window {

        long startedAt;
        int requests;

        Window(long startedAt) {

            this.startedAt = startedAt;
            this.requests = 0;
        }
    }

    private static class DaemonThreadFactory
            implements ThreadFactory {

        @Override
        public Thread newThread(Runnable runnable) {

            Thread thread =
                    new Thread(
                            runnable,
                            "pixel-chat-rate-cleaner"
                    );

            thread.setDaemon(true);

            return thread;
        }
    }
}
