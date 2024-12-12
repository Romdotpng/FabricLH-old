#version 150

uniform sampler2D DiffuseSampler;
uniform sampler2D MinecraftSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform float BlurDirX;
uniform float BlurDirY;
uniform float BlurRadius;

out vec4 fragColor;

void main() {
    vec4 currentColor = texture(DiffuseSampler, texCoord);

    vec4 blurred = vec4(0.0);
    float totalStrength = 0.0;
    float totalAlpha = 0.0;
    float totalSamples = 0.0;

    if(currentColor.a != 0.0) {
        for(float r = -BlurRadius; r <= BlurRadius; r += 1.0) {
            vec4 sampleValue = texture(MinecraftSampler, texCoord + oneTexel * r * vec2(BlurDirX, BlurDirY));

            totalAlpha = totalAlpha + sampleValue.a;
            totalSamples = totalSamples + 1.0;

            float strength = 1.0 - abs(r / BlurRadius);
            totalStrength = totalStrength + strength;
            blurred = blurred + sampleValue;
        }
    }

    fragColor = vec4(blurred.rgb / (BlurRadius * 2.0 + 1.0), totalAlpha);
}
