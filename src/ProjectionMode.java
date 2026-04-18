public enum ProjectionMode {
    PERSPECTIVE,
    ORTHOGRAPHIC;

    public ProjectionMode toggle() {
        return this == PERSPECTIVE ? ORTHOGRAPHIC : PERSPECTIVE;
    }
}
