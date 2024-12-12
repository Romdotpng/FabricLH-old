#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D PrevSampler;
//uniform sampler2D MinecraftSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform float GameTime;
uniform float timeMult;

uniform vec2 ScreenSize;

uniform vec4 color1;
uniform vec4 color2;
uniform vec4 color3;
uniform vec4 color4;
uniform bool rainbow;
uniform float range;
uniform float saturation;
uniform float brightness;
uniform bool applySin;
uniform float customAlpha;
uniform bool mixAlphas;
uniform bool originalAlpha;
uniform bool centred;
uniform float blurRadius;
uniform float blurX;
uniform float blurY;
uniform float blurMix;

out vec4 fragColor;

vec4 mixColors(vec4 color1, vec4 color2, vec4 color3, vec4 color4, float progress1, float progress2, float progress3, float progress4) {
    return vec4(
        color1.x * progress1 + color2.x * progress2 + color3.x * progress3 + color4.x * progress4,
        color1.y * progress1 + color2.y * progress2 + color3.y * progress3 + color4.y * progress4,
        color1.z * progress1 + color2.z * progress2 + color3.z * progress3 + color4.z * progress4,
        color1.w * progress1 + color2.w * progress2 + color3.w * progress3 + color4.w * progress4
    );
}

vec4 mixTwoColors(vec4 color1, vec4 color2, float progress) {
    return vec4(
        color1.x * progress + color2.x * (1.0 - progress),
        color1.y * progress + color2.y * (1.0 - progress),
        color1.z * progress + color2.z * (1.0 - progress),
        color1.w * progress + color2.w * (1.0 - progress)
    );
}

vec3 hsv2rgb(vec3 c) {
    vec4 K = vec4(1.0, 2.0 / 3.0, 1.0 / 3.0, 3.0);
    vec3 p = abs(fract(c.xxx + K.xyz) * 6.0 - K.www);
    return c.z * mix(K.xxx, clamp(p - K.xxx, 0.0, 1.0), c.y);
}

float distance0(vec2 vec) {
    return sqrt(vec.x * vec.x + vec.y * vec.y) * 0.01;
}

vec4 blur(vec4 color) {
    /*vec4 currentColor = texture(PrevSampler, texCoord);

    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    float totalAlpha = 0.0;
    float totalSamples = 0.0;

    if(currentColor.a != 0.0 && blurRadius != 0.0) {
        for(float r = -blurRadius; r <= blurRadius; r += 1.0) {
            vec4 sampleValue = texture(PrevSampler, texCoord + oneTexel * r * vec2(blurX, blurY));

            totalAlpha = totalAlpha + sampleValue.a;
            totalSamples = totalSamples + 1.0;

            float strength = 1.0 - abs(r / blurRadius);
            totalStrength = totalStrength + strength;
            blurred = blurred + sampleValue;
        }

        vec4 blurColor = vec4(blurred.rgb / (blurRadius * 2.0 + 1.0), totalAlpha);

        return mixTwoColors(blurColor, color, blurMix);
    } else {
        return color;
    }*/
    return color;
}

void main() {
    vec2 offset = vec2(0.0);

    if(centred) {
        offset = -ScreenSize / 2.0;
    }

    vec4 currentColor = texture(DiffuseSampler, texCoord);
    vec4 originalColor = texture(PrevSampler, texCoord);
    float time = GameTime * timeMult;
    float distance = (distance0(texCoord + offset) + time) / range;

    if(applySin) {
        distance = (sin(distance) + 1.0) / 2.0;
    }

    if(rainbow) {
        float alpha = customAlpha;
        float progress = (distance - float(int(distance))) / range;

        if(mixAlphas) {
            alpha *= currentColor.a;
        }

        fragColor = blur(vec4(hsv2rgb(vec3(progress, saturation, brightness)), alpha));
    } else {
        float distance2 = (distance0(texCoord + offset - vec2(800.0, 0.0)) + time) / range;
        float distance3 = (distance0(texCoord + offset - vec2(400.0, 400.0)) + time) / range;

        distance /= range;
        distance2 /= range;
        distance3 /= range;

        if(applySin) {
            distance2 = (sin(distance2) + 1.0) / 2.0;
            distance3 = (sin(distance3) + 1.0) / 2.0;
        }

        float distance1 = 1.0 - distance;

        vec4 color = mixColors(color1, color2, color3, color4, distance, distance1, distance2, distance3);

        if(mixAlphas) {
            color = vec4(color.xyz, color.a * currentColor.a);
        }

        if(originalAlpha || (currentColor.a != 0.0 && originalColor.a == 0.0)) {
            color = vec4(color.xyz, currentColor.a);
        }

        fragColor = blur(color);
    }
}
