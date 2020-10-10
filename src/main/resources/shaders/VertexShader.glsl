#version 430 core

layout (std430, binding = 3) buffer vertexBuffer {
	float positions[];
};

//in int index;

uniform int layer;

void main(void){
	gl_Position = vec4(layer, gl_VertexID, gl_VertexID, 1.0);
}