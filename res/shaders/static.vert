#version 330
in vec4 inPosition;
in vec3 inNormal;
in vec2 inTexCoord;

uniform mat4 modelViewProjection;
uniform mat4 modelViewMatrix;
uniform mat4 modelMatrix;

out vec3 vViewPos;
out vec3 vViewNormal;
out vec3 vWorldPos;
out vec2 vTexCoord;

void main() {
    vViewPos = vec3(modelViewMatrix * inPosition);
    vViewNormal = normalize(mat3(modelViewMatrix) * inNormal);
    vWorldPos = vec3(modelMatrix * inPosition);
    vTexCoord = inTexCoord;
    gl_Position = modelViewProjection * inPosition;
}
