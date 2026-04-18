#version 330

in vec3 vWorldPos;
in vec3 vNormal;
in vec2 vUv;

uniform vec3 uBaseColor;
uniform sampler2D uTexture;
uniform int uUseTexture;

out vec4 outPosition;
out vec4 outNormal;
out vec4 outAlbedo;

void main() {
    vec3 texColor = uUseTexture != 0 ? texture(uTexture, vUv).rgb : vec3(1.0);
    outPosition = vec4(vWorldPos, 1.0);
    outNormal = vec4(normalize(vNormal) * 0.5 + 0.5, 1.0);
    outAlbedo = vec4(texColor * uBaseColor, 1.0);
}
