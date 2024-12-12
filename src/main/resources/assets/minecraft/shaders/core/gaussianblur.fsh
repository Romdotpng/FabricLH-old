#version 150

//uniform sampler2D DiffuseSampler;

in vec4 vertexCoord;
in vec4 vertexColor;
in vec2 texCoord;
in vec2 offset;

uniform sampler2D MinecraftSampler;

uniform vec2 ScreenSize;
uniform float radius;

uniform float weights[256];

out vec4 fragColor;

//#define texelSize vec2(1.0) / ScreenSize
//#define direction vec2(dirX, dirY)
//#define offset texelSize * direction

void main() {
    /*float texCoordX = vertexCoord.x;
    float texCoordY = vertexCoord.y;

    if(texCoordX >= 0.0) {
        texCoordX += ScreenSize.x / 2.0;
    } else {
        texCoordX = ScreenSize.x / 2.0 -  abs(texCoordX);
    }

    if(texCoordY >= 0.0) {
        texCoordY += ScreenSize.y / 2.0;
    } else {
        texCoordY = ScreenSize.y / 2.0 -  abs(texCoordY);
    }

    vec2 texCoord = vec2(texCoordX, texCoordY);*/
    vec2 v = vertexCoord.xy;// / 1000.0;
    float length = sqrt(v.x * v.x + v.y * v.y) / 1000.0;

    fragColor = vec4(length, 0.0, 0.0, 1.0);
//    vec2 texCoord = vertexCoord.xy + ScreenSize.xy / 2.0 - ScreenSize.xy;


//    vec2 texCoord = vertexCoord
    /*vec3 color = texture(MinecraftSampler, texCoord).rgb * weights[0];
    float totalWeight = weights[0];

    for(float f = 1.0; f <= radius; f++) {
        //TODO: optimize that
        float weight = weights[int(f)];
        color += texture(MinecraftSampler, texCoord + f * offset).rgb * weight;
        color += texture(MinecraftSampler, texCoord - f * offset).rgb * weight;

        totalWeight += weight * 2.0;
    }

    fragColor = vec4(color / totalWeight, 1.0);*/
}