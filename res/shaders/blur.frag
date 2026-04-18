#version 330
in vec2 vTexCoord;

uniform sampler2D uAOTex;
uniform vec2 uTexelSize;

out vec4 outColor;

void main() {
    float sum = 0.0;
    int count = 0;

    for (int y = -2; y <= 2; y++) {
        for (int x = -2; x <= 2; x++) {
            vec2 offset = vec2(float(x), float(y)) * uTexelSize;
            sum += texture(uAOTex, vTexCoord + offset).r;
            count++;
        }
    }

    outColor = vec4(vec3(sum / float(count)), 1.0);
}
