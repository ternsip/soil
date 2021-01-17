#version 430 core

const int MESH_MAX_QUADS = (1 << 16) / 4;
const int[] TEXTURE_X = { 0, 1, 1, 0 };
const int[] TEXTURE_Y = { 0, 0, 1, 1 };
const int[] DELTA_X = { -1, 1, 1, -1 };
const int[] DELTA_Y = { -1, -1, 1, 1 };

const int QUAD_FLAG_PINNED = 0x1;
const int QUAD_FLAG_SHADOW = 0x2;
const int QUAD_FLAG_FONT256 = 0x4;

struct Quad {
    int type;
    int animation_start;
    float animation_period;
    int flags;
    int meta1;
    int meta2;
    float vertices[8];
};

struct Light {
    float x;
    float y;
    float radius;
    float power;
};

layout (std430, binding = 0) buffer quadBuffer {
    Quad quadData[];
};

layout (std430, binding = 2) buffer quadOrderBuffer {
    int quadOrder[];
};

layout (std430, binding = 4) buffer lightBuffer {
    Light lights[];
};

uniform int meshIndex;
uniform vec2 cameraPos;
uniform vec2 cameraScale;
uniform vec2 aspect;
uniform bool processingLight;

out float quadIndex;
out float lightIndex;
out float lightPower;
out vec2 texture_xy;

vec2 applyCamera(vec2 pos) {
    return (pos - cameraPos) * cameraScale * aspect;
}

void main(void) {

    if (processingLight) {
        int lightIndexi = MESH_MAX_QUADS * meshIndex + gl_VertexID / 4;
        Light light = lights[lightIndexi];
        int indexMod = gl_VertexID % 4;
        gl_Position.x = light.x + light.radius * DELTA_X[indexMod];
        gl_Position.y = light.y + light.radius * DELTA_Y[indexMod];
        gl_Position.xy = applyCamera(gl_Position.xy);
        texture_xy = vec2(TEXTURE_X[indexMod], TEXTURE_Y[indexMod]);
        lightIndex = lightIndexi;
        lightPower = light.power;
        return;
    }

    int quadIndexi = MESH_MAX_QUADS * meshIndex + gl_VertexID / 4;
    quadIndexi = quadOrder[quadIndexi];
    Quad quad = quadData[quadIndexi];
    quadIndex = quadIndexi;
    int indexMod = gl_VertexID % 4;
    texture_xy = vec2(TEXTURE_X[indexMod], TEXTURE_Y[indexMod]);
    float x = quad.vertices[indexMod * 2];
    float y = quad.vertices[indexMod * 2 + 1];
    if ((quad.flags & QUAD_FLAG_PINNED) == 0) {
        gl_Position.x = (x - cameraPos.x) * cameraScale.x * aspect.x;
        gl_Position.y = (y - cameraPos.y) * cameraScale.y * aspect.y;
    } else {
        gl_Position = vec4(x, y, 0, 1.0);
    }

}