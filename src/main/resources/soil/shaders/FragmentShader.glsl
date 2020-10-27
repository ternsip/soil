#version 430 core

#define PI 3.1415926538
#define PI_2 6.28318530717
#define SQRT2 1.4142135623730

const float LIGHT_ACCURACY = 5;
const float INF = 1e6;
const float EPS = 1e-6;
const int MAX_SAMPLERS = 16;
const int MAX_LIGHT = 8;
const int POWER4 = 16;
const int BLOCKS_X = 4000;
const int BLOCKS_Y = 3000;
const int TOTAL_BLOCKS = BLOCKS_X * BLOCKS_Y;
const int[] ANCHOR_DELTA_X = { -1, -1, -1, 0, 0, 1, 1, 1 };
const int[] ANCHOR_DELTA_Y = { -1, 0, 1, -1, 1, -1, 0, 1 };
const int[] ANCHOR_DELTA_INDEX = { -BLOCKS_X-1, -1, BLOCKS_X-1, -BLOCKS_X, BLOCKS_X, -BLOCKS_X+1, 1, BLOCKS_X+1 };

const int TEXTURE_STYLE_EMPTY = 0;
const int TEXTURE_STYLE_NORMAL = 1;
const int TEXTURE_STYLE_SHADOW = 2;
const int TEXTURE_STYLE_BLOCKS = 3;
const int TEXTURE_STYLE_FONT256 = 4;
const int TEXTURE_STYLE_WATER = 5;
const int TEXTURE_STYLE_LAVA = 6;
const int TEXTURE_STYLE_4_SIMPLE_VARIATION = 7;
const int TEXTURE_STYLE_4_ADJACENT8_VARIATION = 8;

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
    int textureStyle;
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
uniform vec2 aspect;
uniform int time;
uniform bool debugging;
uniform sampler2DArray[MAX_SAMPLERS] samplers;

int roundFloat(float value) {
    return int(round(value));
}

float rand(float n) {
    return fract(sin(n) * 43758.5453123);
}

int randInt(int seed) {
    // Xorshift*32
    // Based on George Marsaglia's work: http://www.jstatsoft.org/v08/i14/paper
    seed ^= seed << 13;
    seed ^= seed >> 17;
    seed ^= seed << 5;
    return seed;
}

