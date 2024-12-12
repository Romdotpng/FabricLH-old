#version 150

in vec3 Position;
in vec2 UV;
in vec4 Color;

uniform mat4 ModelViewMat;
uniform mat4 ProjMat;
uniform vec2 ScreenSize;
uniform float dirX;
uniform float dirY;

out vec4 vertexColor;
out vec4 vertexCoord;
out vec2 texCoord;
out vec2 offset;

void main() {
    gl_Position = ProjMat * ModelViewMat * vec4(Position, 1.0);

    vertexColor = Color;
    vertexCoord = gl_Position;
    texCoord = UV;
//    texCoord = vertexCoord.xy;//(ScreenSize / 2.0) + (vertexCoorокd.xy / ScreenSize) * (ScreenSize / 2.0);//(vertexCoord.xy + ScreenSize) / 2.0;
    offset = vec2(dirX, dirY) / ScreenSize;

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

    texCoord = vec2(texCoordX, texCoordY);*/
}
