#version 430 core

const int MAX_VERTICES = 1 << 16;

struct Vertex {
    float x;
    float y;
};

layout (std430, binding = 3) buffer vertexBuffer {
    Vertex vertices[];
};

uniform int layer;

void main(void) {

	Vertex v = vertices[layer * MAX_VERTICES + gl_VertexID];
    gl_Position = vec4(v.x, v.y, 0, 1.0);

}