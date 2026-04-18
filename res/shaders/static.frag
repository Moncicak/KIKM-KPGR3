#version 330
in vec3 vPos;
in vec3 vNormal;
uniform int debugMode;
uniform vec3 uColor; // Ta žlutá barva z Renderer.java

out vec4 outColor;

void main() {
    if (debugMode == 0) {
        outColor = vec4(uColor, 1.0);
    } else if (debugMode == 1) {
        outColor = vec4(vPos * 0.5 + 0.5, 1.0);
    } else if (debugMode == 2) {
        outColor = vec4(vNormal * 0.5 + 0.5, 1.0);
    } else {
        outColor = vec4(1.0); // Bílá pro hloubku/ostatní
    }
}