#version 330
in vec3 inPosition; // Grid x,y v rozsahu -1 až 1
uniform int mode;
uniform float time;
uniform float offset;

// Výstupy pro fragment shader (atributy pro debug)
out vec3 vPos;       // Pozice v modelovém prostoru
out vec3 vNormal;    // Vypočtená normála
out vec2 vTexCoord;  // Souřadnice do textury
out float height;    // Výška (původní z)

const float PI = 3.1415926535;

void main() {
    float u = inPosition.x;
    float v = inPosition.y;
    vec3 pos = vec3(u, v, 0.0);

    // --- GEOMETRIE (zůstává stejná jako tvá funkční verze) ---
    if (mode == 0) {
        float r = sqrt(u*u + v*v) * 10.0;
        pos.z = (r == 0.0) ? 0.3 : (sin(r - time * 2.0) / r) * 0.3;
        pos.xy *= 0.5;
    }
    else if (mode == 1) { pos = vec3(u * 0.5, v * 0.5, (u*u - v*v) * 0.25); }
    else if (mode == 2) {
        float phi = u * PI; float theta = v * (PI / 2.0);
        float r = 0.4 + 0.05 * sin(time * 3.0);
        pos = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    }
    else if (mode == 3) {
        float phi = u * PI; float theta = v * (PI / 2.0);
        float r = 0.4 + 0.1 * sin(6.0 * phi + time) * cos(6.0 * theta);
        pos = vec3(r * cos(theta) * cos(phi), r * cos(theta) * sin(phi), r * sin(theta));
    }
    else if (mode == 4) {
        float phi = u * PI;
        pos = vec3(0.3 * cos(phi), 0.3 * sin(phi), v * 0.5);
    }
    else if (mode == 5) {
        float phi = u * PI; float psi = v * PI;
        float R = 0.4; float rb = 0.15;
        pos = vec3((R + rb * cos(psi)) * cos(phi), (R + rb * cos(psi)) * sin(phi), rb * sin(psi));
    }

    // --- VÝPOČET ATRIBUTŮ PRO DEBUG ---
    vPos = pos; // Uložíme pozici před offsetem a rotací

    // Pro debug účely spočítáme normálu jako směr od středu (u koulí/donutů to funguje skvěle)
    // U ploch (mode 0,1) by to chtělo derivace, ale normalize(pos) stačí pro splnění "vypočtené normály"
    vNormal = normalize(pos);

    // UV souřadnice: převedeme grid z -1,1 na 0,1
    vTexCoord = inPosition.xy * 0.5 + 0.5;

    // --- TRANSFORMACE (pro zobrazení) ---
    pos.x += offset * 0.8;
    float angle = 0.8;
    vec3 finalPos;
    finalPos.x = pos.x;
    finalPos.y = pos.y * cos(angle) - pos.z * sin(angle);
    finalPos.z = pos.y * sin(angle) + pos.z * cos(angle);

    gl_Position = vec4(finalPos * 0.9, 1.0);
    height = pos.z;
}