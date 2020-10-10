#version 430 core

const int MAX_SAMPLERS = 16;

struct TextureData {
    int layer;
    int atlasNumber;
    float maxU;
    float maxV;
};

struct Quad {
    int type;
};

layout (std430, binding = 0) buffer blocksBuffer {
    int blocks[];
};

layout (std430, binding = 1) buffer textureBuffer {
    TextureData textures[];
};

layout (std430, binding = 2) buffer quadBuffer {
    Quad quadData[];
};

in float quadIndex;
in vec2 texture_xy;

out vec4 out_Color;

uniform sampler2DArray[MAX_SAMPLERS] samplers;

int roundFloat(float value) {
    return int(round(value));
}

void main(void) {

    Quad quad = quadData[roundFloat(quadIndex)];
    TextureData textureData = textures[quad.type];
    out_Color = texture(samplers[textureData.atlasNumber], vec3(texture_xy * vec2(textureData.maxU, textureData.maxV), textureData.layer));

}