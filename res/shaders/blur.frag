#version 330

in vec2 vUv;

uniform sampler2D uAOTex;
uniform vec2 uTexelSize;

out vec4 fragColor;

void main() {
    float sum = 0.0;
    float weight = 0.0;
    for (int x = -2; x <= 2; ++x) {
        for (int y = -2; y <= 2; ++y) {
            float w = 1.0;
            sum += texture(uAOTex, vUv + vec2(x, y) * uTexelSize).r * w;
            weight += w;
        }
    }
    fragColor = vec4(vec3(sum / weight), 1.0);
}
