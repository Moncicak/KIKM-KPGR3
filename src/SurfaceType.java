public enum SurfaceType {
    CARTESIAN_WAVE("cartesian wave"),
    CARTESIAN_RIPPLE("cartesian ripple"),
    SPHERICAL_FLOWER("spherical flower"),
    SPHERICAL_BUMP("spherical bump"),
    CYLINDRICAL_WAVES("cylindrical waves"),
    CYLINDRICAL_SPIRAL("cylindrical spiral");

    private final String label;

    SurfaceType(String label) {
        this.label = label;
    }

    public SurfaceType next() {
        SurfaceType[] values = values();
        return values[(ordinal() + 1) % values.length];
    }

    public int getIndex() {
        return ordinal();
    }

    public String getLabel() {
        return label;
    }
}
