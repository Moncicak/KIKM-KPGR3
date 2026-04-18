#version 330

in vec2 vUv;

uniform sampler2D uPositionTex;
uniform sampler2D uNormalTex;
uniform sampler2D uAlbedoTex;
uniform sampler2D uAOTexture;
uniform sampler2D uShadowMap;
uniform vec3 uCameraPos;
uniform vec3 uLightPos;
uniform vec3 uSpotDirection;
uniform vec3 uSpotAttenuation;
uniform float uSpotInnerCutoff;
uniform float uSpotOuterCutoff;
uniform mat4 uShadowMvp;
uniform vec3 uBaseColor;
uniform int uUseAmbient;
uniform int uUseDiffuse;
uniform int uUseSpecular;

out vec4 fragColor;

float sampleShadow(vec3 worldPos, vec3 normal, vec3 lightDir) {
    vec4 shadowClip = uShadowMvp * vec4(worldPos, 1.0);
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
    vec3 worldPos = texture(uPositionTex, vUv).xyz;
    vec3 normal = normalize(texture(uNormalTex, vUv).xyz * 2.0 - 1.0);
    vec3 albedo = texture(uAlbedoTex, vUv).rgb * uBaseColor;
    float ao = texture(uAOTexture, vUv).r;

    vec3 lightVector = uLightPos - worldPos;
    float lightDistance = length(lightVector);
    vec3 lightDir = normalize(lightVector);
    vec3 viewDir = normalize(uCameraPos - worldPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);

    float ambient = 0.22;
    float diff = max(dot(normal, lightDir), 0.0);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 48.0);
    float spotCos = dot(normalize(-lightDir), normalize(uSpotDirection));
    float spot = smoothstep(uSpotOuterCutoff, uSpotInnerCutoff, spotCos);
    vec3 attenuation = 1.0 / (uSpotAttenuation.x + uSpotAttenuation.y * lightDistance + uSpotAttenuation.z * lightDistance * lightDistance);
    float shadow = sampleShadow(worldPos, normal, lightDir);

    float ambientTerm = float(uUseAmbient) * ambient;
    float diffuseTerm = float(uUseDiffuse) * diff * spot * shadow;
    float specularTerm = float(uUseSpecular) * spec * spot * shadow;
    vec3 color = (albedo * ambientTerm + albedo * diffuseTerm + vec3(specularTerm)) * attenuation;
    color *= ao;
    fragColor = vec4(color, 1.0);
}
