#version 330

in vec4 inPosition;
in vec3 inNormal;
in vec2 inTexCoord;

uniform mat4 uMvp;
uniform mat4 uModel;

out vec3 vWorldPos;
out vec3 vNormal;
out vec2 vUv;

void main() {
    vec4 worldPos = uModel * inPosition;
    mat3 normalMatrix = transpose(inverse(mat3(uModel)));

    vWorldPos = worldPos.xyz;
    vNormal = normalize(normalMatrix * inNormal);
    vUv = inTexCoord;
    gl_Position = uMvp * inPosition;
}
