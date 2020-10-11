#version 430 core

const int MAX_SAMPLERS = 16;
const int BLOCKS_X = 4000;
const int BLOCKS_Y = 3000;

const int QUAD_TYPE_EMPTY = 0;
const int QUAD_TYPE_BLOCKS = 1;

struct TextureData {
    int layerStart;
    int layerEnd;
    int atlasNumber;
    float maxU;
    float maxV;
};

struct Quad {
    int type;
    float period;
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

uniform vec2 cameraPos;
uniform vec2 cameraScale;
uniform int time;
uniform sampler2DArray[MAX_SAMPLERS] samplers;

int roundFloat(float value) {
    return int(round(value));
}

vec4 resolveQuadTexel(int type, float period, vec2 pos) {
    if (type == QUAD_TYPE_EMPTY) {
        discard;
    }
    TextureData textureData = textures[type];
    float timeDelta = mod(time, period) / period;
    int count = textureData.layerEnd - textureData.layerStart + 1;
    int textureLayer = textureData.layerStart + clamp(int(timeDelta * count), 0, count - 1);
    return texture(samplers[textureData.atlasNumber], vec3(pos * vec2(textureData.maxU, textureData.maxV), textureLayer));
}

void main(void) {

    Quad quad = quadData[roundFloat(quadIndex)];
    if (quad.type == QUAD_TYPE_BLOCKS) {
        float realX = (texture_xy.x * 2 - 1) / cameraScale.x - cameraPos.x;
        float realY = (texture_xy.y * 2 - 1) / cameraScale.y - cameraPos.y;
        if (realX < 0 || realY < 0 || realX >= BLOCKS_X || realY >= BLOCKS_Y) {
            discard;
        }
        int blockX = int(realX);
        int blockY = int(realY);
        out_Color = resolveQuadTexel(blocks[blockY * BLOCKS_X + blockX], quad.period, vec2(realX - blockX, 1 - (realY - blockY)));
        return;
    }
    out_Color = resolveQuadTexel(quad.type, quad.period, texture_xy);

}