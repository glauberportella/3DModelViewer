#version 330
//in vec4 gxl3d_Position;
//in vec4 gxl3d_Normal;

layout (location = 0) in vec3 aPos;
layout (location = 1) in vec3 aNormal;

out Vertex
{
  vec4 normal;
  vec4 color;
} vertex;

void main()
{
  gl_Position = vec4(aPos, 1);
  vertex.normal = vec4(aNormal, 1);
  vertex.color =  vec4(1.0, 1.0, 0.0, 1.0);
}


//#version 330 core
////in vec4 gxl3d_Position;
////in vec4 gxl3d_Normal;
////
////out Vertex
////{
////  vec4 normal;
////  vec4 color;
////} vertex;
////
////void main()
////{
////  gl_Position = gxl3d_Position;
////  vertex.normal = gxl3d_Normal;
////  vertex.color =  vec4(1.0, 1.0, 0.0, 1.0);
////}
//
////#version 330 core
//
//layout (location = 0) in vec3 aPos;
//layout (location = 1) in vec3 aNormal;
//layout (location = 2) in vec2 aTexCoords;
//
////#define NR_POINT_LIGHTS 4
//
////out VS_OUT {
//out vec3 FragPos;
//out vec3 Normal;
//out vec2 TexCoords;
////out vec4 FragPosLightSpaceDir;
////out vec4 FragPosLightSpacePoint[NR_POINT_LIGHTS];
////} vs_out;
//
////uniform mat4 projectionMatrix;
////uniform mat4 viewMatrix;
////uniform mat4 modelMatrix;
//////uniform mat4 lightSpaceMatrix;
////
////uniform mat4 lightSpaceMatrixDir;
////uniform mat4 lightSpaceMatrixes[NR_POINT_LIGHTS];
//
//void main()
//{
//    gl_Position = aPos;
//    Normal = aNormal;
//    TexCoords = aTexCoords;
//
////    FragPos = vec3(modelMatrix * vec4(aPos, 1.0));
////    //Normal = mat3(transpose(inverse(modelMatrix))) * aNormal;
//////    Normal = transpose(inverse(mat3(modelMatrix))) * aNormal;
////    Normal = transpose(inverse(mat3(modelMatrix))) * aNormal;
////    TexCoords = aTexCoords;
////    FragPosLightSpaceDir = lightSpaceMatrixDir * vec4(FragPos, 1.0);
//////    FragPosLightSpaceDir = vec4(FragPos, 1.0);
////    for (int i = 0; i < NR_POINT_LIGHTS; i ++) {
////        FragPosLightSpacePoint[i] = lightSpaceMatrixes[i] * vec4(FragPos, 1.0);
////    }
////    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
//}
