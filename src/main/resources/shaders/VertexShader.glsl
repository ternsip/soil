#version 430 core

const int MAX_VERTICES = 1 << 16;
const int MAX_QUADS = (1 << 16) / 4;

struct Vertex {
    float x;
    float y;
};

layout (std430, binding = 3) buffer vertexBuffer {
    Vertex vertices[];
};

uniform int layer;

out float quad;
out vec2 texture_xy;

void main(void) {

	Vertex v = vertices[layer * MAX_VERTICES + gl_VertexID];
    gl_Position = vec4(v.x, v.y, 0, 1.0);
    texture_xy = vec2(0, 0);
    quad = MAX_QUADS * layer + gl_VertexID / 4;
    if (gl_VertexID % 4 == 1) {
        texture_xy = vec2(1, 0);
    }
    if (gl_VertexID % 4 == 2) {
        texture_xy = vec2(1, 1);
    }
    if (gl_VertexID % 4 == 3) {
        texture_xy = vec2(0, 1);
    }

}