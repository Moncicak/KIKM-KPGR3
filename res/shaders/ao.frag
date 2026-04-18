#version 330
in vec2 vTexCoord;

uniform sampler2D uPositionTex;
uniform sampler2D uNormalTex;
uniform vec2 uResolution;
uniform float uSampleRadius;
uniform float uPixelRadius;
uniform float uBias;

out vec4 outColor;

float hash(vec2 p) {
    return fract(sin(dot(p, vec2(127.1, 311.7))) * 43758.5453123);
}

void main() {
    vec3 fragPos = texture(uPositionTex, vTexCoord).xyz;
    vec3 normal = normalize(texture(uNormalTex, vTexCoord).xyz);

    float angle = hash(vTexCoord * uResolution) * 6.2831853;
    mat2 rot = mat2(cos(angle), -sin(angle), sin(angle), cos(angle));

    const int sampleCount = 8;
    float occlusion = 0.0;

    for (int i = 0; i < sampleCount; i++) {
        float a = 6.2831853 * (float(i) / float(sampleCount));
        vec2 dir = rot * vec2(cos(a), sin(a));
        vec2 sampleUV = vTexCoord + dir * (uPixelRadius / uResolution);
        vec3 samplePos = texture(uPositionTex, sampleUV).xyz;
        vec3 toSample = samplePos - fragPos;
        float dist = length(toSample);
        float range = 1.0 - smoothstep(0.0, uSampleRadius, dist);
        float nd = max(dot(normal, normalize(toSample + vec3(1e-5))), 0.0);
        float depthTest = step(uBias, fragPos.z - samplePos.z);
        occlusion += depthTest * nd * range;
    }

    float ao = 1.0 - occlusion / float(sampleCount);
    outColor = vec4(vec3(clamp(ao, 0.0, 1.0)), 1.0);
}
