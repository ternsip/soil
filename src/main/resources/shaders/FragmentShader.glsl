#version 430 core

const float INF = 1e6;
const float EPS = 1e-6;
const int MAX_SAMPLERS = 16;
const int L_RADIUS = 2;
const int MAX_LIGHT = 8;
const int POWER4 = 16;
const int BLOCKS_X = 4000;
const int BLOCKS_Y = 3000;
const int TOTAL_BLOCKS = BLOCKS_X * BLOCKS_Y;

const int QUAD_TYPE_EMPTY = 0;
const int QUAD_TYPE_BLOCKS = 1;
const int QUAD_TYPE_FONT = 2;
const int QUAD_TYPE_WATER = 3;
const int QUAD_TYPE_LAVA = 4;
const int QUAD_TYPE_SHADOW = 5;

const int QUAD_FLAG_PINNED = 0x1;

struct Block {
    int type;
    float sky;
    float emit;
};

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
    Block blocks[];
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

float mod289(float x){ return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 mod289(vec4 x){ return x - floor(x * (1.0 / 289.0)) * 289.0; }
vec4 perm(vec4 x){ return mod289(((x * 34.0) + 1.0) * x); }

// https://gist.github.com/patriciogonzalezvivo/670c22f3966e662d2f83
float noise(vec3 p){
    vec3 a = floor(p);
    vec3 d = p - a;
    d = d * d * (3.0 - 2.0 * d);
    vec4 b = a.xxyy + vec4(0.0, 1.0, 0.0, 1.0);
    vec4 k1 = perm(b.xyxy);
    vec4 k2 = perm(k1.xyxy + b.zzww);
    vec4 c = k2 + a.zzzz;
    vec4 k3 = perm(c);
    vec4 k4 = perm(c + 1.0);
    vec4 o1 = fract(k3 * (1.0 / 41.0));
    vec4 o2 = fract(k4 * (1.0 / 41.0));
    vec4 o3 = o2 * d.z + o1 * (1.0 - d.z);
    vec2 o4 = o3.yw * d.x + o3.xz * (1.0 - d.x);
    return o4.y * d.y + o4.x * (1.0 - d.y);
}

float loopTime(int length) {
    return (abs(time % (2 * length) - length)) / float(length);
}

vec4 resolveQuadTexel(Quad quad, vec2 pos) {
    int type = quad.type;
    int animation_start = quad.animation_start;
    float realX = (texture_xy.x * 2 - 1) / cameraScale.x - cameraPos.x;
    float realY = (texture_xy.y * 2 - 1) / cameraScale.y - cameraPos.y;
    float shadowMask = 0;
    if (type == QUAD_TYPE_BLOCKS || type == QUAD_TYPE_SHADOW) {
        if (realX < 0 || realY < 0 || realX >= BLOCKS_X || realY >= BLOCKS_Y) {
            discard;
        }
        int blockX = int(realX);
        int blockY = int(realY);
        int blockIndex = blockY * BLOCKS_X + blockX;
        Block block = blocks[blockIndex];
        vec2 blockFragment = vec2(realX - blockX, realY - blockY);
        if (type == QUAD_TYPE_SHADOW) {
            float light = 0;
            for (int dx = -L_RADIUS, nx = blockX - L_RADIUS; dx <= L_RADIUS; ++dx, ++nx) {
                for (int dy = -L_RADIUS, ny = blockY - L_RADIUS; dy <= L_RADIUS; ++dy, ++ny) {
                    if (nx < 0 || ny < 0 || nx >= BLOCKS_X || ny >= BLOCKS_Y) continue;
                    Block nextBlock = blocks[ny * BLOCKS_X + nx];
                    vec2 anchor = vec2(dx, dy);
                    if (dx == 0) anchor.x = blockFragment.x;
                    if (dy == 0) anchor.y = blockFragment.y;
                    if (dx < 0) anchor.x += 1;
                    if (dy < 0) anchor.y += 1;
                    float dist = max(0, (L_RADIUS - distance(blockFragment, anchor)) / L_RADIUS);
                    light = max(light, max(nextBlock.emit, nextBlock.sky) * dist);
                }
            }
            return vec4(0, 0, 0, 1 - light);
        }
        type = block.type;
        animation_start = int(rand(blockIndex) * quad.animation_period);
        pos.x = blockFragment.x;
        pos.y = 1 - blockFragment.y;
    }
    if (type == QUAD_TYPE_EMPTY) {
        discard;
    }
    if (type == QUAD_TYPE_LAVA) {
        vec3 orange = vec3(1., .45, 0.);
        vec3 yellow = vec3(1., 1., 0.);
        // TODO make lava better https://www.shadertoy.com/view/llsBR4 https://www.shadertoy.com/view/lslXRS https://thebookofshaders.com/edit.php#11/lava-lamp.frag http://www.science-and-fiction.org/rendering/noise.html
        float noiseValue = noise(vec3(realX, realY, loopTime(10000) * 10));
        return vec4(mix(yellow, orange, vec3(smoothstep(0., 1., noiseValue))), 0.9f);
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