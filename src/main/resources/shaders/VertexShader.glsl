#version 430 core

const int MAX_VERTICES = 1 << 16;
const int MAX_QUADS = (1 << 16) / 4;
const int[] VERTEX_BLOCKS_X = { -1, 1, 1, -1 };
const int[] VERTEX_BLOCKS_Y = { -1, -1, 1, 1 };
const int[] TEXTURE_X = { 0, 1, 1, 0 };
const int[] TEXTURE_Y = { 0, 0, 1, 1 };

const int QUAD_TYPE_EMPTY = 0;
const int QUAD_TYPE_BLOCKS = 1;
const int QUAD_TYPE_FONT = 2;
const int QUAD_FLAG_PINNED = 0x1;

struct Vertex {
    float x;
    float y;
};

struct Quad {
    int type;
    int animation_start;
    float animation_period;
    int flags;
    int meta1;
    int meta2;
};

layout (std430, binding = 2) buffer quadBuffer {
    Quad quadData[];
};

layout (std430, binding = 3) buffer vertexBuffer {
    Vertex vertices[];
};

uniform int layer;
uniform vec2 cameraPos;
uniform vec2 cameraScale;

out float quadIndex;
out vec2 texture_xy;

void main(void) {

    int quadIndexi = MAX_QUADS * layer + gl_VertexID / 4;
    Quad quad = quadData[quadIndexi];
    quadIndex = quadIndexi;
    int indexMod = gl_VertexID % 4;
    texture_xy = vec2(TEXTURE_X[indexMod], TEXTURE_Y[indexMod]);
    if (quad.type == QUAD_TYPE_BLOCKS) {
        gl_Position = vec4(VERTEX_BLOCKS_X[indexMod], VERTEX_BLOCKS_Y[indexMod], 0, 1.0);
        return;
    }
    Vertex v = vertices[layer * MAX_VERTICES + gl_VertexID];
    if ((quad.flags & QUAD_FLAG_PINNED) > 0) {
        gl_Position = vec4(v.x, v.y, 0, 1.0);
    } else {
        gl_Position = vec4((v.x + cameraPos.x) * cameraScale.x, (v.y + cameraPos.y) * cameraScale.y, 0, 1.0);
    }


}