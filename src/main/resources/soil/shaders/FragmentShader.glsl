#version 430 core

#define PI 3.1415926538
#define PI_2 6.28318530717
#define SQRT2 1.4142135623730

const float LIGHT_ACCURACY = 5;
const float INF = 1e6;
const float EPS = 1e-6;
const int MAX_LIGHT = 16;
const float MAX_LIGHT_F = MAX_LIGHT;
const int MAX_SAMPLERS = 16;
const int POWER4 = 16;

const int TEXTURE_STYLE_NORMAL = 0;
const int TEXTURE_STYLE_SHADOW = 1;
const int TEXTURE_STYLE_FONT256 = 2;

const int QUAD_FLAG_PINNED = 0x1;
const int QUAD_FLAG_SHADOW = 0x2;
const int QUAD_FLAG_FONT256 = 0x4;

const int SHADOW_TEXTURE_WIDTH = 7680;
const int SHADOW_TEXTURE_HEIGHT = 4320;

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

layout(pixel_center_integer) in vec4 gl_FragCoord;

layout (std430, binding = 0) readonly buffer quadBuffer {
    Quad quadData[];
};

layout (std430, binding = 1) readonly buffer textureBuffer {
    TextureData textures[];
};

in float quadIndex;
in float lightIndex;
in float lightPower;
in vec2 texture_xy;

layout(location = 0) out vec4 out_Color;

uniform vec2 cameraPos;
uniform vec2 cameraScale;
uniform vec2 aspect;
uniform int time;
uniform bool debugging;
uniform bool processingLight;
uniform sampler2D shadowTexture;
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

void main(void) {
    if (processingLight) {
        float dist = 2 * distance(texture_xy, vec2(0.5, 0.5));
        if (dist > 1) discard;
        int lightIndex = roundFloat(lightIndex);
        float intensity = 1 - dist;
        float angle = atan(texture_xy.y - 0.5f, texture_xy.x - 0.5f);
        float strobe = 1 + 2 * noise(vec3(loopValue(time + randInt(lightIndex), 100000) * 100, 2 + sin(angle) * 2, 2 + cos(angle) * 2));
        intensity = intensity + pow(intensity, strobe);
        intensity *= lightPower;
        out_Color = vec4(0, 0, 0, clamp(intensity, 0, 1));
        return;
    }
    Quad quad = quadData[roundFloat(quadIndex)];
    if ((quad.flags & QUAD_FLAG_SHADOW) != 0) {
        if (debugging) discard;
        vec2 tex_coords = vec2(gl_FragCoord.x / SHADOW_TEXTURE_WIDTH, gl_FragCoord.y / SHADOW_TEXTURE_HEIGHT);
        out_Color = texture2D(shadowTexture, tex_coords);
        out_Color.a = 1 - out_Color.a;
        return;
    }
    vec2 pos = texture_xy;
    if ((quad.flags & QUAD_FLAG_FONT256) != 0) {
        pos = translateSquareIndex(pos, POWER4, quad.meta1);
    }
    TextureData textureData = textures[quad.type];
    out_Color = resolveTexture(textureData, quad.animation_start, quad.animation_period, pos);
}