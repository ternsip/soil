#version 430 core

const int MAX_SAMPLERS = 16;
const int POWER4 = 16;
const int BLOCKS_X = 4000;
const int BLOCKS_Y = 3000;

const int QUAD_TYPE_EMPTY = 0;
const int QUAD_TYPE_BLOCKS = 1;
const int QUAD_TYPE_FONT = 2;
const int QUAD_FLAG_PINNED = 0x1;

struct TextureData {
    int layerStart;
    int layerEnd;
    int atlasNumber;
    float maxU;
    float maxV;
};

struct Quad {
    int type;
    int animation_start;
    float animation_period;
    int flags;
    int meta1;
    int meta2;
    float vertices[8];
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

float rand(float n) {
    return fract(sin(n) * 43758.5453123);
}

vec4 resolveQuadTexel(Quad quad, vec2 pos) {
    int type = quad.type;
    int animation_start = quad.animation_start;
    if (type == QUAD_TYPE_BLOCKS) {
        float realX = (texture_xy.x * 2 - 1) / cameraScale.x - cameraPos.x;
        float realY = (texture_xy.y * 2 - 1) / cameraScale.y - cameraPos.y;
        if (realX < 0 || realY < 0 || realX >= BLOCKS_X || realY >= BLOCKS_Y) {
            discard;
        }
        int blockX = int(realX);
        int blockY = int(realY);
        int blockIndex = blockY * BLOCKS_X + blockX;
        type = blocks[blockIndex];
        animation_start = int(rand(blockIndex) * quad.animation_period);
        pos.x = realX - blockX;
        pos.y = 1 - (realY - blockY);
    }
    if (type == QUAD_TYPE_EMPTY) {
        discard;
    }
    TextureData textureData = textures[type];
    float timeDelta = mod(abs(time - animation_start), quad.animation_period) / quad.animation_period;
    int count = textureData.layerEnd - textureData.layerStart + 1;
    int textureLayer = textureData.layerStart + clamp(int(timeDelta * count), 0, count - 1);
    vec2 maxUV = vec2(textureData.maxU, textureData.maxV);
    if (type == QUAD_TYPE_FONT) {
        pos.x = (pos.x + quad.meta1 % POWER4) / POWER4;
        pos.y = (pos.y + quad.meta1 / POWER4) / POWER4;
    }
    return texture(samplers[textureData.atlasNumber], vec3(pos * maxUV, textureLayer));
}

void main(void) {

    out_Color = resolveQuadTexel(quadData[roundFloat(quadIndex)], texture_xy);

}