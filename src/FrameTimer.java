public class FrameTimer {

    private long lastFrameNanos = -1L;
    private double deltaSeconds;
    private double elapsedSeconds;

    public void reset() {
        lastFrameNanos = -1L;
        deltaSeconds = 0.0;
        elapsedSeconds = 0.0;
    }

    public void beginFrame() {
        long now = System.nanoTime();
        if (lastFrameNanos < 0L) {
            lastFrameNanos = now;
            deltaSeconds = 0.0;
            return;
        }

        deltaSeconds = (now - lastFrameNanos) / 1_000_000_000.0;
        elapsedSeconds += deltaSeconds;
        lastFrameNanos = now;
    }

    public double getDeltaSeconds() {
        return deltaSeconds;
    }

    public double getElapsedSeconds() {
        return elapsedSeconds;
    }
}
