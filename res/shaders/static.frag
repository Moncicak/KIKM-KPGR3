#version 330
in vec3 vViewPos;
in vec3 vViewNormal;
in vec3 vWorldPos;
in vec2 vTexCoord;

uniform vec3 uColor;
uniform sampler2D uSurfaceTex;

layout(location = 0) out vec4 gPosition;
layout(location = 1) out vec4 gNormal;
layout(location = 2) out vec4 gAlbedo;
layout(location = 3) out vec4 gWorldPosition;
layout(location = 4) out vec4 gUV;

void main() {
    vec3 texColor = texture(uSurfaceTex, vTexCoord).rgb;
    gPosition = vec4(vViewPos, 1.0);
    gNormal = vec4(normalize(vViewNormal), 1.0);
    gAlbedo = vec4(texColor * uColor, 1.0);
    gWorldPosition = vec4(vWorldPos, 1.0);
    gUV = vec4(vTexCoord, 0.0, 1.0);
}
