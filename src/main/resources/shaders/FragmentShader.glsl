#version 430 core

layout (std430, binding = 0) buffer blocksBuffer {
    int blocks[];
};

layout (std430, binding = 1) buffer textureBuffer {
    float textures[];
};

layout (std430, binding = 2) buffer quadBuffer {
    float quadData[];
};

out vec4 out_Color;

void main(void) {

    out_Color = vec4(0.5, 0.6, 0.2, 1.0);

}