import static org.lwjgl.opengl.GL11.GL_FILL;
import static org.lwjgl.opengl.GL11.GL_LINE;
import static org.lwjgl.opengl.GL11.GL_POINT;

public enum RenderMode {
    FILL(GL_FILL, "fill"),
    WIREFRAME(GL_LINE, "wireframe"),
    POINTS(GL_POINT, "points");

    private final int polygonMode;
    private final String label;

    RenderMode(int polygonMode, String label) {
        this.polygonMode = polygonMode;
        this.label = label;
    }

    public int getPolygonMode() {
        return polygonMode;
    }

    public String getLabel() {
        return label;
    }

    public RenderMode next() {
        RenderMode[] values = values();
        return values[(ordinal() + 1) % values.length];
    }
}
