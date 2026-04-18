#version 330
in float height;
out vec4 outColor;
void main() {
    // Jednoduchý barevný přechod podle "výšky"
    outColor = vec4(height + 0.5, 0.5, 1.0 - height, 1.0);
}