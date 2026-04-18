public enum DebugView {
    LIT("lit"),
    POSITION("position"),
    NORMAL("normal"),
    UV("uv"),
    DEPTH("depth"),
    TEXTURE("texture");

    private final String label;

    DebugView(String label) {
        this.label = label;
    }

    public DebugView next() {
        DebugView[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public int getIndex() {
        return ordinal();
    }

    public String getLabel() {
        return label;
    }
}
