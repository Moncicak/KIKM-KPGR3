#version 330
in vec3 inPosition;

uniform int mode;
uniform float time;
uniform mat4 modelViewProjection;
uniform mat4 modelMatrix; // Potřebujeme pro výpočet světla ve world-space

out vec3 vPos;       // Pozice ve světě
out vec3 vNormal;    // Normála ve světě
out vec2 vTexCoord;
out float height;

const float PI = 3.1415926535;

vec3 getPosition(float u, float v) {
    vec3 p = vec3(u, v, 0.0);

    if (mode == 0) {
        float r = sqrt(u*u + v*v) * 10.0;
        p.z = (r == 0.0) ? 0.3 : (sin(r - time * 2.0) / r) * 0.3;
        p.xy *= 0.5;
    }
    else if (mode == 1) {
        p = vec3(u * 0.5, v * 0.5, (u*u - v*v) * 0.25);
    }
    else if (mode == 2) {
        float phi = u * PI; float theta = v * (PI / 2.0);
        float r = 0.4 + 0.05 * sin(time * 3.0);
        p = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    }
    else if (mode == 3) {
        float phi = u * PI; float theta = v * (PI / 2.0);
        float r = 0.4 + 0.1 * sin(6.0 * phi + time) * cos(6.0 * theta);
        p = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    }
    else if (mode == 4) {
        float phi = u * PI;
        p = vec3(0.3 * cos(phi), 0.3 * sin(phi), v * 0.5);
    }
    else if (mode == 5) {
        float phi = u * PI; float psi = v * PI;
        float R = 0.4; float rb = 0.15;
        p = vec3((R + rb * cos(psi)) * cos(phi), (R + rb * cos(psi)) * sin(phi), rb * sin(psi));
    }
    return p;
}

void main() {
    float u = inPosition.x;
    float v = inPosition.y;
    float delta = 0.01;

    vec3 p = getPosition(u, v);
    vec3 pU = getPosition(u + delta, v);
    vec3 pV = getPosition(u, v + delta);

    // Výpočet normály (ve world space pomocí modelové matice)
    vec3 tangentU = pU - p;
    vec3 tangentV = pV - p;
    vec3 normal = normalize(cross(tangentU, tangentV));

    // Transformujeme normálu do world-space (pouze rotace, proto mat3)
    vNormal = mat3(modelMatrix) * normal;

    // Transformujeme pozici do world-space pro fragment shader
    vPos = vec3(modelMatrix * vec4(p, 1.0));

    vTexCoord = inPosition.xy * 0.5 + 0.5;
    height = p.z;

    gl_Position = modelViewProjection * vec4(p, 1.0);
}