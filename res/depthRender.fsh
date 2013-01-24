#version 400 core

// Useful to improve depth buffer visualzation
uniform float factor;

uniform sampler2D colorMap;

in vec2 UV;

// Ouput data
layout(location = 0) out vec4 color;

void main(){
	color = 1.0f - (1.0f - texture(colorMap, UV)) * factor;
	color.a = 1.0f;
}