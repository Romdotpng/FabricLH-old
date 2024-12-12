#version 150

uniform sampler2D DiffuseSampler;

in vec2 texCoord;
in vec2 oneTexel;

uniform float radius;
uniform float divider;
uniform float maxSample;
uniform bool fill;

out vec4 fragColor;

void main() {
    vec4 currentColor = texture(DiffuseSampler, texCoord);
    float alpha = 0.0;

    if(currentColor.a == 0.0) {
        float minDistance = 114514;

        for(float x = -radius; x < radius; x++) {
            for(float y = -radius; y < radius; y++) {
                vec2 pos = texCoord + vec2(x, y) * oneTexel;
                vec4 color = texture(DiffuseSampler, pos);
                float distanceSq = x * x + y * y;

                if(distanceSq > radius * radius || distanceSq > minDistance) {
                    continue;
                }

                if(color.a != 0.0) {
                    if(divider > 0.0) {
                        alpha = alpha + max(0.0, (maxSample - sqrt(distanceSq)) / divider);
                    } else {
                        alpha = 1.0;
                    }
                }
            }
        }
    } else {
        if(fill) {
            alpha = 1.0;
        } else {
            alpha = 0.0;
        }
    }

    fragColor = vec4(1.0, 1.0, 1.0, alpha);
}
