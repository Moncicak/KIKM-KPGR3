#version 330
in vec3 vViewPos;
in vec3 vViewNormal;

uniform vec3 uColor;

layout(location = 0) out vec4 gPosition;
layout(location = 1) out vec4 gNormal;
layout(location = 2) out vec4 gAlbedo;

void main() {
    gPosition = vec4(vViewPos, 1.0);
    gNormal = vec4(normalize(vViewNormal), 1.0);
    gAlbedo = vec4(uColor, 1.0);
}
