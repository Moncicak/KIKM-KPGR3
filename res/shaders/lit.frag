#version 330

in vec3 vWorldPos;
in vec3 vNormal;
in vec2 vUv;

uniform vec3 uCameraPos;
uniform vec3 uLightPos;
uniform vec3 uSpotDirection;
uniform vec3 uSpotAttenuation;
uniform float uSpotInnerCutoff;
uniform float uSpotOuterCutoff;
uniform mat4 uShadowMvp;
uniform vec3 uBaseColor;
uniform sampler2D uTexture;
uniform sampler2D uShadowMap;
uniform int uDebugView;
uniform int uUseAmbient;
uniform int uUseDiffuse;
uniform int uUseSpecular;
uniform int uUseTexture;

out vec4 fragColor;

vec3 encodeNormal(vec3 n) {
    return n * 0.5 + 0.5;
}

float sampleShadow(vec3 normal, vec3 lightDir) {
    vec4 shadowClip = uShadowMvp * vec4(vWorldPos, 1.0);
    vec3 shadowCoord = shadowClip.xyz / shadowClip.w;
    shadowCoord = shadowCoord * 0.5 + 0.5;

    if (shadowCoord.x < 0.0 || shadowCoord.x > 1.0 ||
        shadowCoord.y < 0.0 || shadowCoord.y > 1.0 ||
        shadowCoord.z < 0.0 || shadowCoord.z > 1.0) {
        return 1.0;
    }

    float bias = max(0.004 * (1.0 - dot(normal, lightDir)), 0.001);
    float currentDepth = shadowCoord.z - bias;
    vec2 texelSize = 1.0 / textureSize(uShadowMap, 0);
    float visibility = 0.0;

    for (int x = -1; x <= 1; ++x) {
        for (int y = -1; y <= 1; ++y) {
            float closestDepth = texture(uShadowMap, shadowCoord.xy + vec2(x, y) * texelSize).r;
            visibility += currentDepth <= closestDepth ? 1.0 : 0.35;
        }
    }

    return visibility / 9.0;
}

void main() {
    vec3 normal = normalize(vNormal);
    vec3 lightVector = uLightPos - vWorldPos;
    float lightDistance = length(lightVector);
    vec3 lightDir = normalize(lightVector);
    vec3 viewDir = normalize(uCameraPos - vWorldPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);

    vec3 texColor = float(uUseTexture) > 0.5 ? texture(uTexture, vUv).rgb : vec3(1.0);
    vec3 baseColor = texColor * uBaseColor;

    if (uDebugView == 1) {
        fragColor = vec4(clamp(0.5 + 0.25 * vWorldPos, vec3(0.0), vec3(1.0)), 1.0);
        return;
    }
    if (uDebugView == 2) {
        fragColor = vec4(encodeNormal(normal), 1.0);
        return;
    }
    if (uDebugView == 3) {
        fragColor = vec4(vUv, 0.0, 1.0);
        return;
    }
    if (uDebugView == 4) {
        float depth = clamp(length(uCameraPos - vWorldPos) / 25.0, 0.0, 1.0);
        fragColor = vec4(vec3(depth), 1.0);
        return;
    }
    if (uDebugView == 5) {
        fragColor = vec4(texColor, 1.0);
        return;
    }

    float ambient = 0.22;
    float diff = max(dot(normal, lightDir), 0.0);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 48.0);
    float spotCos = dot(normalize(-lightDir), normalize(uSpotDirection));
    float spot = smoothstep(uSpotOuterCutoff, uSpotInnerCutoff, spotCos);
    vec3 attenuation = 1.0 / (uSpotAttenuation.x + uSpotAttenuation.y * lightDistance + uSpotAttenuation.z * lightDistance * lightDistance);
    float shadow = sampleShadow(normal, lightDir);

    float ambientTerm = float(uUseAmbient) * ambient;
    float diffuseTerm = float(uUseDiffuse) * diff * spot * shadow;
    float specularTerm = float(uUseSpecular) * spec * spot * shadow;
    vec3 color = baseColor * ambientTerm + baseColor * diffuseTerm + vec3(specularTerm);
    color *= attenuation;
    fragColor = vec4(color, 1.0);
}
