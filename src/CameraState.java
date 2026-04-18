import transforms.Camera;
import transforms.Mat4;
import transforms.Mat4OrthoRH;
import transforms.Mat4PerspRH;
import transforms.Vec3D;

public class CameraState {

    private static final double DEFAULT_FOV = Math.toRadians(60.0);
    private static final double DEFAULT_ORTHO_HEIGHT = 6.0;
    private static final double NEAR_PLANE = 0.1;
    private static final double FAR_PLANE = 100.0;

    private Camera camera;
    private ProjectionMode projectionMode = ProjectionMode.PERSPECTIVE;
    private double orthographicHeight = DEFAULT_ORTHO_HEIGHT;

    public CameraState() {
        reset();
    }

    public void reset() {
        camera = new Camera()
                .withFirstPerson(true)
                .withPosition(new Vec3D(-5.0, -6.0, 3.0))
                .withAzimuth(Math.toRadians(55.0))
                .withZenith(Math.toRadians(-18.0));
        projectionMode = ProjectionMode.PERSPECTIVE;
        orthographicHeight = DEFAULT_ORTHO_HEIGHT;
    }

    public void addYaw(double radians) {
        camera = camera.addAzimuth(radians);
    }

    public void addPitch(double radians) {
        camera = camera.addZenith(radians);
    }

    public void moveForward(double distance) {
        camera = camera.forward(distance);
    }

    public void moveRight(double distance) {
        camera = camera.right(distance);
    }

    public void moveUp(double distance) {
        camera = camera.up(distance);
    }

    public void adjustOrthographicHeight(double delta) {
        orthographicHeight = clamp(orthographicHeight + delta, 2.0, 30.0);
    }

    public void toggleProjection() {
        projectionMode = projectionMode.toggle();
    }

    public ProjectionMode getProjectionMode() {
        return projectionMode;
    }

    public Mat4 getViewMatrix() {
        return camera.getViewMatrix();
    }

    public Mat4 getProjectionMatrix(int width, int height) {
        int safeWidth = Math.max(width, 1);
        int safeHeight = Math.max(height, 1);
        if (projectionMode == ProjectionMode.PERSPECTIVE) {
            return new Mat4PerspRH(DEFAULT_FOV, (double) safeHeight / safeWidth, NEAR_PLANE, FAR_PLANE);
        }

        double aspect = (double) safeWidth / safeHeight;
        return new Mat4OrthoRH(orthographicHeight * aspect, orthographicHeight, NEAR_PLANE, FAR_PLANE);
    }

    public Vec3D getPosition() {
        return camera.getPosition();
    }

    private static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }
}
