#version 330
in vec2 vTexCoord;

uniform sampler2D uPositionTex;
uniform sampler2D uNormalTex;
uniform sampler2D uAlbedoTex;
uniform sampler2D uAOTex;
uniform sampler2D uAOBlurTex;
uniform vec3 uLightPosView;
uniform vec3 uSpotDirView;
uniform vec3 uLightColor;
uniform float uAmbientStrength;
uniform float uSpotInnerCutoff;
uniform float uSpotOuterCutoff;
uniform int debugMode;
uniform int uAmbientEnabled;
uniform int uDiffuseEnabled;
uniform int uSpecularEnabled;
uniform int uSpotEnabled;

out vec4 outColor;

vec3 lighting(vec3 pos, vec3 normal, vec3 albedo, float ao) {
    vec3 lightVec = uLightPosView - pos;
    float distance = length(lightVec);
    vec3 lightDir = normalize(lightVec);
    vec3 viewDir = normalize(-pos);
    vec3 halfwayDir = normalize(lightDir + viewDir);
    vec3 spotAxis = normalize(uSpotDirView);
    vec3 lightToFrag = normalize(pos - uLightPosView);
    float spotCos = dot(spotAxis, lightToFrag);
    float spotFactor = (uSpotEnabled == 1) ? smoothstep(uSpotOuterCutoff, uSpotInnerCutoff, spotCos) : 1.0;

    float attenuation = 1.0 / (1.0 + 0.02 * distance + 0.005 * distance * distance);
    float diff = max(dot(normal, lightDir), 0.0);
    float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);

    vec3 ambient = (uAmbientEnabled == 1) ? ao * uAmbientStrength * uLightColor : vec3(0.0);
    vec3 diffuse = (uDiffuseEnabled == 1) ? diff * uLightColor * attenuation * spotFactor : vec3(0.0);
    vec3 specular = (uSpecularEnabled == 1) ? spec * uLightColor * attenuation * spotFactor : vec3(0.0);
    return (ambient + diffuse + specular) * albedo;
}

float getSpotFactor(vec3 pos) {
    if (uSpotEnabled == 0) {
        return 0.0;
    }
    vec3 spotAxis = normalize(uSpotDirView);
    vec3 lightToFrag = normalize(pos - uLightPosView);
    float spotCos = dot(spotAxis, lightToFrag);
    return smoothstep(uSpotOuterCutoff, uSpotInnerCutoff, spotCos);
}

void main() {
    vec3 pos = texture(uPositionTex, vTexCoord).xyz;
    vec3 normal = normalize(texture(uNormalTex, vTexCoord).xyz);
    vec3 albedo = texture(uAlbedoTex, vTexCoord).rgb;
    float aoRaw = texture(uAOTex, vTexCoord).r;
    float aoBlur = texture(uAOBlurTex, vTexCoord).r;

    if (debugMode == 1) {
        outColor = vec4(vec3(aoRaw), 1.0);
    } else if (debugMode == 2) {
        outColor = vec4(vec3(aoBlur), 1.0);
    } else if (debugMode == 3) {
        outColor = vec4(normal * 0.5 + 0.5, 1.0);
    } else if (debugMode == 4) {
        outColor = vec4(pos * 0.1 + 0.5, 1.0);
    } else if (debugMode == 5) {
        outColor = vec4(vec3(getSpotFactor(pos)), 1.0);
    } else {
        outColor = vec4(lighting(pos, normal, albedo, aoBlur), 1.0);
    }
}
