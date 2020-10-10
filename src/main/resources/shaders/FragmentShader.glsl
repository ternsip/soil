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

in float quad; // TODO consider gl_PrimitiveID
in vec2 texture_xy;

out vec4 out_Color;

uniform sampler2DArray[MAX_SAMPLERS] samplers;

vec4 getTexture(int index, vec3 pos) {
    if (index == 0) return texture(samplers[0], pos);
    else if (index == 1) return texture(samplers[1], pos);
    else if (index == 2) return texture(samplers[2], pos);
    else if (index == 3) return texture(samplers[3], pos);
    else if (index == 4) return texture(samplers[4], pos);
    else if (index == 5) return texture(samplers[5], pos);
    else if (index == 6) return texture(samplers[6], pos);
    else if (index == 7) return texture(samplers[7], pos);
    else if (index == 8) return texture(samplers[8], pos);
    else if (index == 9) return texture(samplers[9], pos);
    else if (index == 10) return texture(samplers[10], pos);
    else if (index == 11) return texture(samplers[11], pos);
    else if (index == 12) return texture(samplers[12], pos);
    else if (index == 14) return texture(samplers[14], pos);
    return texture(samplers[15], pos);
}

int roundFloat(float value) {
    return int(round(value));
}

void main(void) {

    Quad q = quadData[roundFloat(quad)];
    TextureData t = textures[q.type];
    out_Color = getTexture(t.atlasNumber, vec3(texture_xy * vec2(t.maxU, t.maxV), t.layer));

}