#version 430 core

const int MAX_LAYERS = 16;
const int LAYER_MAX_QUADS = (1 << 16) / 4;
const int MAX_QUADS = LAYER_MAX_QUADS * MAX_LAYERS;
const int[] TEXTURE_X = { 0, 1, 1, 0 };
const int[] TEXTURE_Y = { 0, 0, 1, 1 };

const int QUAD_FLAG_PINNED = 0x1;

struct Quad {
    int type;
    int animation_start;
    float animation_period;
    int flags;
    int meta1;
    int meta2;
    float vertices[8];
};

layout (std430, binding = 0) buffer quadBuffer {
    Quad quadData[];
};

uniform int layer;
uniform vec2 cameraPos;
uniform vec2 cameraScale;
uniform vec2 aspect;

out float quadIndex;
out vec2 texture_xy;

void main(void) {

    int quadIndexi = LAYER_MAX_QUADS * layer + gl_VertexID / 4;
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