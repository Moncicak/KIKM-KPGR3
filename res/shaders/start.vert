#version 330
in vec3 inPosition;
uniform int mode;
uniform float time;
uniform float offset;
out float height;

const float PI = 3.1415926535;

void main() {
    float u = inPosition.x;
    float v = inPosition.y;
    vec3 pos = vec3(u, v, 0.0);

    // Každé těleso trochu zmenšíme přímo ve výpočtu
    if (mode == 0) {
        float r = sqrt(u*u + v*v) * 10.0;
        pos.z = (r == 0.0) ? 0.3 : (sin(r - time * 2.0) / r) * 0.3;
        pos.xy *= 0.5; // zmenšíme sombrero
    }
    else if (mode == 1) {
        pos = vec3(u * 0.5, v * 0.5, (u*u - v*v) * 0.25);
    }
    else if (mode == 2) {
        float phi = u * PI; float theta = v * (PI / 2.0);
        float r = 0.4 + 0.05 * sin(time * 3.0); // menší poloměr
        pos = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    }
    else if (mode == 3) {
        float phi = u * PI; float theta = v * (PI / 2.0);
        float r = 0.4 + 0.1 * sin(6.0 * phi + time) * cos(6.0 * theta);
        pos = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    }
    else if (mode == 4) {
        float phi = u * PI;
        pos = vec3(0.3 * cos(phi), 0.3 * sin(phi), v * 0.5); // užší válec
    }
    else if (mode == 5) {
        float phi = u * PI; float psi = v * PI;
        float R = 0.4; float rb = 0.15; // menší donut
        pos = vec3((R + rb * cos(psi)) * cos(phi), (R + rb * cos(psi)) * sin(phi), rb * sin(psi));
    }

    // APLIKACE OFFSETU (zmenšíme i offset, aby to nevyletělo z okna)
    pos.x += offset * 0.8;

    // GLOBÁLNÍ ROTACE (nakloníme to víc "k sobě", aby byla vidět hloubka)
    float angle = 0.8;
    vec3 finalPos;
    finalPos.x = pos.x;
    finalPos.y = pos.y * cos(angle) - pos.z * sin(angle);
    finalPos.z = pos.y * sin(angle) + pos.z * cos(angle);

    // Zmenšíme celkový výsledek, aby byl v bezpečné zóně
    gl_Position = vec4(finalPos * 0.9, 1.0);
    height = pos.z;
}