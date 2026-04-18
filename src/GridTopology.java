public enum GridTopology {
    TRIANGLE_LIST,
    TRIANGLE_STRIP;

    public GridTopology next() {
        return this == TRIANGLE_LIST ? TRIANGLE_STRIP : TRIANGLE_LIST;
    }

    public int getGlTopology() {
        return this == TRIANGLE_LIST ? org.lwjgl.opengl.GL11.GL_TRIANGLES : org.lwjgl.opengl.GL11.GL_TRIANGLE_STRIP;
    }

    public String getLabel() {
        return this == TRIANGLE_LIST ? "list" : "strip";
    }
}
