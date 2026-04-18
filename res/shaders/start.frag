#version 330
in vec3 vPos;
in vec3 vNormal;
in vec2 vTexCoord;

uniform int debugMode;
uniform vec3 lightPos;
uniform vec3 viewPos;
uniform vec3 lightDirUniform;

out vec4 outColor;

void main() {
    // 1. VEKTORY
    vec3 normal = normalize(vNormal);
    vec3 lightVec = lightPos - vPos;
    float distance = length(lightVec);
    vec3 lightDir = normalize(lightVec); // Směr K světlu
    vec3 viewDir = normalize(viewPos - vPos);
    vec3 halfwayDir = normalize(lightDir + viewDir);

    // 2. ÚTLUM (Attenuation) - klasický bodový zdroj
    float attenuation = 1.0 / (1.0 + 0.02 * distance + 0.005 * (distance * distance));

    // 3. BARVY
    vec3 lightColor = vec3(2.0, 2.0, 1.8); // Silné světlo
    vec3 objectColor = vec3(0.0, 0.4, 0.8);

    // Ambient svítí pořád trošku (0.1)
    vec3 ambient = 0.1 * lightColor;

    // Diffuse a Specular pro bodový zdroj
    float diff = max(dot(normal, lightDir), 0.0);
    vec3 diffuse = diff * lightColor * attenuation;

    float spec = pow(max(dot(normal, halfwayDir), 0.0), 32.0);
    vec3 specular = spec * lightColor * attenuation;

    // 4. PŘEPÍNÁNÍ REŽIMŮ
    vec3 finalLight;
    if (debugMode == 1) finalLight = ambient;
    else if (debugMode == 2) finalLight = diffuse;
    else if (debugMode == 3) finalLight = specular;
    else finalLight = ambient + diffuse + specular;

    outColor = vec4(finalLight * objectColor, 1.0);
}