float noise(float p){
    float fl = floor(p);
    float fc = fract(p);
    return mix(rand(fl), rand(fl + 1.0), fc);
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

float loopValue(int value, int length) {
    return (abs(value % (2 * length) - length)) / float(length);
}

vec4 mix4(vec4 src, vec4 dst) {
    return src * (1 - dst.a) + vec4(dst.rgb * dst.a, dst.a);
}

vec2 translateSquarePos(vec2 pos, int scale, int dx, int dy) {
    return vec2((pos.x + dx) / scale, (pos.y + dy) / scale);
}

vec2 translateSquareIndex(vec2 pos, int scale, int index) {
    return vec2((pos.x + index % scale) / scale, (pos.y + index / scale) / scale);
}

vec4 resolveTexture(TextureData textureData, int animation_start, float animation_period, vec2 pos) {
    float timeDelta = mod(abs(time - animation_start), animation_period) / animation_period;
    int count = textureData.layerEnd - textureData.layerStart + 1;
    int textureLayer = textureData.layerStart + clamp(int(timeDelta * count), 0, count - 1);
    vec2 maxUV = vec2(textureData.maxU, textureData.maxV);
    return texture(samplers[textureData.atlasNumber], vec3(pos * maxUV, textureLayer));
}

vec4 resolveQuadTexel() {
    Quad quad = quadData[roundFloat(quadIndex)];
    vec2 pos = texture_xy;
    TextureData textureData = textures[quad.type];
    int animation_start = quad.animation_start;
    float realX = (texture_xy.x * 2 - 1) / (cameraScale.x * aspect.x) - cameraPos.x;
    float realY = (texture_xy.y * 2 - 1) / (cameraScale.y * aspect.y) - cameraPos.y;
    vec4 overlapTexel = vec4(0);
    if (textureData.textureStyle == TEXTURE_STYLE_BLOCKS || textureData.textureStyle == TEXTURE_STYLE_SHADOW) {
        if (realX < 0 || realY < 0 || realX >= BLOCKS_X || realY >= BLOCKS_Y || (debugging && textureData.textureStyle == TEXTURE_STYLE_SHADOW)) {
            discard;
        }
        int blockX = int(realX);
        int blockY = int(realY);
        int blockIndex = blockY * BLOCKS_X + blockX;
        Block block = blocks[blockIndex];
        vec2 blockFragment = vec2(realX - blockX, realY - blockY);
        if (textureData.textureStyle == TEXTURE_STYLE_SHADOW) {
            float light = max(block.emit, block.sky);
            float receiveMaxLight[8];
            float receiveLight[8];
            for (int k = 0; k < 8; ++k) {
                int nextBlockIndex = blockIndex + ANCHOR_DELTA_INDEX[k];
                if (blockX + ANCHOR_DELTA_X[k] < 0 || blockY + ANCHOR_DELTA_Y[k] < 0 || blockX + ANCHOR_DELTA_X[k] >= BLOCKS_X || blockY + ANCHOR_DELTA_Y[k] >= BLOCKS_Y) continue;
                Block nextBlock = blocks[nextBlockIndex];
                vec2 anchor = vec2(ANCHOR_DELTA_X[k] + 0.5, ANCHOR_DELTA_Y[k] + 0.5);
                float dist = (SQRT2 - min(SQRT2, distance(blockFragment, anchor))) / SQRT2;
                float angle = atan(anchor.y - blockFragment.y, anchor.x - blockFragment.x);
                float strobe = 1 + 2 * noise(vec3(loopValue(time + randInt(nextBlockIndex), 100000) * 100, 2 + sin(angle) * 2, 2 + cos(angle) * 2));
                receiveMaxLight[k] = max(nextBlock.emit, nextBlock.sky);
                receiveLight[k] = receiveMaxLight[k] * pow(dist, strobe);
            }
            for (int iteration = 0; iteration < LIGHT_ACCURACY; ++iteration) {
                for (int k = 0; k < 8; ++k) {
                    light = max(light, max(receiveLight[k], min(receiveMaxLight[k], light + receiveLight[k])));
                }
            }
            return vec4(0, 0, 0, 1 - light);
        }
        textureData = textures[block.type];
        animation_start = randInt(blockIndex);
        pos.x = blockFragment.x;
        pos.y = 1 - blockFragment.y;
        if (textureData.textureStyle == TEXTURE_STYLE_4_SIMPLE_VARIATION) {
            int variation = abs(randInt(blockIndex)) % 4;
            pos = translateSquareIndex(pos, 2, variation);
        }
        if (textureData.textureStyle == TEXTURE_STYLE_4_ADJACENT8_VARIATION) {
            int variation = abs(randInt(blockIndex)) % 4;
            int dx = 1 + (variation % 2) * 3;
            int dy = 1 + (variation / 2) * 3;
            pos = translateSquarePos(pos, 6, dx, dy);
        }
        for (int k = 0; k < 8; ++k) {
            int nextBlockIndex = blockIndex + ANCHOR_DELTA_INDEX[k];
            if (blockX + ANCHOR_DELTA_X[k] < 0 || blockY + ANCHOR_DELTA_Y[k] < 0 || blockX + ANCHOR_DELTA_X[k] >= BLOCKS_X || blockY + ANCHOR_DELTA_Y[k] >= BLOCKS_Y) continue;
            Block nextBlock = blocks[nextBlockIndex];
            TextureData nextTextureData = textures[nextBlock.type];
            if (nextTextureData.textureStyle != TEXTURE_STYLE_4_ADJACENT8_VARIATION || nextBlock.type == block.type) continue;
            int variation = abs(randInt(nextBlockIndex)) % 4;
            int dx = 1 + (variation % 2) * 3 - ANCHOR_DELTA_X[k];
            int dy = 1 + (variation / 2) * 3 + ANCHOR_DELTA_Y[k];
            overlapTexel = mix4(overlapTexel, resolveTexture(nextTextureData, randInt(nextBlockIndex), quad.animation_period, translateSquarePos(pos, 6, dx, dy)));
        }
    }
    if (textureData.textureStyle == TEXTURE_STYLE_EMPTY) {
        return overlapTexel;
    }
    if (textureData.textureStyle == TEXTURE_STYLE_LAVA) {
        vec3 orange = vec3(1., .45, 0.);
        vec3 yellow = vec3(1., 1., 0.);
        // TODO make lava better https://www.shadertoy.com/view/llsBR4 https://www.shadertoy.com/view/lslXRS https://thebookofshaders.com/edit.php#11/lava-lamp.frag http://www.science-and-fiction.org/rendering/noise.html
        float noiseValue = noise(vec3(realX, realY, loopValue(time, 10000) * 10));
        return mix4(vec4(mix(yellow, orange, vec3(smoothstep(0., 1., noiseValue))), 0.9f), overlapTexel);
    }
    if (textureData.textureStyle == TEXTURE_STYLE_FONT256) {
        pos = translateSquareIndex(pos, POWER4, quad.meta1);
    }
    return mix4(resolveTexture(textureData, animation_start, quad.animation_period, pos), overlapTexel);
}

void main(void) {
    out_Color = resolveQuadTexel();
}