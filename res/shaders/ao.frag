#version 330

in vec2 vUv;

uniform sampler2D uPositionTex;
uniform sampler2D uNormalTex;
uniform mat4 uView;
uniform float uRadius;

out vec4 fragColor;

void main() {
    vec3 centerWorld = texture(uPositionTex, vUv).xyz;
    vec3 centerNormal = normalize(texture(uNormalTex, vUv).xyz * 2.0 - 1.0);
    vec3 centerView = (uView * vec4(centerWorld, 1.0)).xyz;
    vec2 texelSize = 1.0 / vec2(textureSize(uPositionTex, 0));

    float occlusion = 0.0;
    float samples = 0.0;
    for (int x = -2; x <= 2; ++x) {
        for (int y = -2; y <= 2; ++y) {
            if (x == 0 && y == 0) {
                continue;
            }
            vec2 offset = vec2(x, y) * texelSize * uRadius;
            vec3 sampleWorld = texture(uPositionTex, vUv + offset).xyz;
            vec3 sampleNormal = normalize(texture(uNormalTex, vUv + offset).xyz * 2.0 - 1.0);
            vec3 sampleView = (uView * vec4(sampleWorld, 1.0)).xyz;
            float dz = sampleView.z - centerView.z;
            float normalWeight = max(dot(centerNormal, sampleNormal), 0.0);
            float rangeWeight = 1.0 / (1.0 + abs(dz) * 40.0);
            occlusion += step(0.004, dz) * normalWeight * rangeWeight;
            samples += 1.0;
        }
    }

    float ao = 1.0 - occlusion / max(samples, 1.0);
    ao = clamp(pow(ao, 1.35), 0.0, 1.0);
    fragColor = vec4(vec3(ao), 1.0);
}
