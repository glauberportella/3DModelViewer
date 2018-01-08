#version 150
layout(triangles) in;

// Three lines will be generated: 6 vertices
//layout(line_strip, max_vertices=6) out;
layout(triangle_strip, max_vertices=3) out;


//uniform float normal_length;
#define NR_POINT_LIGHTS 4
float normal_length=0.1f;
//uniform mat4 projectionMatrix;

//in Vertex
//{
//  vec4 normal;
//  vec4 color;
//} vertex[];

//out vec4 vertex_color;

//in VertexData {
    in vec2 gTexCoords;
    in vec3 gNormal;
    in vec3 gFragPos;
    in vec4 gFragPosLightSpaceDir;
    in vec4 gFragPosLightSpacePoint[NR_POINT_LIGHTS];
//} vsIn;

//out VertexData {
    out vec3 FragPos;
    out vec3 Normal;
    out vec2 TexCoords;
    out vec4 FragPosLightSpaceDir;
    out vec4 FragPosLightSpacePoint[NR_POINT_LIGHTS];
//} vsOut;

void main()
{
//    gl_Position = gl_in[0].gl_Position + vec4(-0.1, 0.0, 0.0, 0.0);
//    EmitVertex();
//
//    gl_Position = gl_in[0].gl_Position + vec4( 0.1, 0.0, 0.0, 0.0);
//    EmitVertex();
//
//    EndPrimitive();
//
//    vertex_color = vertex[0].color;

//    gl_Position = vec4(-1,1,0,1);
//    EmitVertex();
//    gl_Position = vec4(1,1,0,1);
//    EmitVertex();
//    gl_Position = vec4(1,-1,0,1);
//    EmitVertex();
//     EndPrimitive();
///    vertex_color = vertex[i].color;
//    EmitVertex();
//
////    gl_Position = projectionMatrix * vec4(P + N * normal_length, 1.0);
////    vertex_color = vertex[i].color;
////    EmitVertex();
//
//  }
  int i;
  for(i=0; i<gl_in.length(); i++)
  {
//    vec3 P = gl_in[i].gl_Position.xyz;
//    vec3 N = vertex[i].normal.xyz;

    FragPos = gFragPos;
    Normal = gNormal;
    TexCoords = gTexCoords;
    FragPosLightSpaceDir = gFragPosLightSpaceDir;
    for (int l = 0; l < NR_POINT_LIGHTS; l ++) {
    FragPosLightSpacePoint[l] = gFragPosLightSpacePoint[l];
    }

    gl_Position = gl_in[i].gl_Position;//projectionMatrix * vec4(P, 1.0);
//    vertex_color = vertex[i].color;
    EmitVertex();

//    gl_Position = projectionMatrix * vec4(P + N * normal_length, 1.0);
//    vertex_color = vertex[i].color;
//    EmitVertex();

  }
    EndPrimitive();
//
//  for(i=0; i<gl_in.length(); i++)
//  {
////    vec3 P = gl_in[i].gl_Position.xyz;
////    vec3 N = vertex[i].normal.xyz;
//
//    gl_Position = gl_in[i].gl_Position + vec4(-0.5, 0.0, 0.0, 0.0);
////    vertex_color = vertex[i].color;
//    EmitVertex();
//
////    gl_Position = projectionMatrix * vec4(P + N * normal_length, 1.0);
////    vertex_color = vertex[i].color;
////    EmitVertex();
//
//  }
//    EndPrimitive();
}



//// Displays the normal of each triangle
////#version 150
//
//
//#version 330 core
//
//// Match with our Mesh
////layout (location = 0) in vec3 aPos;
////layout (location = 1) in vec3 aNormal;
////layout (location = 2) in vec2 aTexCoords;
//
//#define NR_POINT_LIGHTS 4
//
////out VS_OUT {
//out vec3 FragPos;
//out vec3 Normal;
//out vec2 TexCoords;
//out vec4 FragPosLightSpaceDir;
//out vec4 FragPosLightSpacePoint[NR_POINT_LIGHTS];
////} vs_out;
//
//uniform mat4 projectionMatrix;
//uniform mat4 viewMatrix;
//uniform mat4 modelMatrix;
////uniform mat4 lightSpaceMatrix;
//
//uniform mat4 lightSpaceMatrixDir;
//uniform mat4 lightSpaceMatrixes[NR_POINT_LIGHTS];
//
////void main()
////{
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
////}
//
//
//
//
//// Match with our glDrawElements call
//layout(triangles) in;
//
//// Three lines will be generated: 6 vertices
//layout(line_strip, max_vertices=6) out;
//
////uniform float normal_length;
//float normal_length = 0.1f;
////uniform mat4 projectionMatrix;
//
////in Vertex
////{
////  vec4 normal;
////  vec4 color;
////} vertex[];
//
//in vec2 TexCoords;
//in vec3 Normal;
//in vec3 FragPos;
//in vec4 FragPosLightSpaceDir;
//in vec4 FragPosLightSpacePoint[NR_POINT_LIGHTS];
//
//out vec4 vertex_color;
//
//void main()
//{
//  int i;
//
//   FragPos = vec3(modelMatrix * vec4(aPos, 1.0));
//      //Normal = mat3(transpose(inverse(modelMatrix))) * aNormal;
//  //    Normal = transpose(inverse(mat3(modelMatrix))) * aNormal;
//    Normal = transpose(inverse(mat3(modelMatrix))) * aNormal;
//    TexCoords = aTexCoords;
//    FragPosLightSpaceDir = lightSpaceMatrixDir * vec4(FragPos, 1.0);
//    //    FragPosLightSpaceDir = vec4(FragPos, 1.0);
//    for (int i = 0; i < NR_POINT_LIGHTS; i ++) {
//    FragPosLightSpacePoint[i] = lightSpaceMatrixes[i] * vec4(FragPos, 1.0);
//    }
//    gl_Position = projectionMatrix * viewMatrix * modelMatrix * vec4(aPos, 1.0);
//
//
//  for(i=0; i<gl_in.length(); i++)
//  {
//    vec3 P = gl_in[i].gl_Position.xyz;
//    vec3 N = vertex[i].normal.xyz;
//
//    gl_Position = projectionMatrix * vec4(P, 1.0);
//    vertex_color = vertex[i].color;
//    EmitVertex();
//
//    gl_Position = projectionMatrix * vec4(P + N * normal_length, 1.0);
//    vertex_color = vertex[i].color;
//    EmitVertex();
//
//    EndPrimitive();
//  }
//}
