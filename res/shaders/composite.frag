#version 330
in vec2 vTexCoord;

uniform sampler2D uPositionTex;
uniform sampler2D uNormalTex;
uniform sampler2D uAlbedoTex;
uniform sampler2D uWorldPosTex;
uniform sampler2D uUVTex;
uniform sampler2D uAOTex;
uniform sampler2D uAOBlurTex;
uniform sampler2D uShadowDepthTex;
uniform vec3 uLightPosView;
uniform vec3 uLightPosWorld;
uniform vec3 uSpotDirView;
uniform vec3 uLightColor;
uniform float uAmbientStrength;
uniform float uSpotInnerCutoff;
uniform float uSpotOuterCutoff;
uniform mat4 uLightViewProjection;
uniform vec2 uShadowTexelSize;
uniform float uShadowBias;
uniform int debugMode;
uniform int uAmbientEnabled;
uniform int uDiffuseEnabled;
uniform int uSpecularEnabled;
uniform int uSpotEnabled;

out vec4 outColor;

float getSpotFactor(vec3 pos) {
    if (uSpotEnabled == 0) {
        return 0.0;
    }
    vec3 spotAxis = normalize(uSpotDirView);
    vec3 lightToFrag = normalize(pos - uLightPosView);
    float spotCos = dot(spotAxis, lightToFrag);
    return smoothstep(uSpotOuterCutoff, uSpotInnerCutoff, spotCos);
}

float getShadowFactor(vec3 worldPos) {
    vec4 lightClip = uLightViewProjection * vec4(worldPos, 1.0);
    vec3 ndc = lightClip.xyz / max(lightClip.w, 1e-6);
    vec3 shadowCoord = ndc * 0.5 + 0.5;

    if (shadowCoord.x < 0.0 || shadowCoord.x > 1.0 ||
        shadowCoord.y < 0.0 || shadowCoord.y > 1.0 ||
        shadowCoord.z <= 0.0 || shadowCoord.z > 1.0) {
        return 1.0;
    }

    float visibility = 0.0;
    for (int y = -1; y <= 1; y++) {
        for (int x = -1; x <= 1; x++) {
            vec2 offset = vec2(float(x), float(y)) * uShadowTexelSize;
            float nearestDepth = texture(uShadowDepthTex, shadowCoord.xy + offset).r;
            visibility += (shadowCoord.z - uShadowBias <= nearestDepth) ? 1.0 : 0.0;
        }
    }

    return visibility / 9.0;
}

vec3 lighting(vec3 pos, vec3 normal, vec3 albedo, float ao, float shadowFactor) {
    vec3 lightVec = uLightPosView - pos;
    float distance = length(lightVec);
    vec3 lightDir = normalize(lightVec);
    vec3 viewDir = normalize(-pos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    float spotFactor = (uSpotEnabled == 1) ? getSpotFactor(pos) : 1.0;

    float attenuation = 1.0 / (1.0 + 0.02 * distance + 0.005 * distance * distance);
    float diff = max(dot(normal, lightDir), 0.0);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);

    vec3 ambient = (uAmbientEnabled == 1) ? ao * uAmbientStrength * uLightColor : vec3(0.0);
    vec3 diffuse = (uDiffuseEnabled == 1) ? diff * uLightColor * attenuation * spotFactor * shadowFactor : vec3(0.0);
    vec3 specular = (uSpecularEnabled == 1) ? spec * uLightColor * attenuation * spotFactor * shadowFactor : vec3(0.0);
    return (ambient + diffuse + specular) * albedo;
}

void main() {
    vec3 posView = texture(uPositionTex, vTexCoord).xyz;
    vec3 normal = normalize(texture(uNormalTex, vTexCoord).xyz);
    vec3 albedo = texture(uAlbedoTex, vTexCoord).rgb;
    vec3 posWorld = texture(uWorldPosTex, vTexCoord).xyz;
    vec2 uv = texture(uUVTex, vTexCoord).xy;
    float aoRaw = texture(uAOTex, vTexCoord).r;
    float aoBlur = texture(uAOBlurTex, vTexCoord).r;
    float spotFactor = getSpotFactor(posView);
    float shadowFactor = getShadowFactor(posWorld);
    float depthView = clamp((-posView.z) / 20.0, 0.0, 1.0);
    float lightDist = length(uLightPosWorld - posWorld);
    float lightDistNorm = clamp(lightDist / 10.0, 0.0, 1.0);

    if (debugMode == 1) {
        outColor = vec4(vec3(aoRaw), 1.0);
    } else if (debugMode == 2) {
        outColor = vec4(vec3(aoBlur), 1.0);
    } else if (debugMode == 3) {
        outColor = vec4(normal * 0.5 + 0.5, 1.0);
    } else if (debugMode == 4) {
        outColor = vec4(posView * 0.1 + 0.5, 1.0);
    } else if (debugMode == 5) {
        outColor = vec4(vec3(spotFactor), 1.0);
    } else if (debugMode == 6) {
        outColor = vec4(albedo, 1.0);
    } else if (debugMode == 7) {
        outColor = vec4(uv, 0.0, 1.0);
    } else if (debugMode == 8) {
        outColor = vec4(vec3(depthView), 1.0);
    } else if (debugMode == 9) {
        outColor = vec4(vec3(lightDistNorm), 1.0);
    } else if (debugMode == 10) {
        outColor = vec4(vec3(shadowFactor), 1.0);
    } else {
        outColor = vec4(lighting(posView, normal, albedo, aoBlur, shadowFactor), 1.0);
    }
}
