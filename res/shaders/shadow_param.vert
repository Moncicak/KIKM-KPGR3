#version 330
in vec3 inPosition;

uniform int mode;
uniform float time;
uniform mat4 lightMVP;

const float PI = 3.1415926535;

vec3 getPosition(float u, float v) {
    vec3 p = vec3(u, v, 0.0);

    if (mode == 0) {
        float r = sqrt(u * u + v * v) * 10.0;
        float rs = max(r, 0.15);
        p.z = (sin(r - time * 2.0) / rs) * 0.22;
        p.xy *= 0.5;
    } else if (mode == 1) {
        p = vec3(u * 0.5, v * 0.5, (u * u - v * v) * 0.25);
    } else if (mode == 2) {
        float phi = u * PI;
        float theta = v * (PI / 2.0);
        float r = 0.4 + 0.05 * sin(time * 3.0);
        p = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    } else if (mode == 3) {
        float phi = u * PI;
        float theta = v * (PI / 2.0);
        float r = 0.4 + 0.1 * sin(6.0 * phi + time) * cos(6.0 * theta);
        p = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    } else if (mode == 4) {
        float phi = u * PI;
        p = vec3(0.3 * cos(phi), 0.3 * sin(phi), v * 0.5);
    } else if (mode == 5) {
        float phi = u * PI;
        float psi = v * PI;
        float R = 0.4;
        float rb = 0.15;
        p = vec3((R + rb * cos(psi)) * cos(phi), (R + rb * cos(psi)) * sin(phi), rb * sin(psi));
    }
    p.z = clamp(p.z, -1.0, 1.0);
    return p;
}

void main() {
    vec3 p = getPosition(inPosition.x, inPosition.y);
    gl_Position = lightMVP * vec4(p, 1.0);
}
