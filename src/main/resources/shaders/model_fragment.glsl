#version 330 core


struct DirLight {
    bool enabled;
    vec3 direction;

    vec3 ambientMin;
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    bool shadowsEnabled;
    sampler2D shadowMap;
};

struct PointLight {
    bool enabled;
    vec3 position;

    float constant;
    float linear;
    float quadratic;

    vec3 ambient;
    vec3 diffuse;
    vec3 specular;

    bool shadowsEnabled;
    sampler2D shadowMap;
};

struct Material {
    vec3 ambient;
    vec3 diffuse;
    vec3 specular;
    float shininess;

    sampler2D diffuseTexture;
    sampler2D specularTexture;
};

#define NR_POINT_LIGHTS 4
uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform DirLight dirLight;
uniform Material material;
uniform vec3 viewPos;
uniform bool shadowsEnabled;
uniform bool shadowsHighQuality;
uniform float shadowBiasMulti;
uniform float shadowBiasMax;
uniform bool drawTextures;
uniform bool doLighting;

out vec4 FragColor;

in vec2 TexCoords;
in vec3 Normal;
in vec3 FragPos;
in vec4 FragPosLightSpaceDir;
in vec4 FragPosLightSpacePoint[NR_POINT_LIGHTS];


float ShadowCalculation(vec4 fragPosLightSpace, sampler2D shadowMap, vec3 normal, vec3 lightDir)
{
    // perform perspective divide
    vec3 projCoords = fragPosLightSpace.xyz / fragPosLightSpace.w;

    if(projCoords.z > 1.0)
        return 0.0;


    // transform to [0,1] range
    projCoords = projCoords * 0.5 + 0.5;


    // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
    float closestDepth = texture(shadowMap, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    // Bias gets rid of weird moire pattern
//    float bias = 0.005;
//    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.00005);
    float bias = max(shadowBiasMulti * (1.0 - dot(normal, lightDir)), shadowBiasMax);

    // check whether current frag pos is in shadow
    float shadow = 0;


    if (shadowsHighQuality) {
//    if (true) {
        vec2 texelSize = 1.0 / textureSize(shadowMap, 0);
        for(int x = -1; x <= 1; ++x)
        {
            for(int y = -1; y <= 1; ++y)
            {
                float pcfDepth = texture(shadowMap, projCoords.xy + vec2(x, y) * texelSize).r;
                shadow += currentDepth - bias > pcfDepth ? 1.0 : 0.0;
            }
        }
        shadow /= 9.0;

    }
    else {
        shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;
    }

    //return 1.0f;
    return shadow;
//    return closestDepth;
}

vec3 CalcDirLight(DirLight light, vec3 normal, vec3 viewDir) {
      vec3 lightDir = normalize(-light.direction);
      // diffuse shading
      float diff = max(dot(normal, lightDir), 0.0);
      // specular shading
      vec3 reflectDir = reflect(-lightDir, normal);
      float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
      // combine results
//      vec3 ambient  = light.ambient  * vec3(texture(material.ambient, TexCoords));
//      vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, TexCoords));
//      vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoords));
      vec3 ambient  = vec3(light.ambientMin) + (light.ambient * material.ambient);
//      vec3 diffuse  = (light.diffuse * material.diffuse) + (light.ambient * material.diffuse *  vec3(texture(material.diffuseTexture, TexCoords)));
//      vec3 diffuse  = (light.diffuse * material.diffuse *  vec3(texture(material.diffuseTexture, TexCoords)));
        vec3 texDiffuse = vec3(1.0f);
        vec3 texSpecular = vec3(1.0f);
        if (drawTextures) {
//            tex = texture2D(material.diffuseTexture, TexCoords).xyz;
            texDiffuse = vec3(texture(material.diffuseTexture, TexCoords));
            texSpecular = vec3(texture(material.specularTexture, TexCoords));
        }
      vec3 diffuse  = (light.diffuse * material.diffuse * texDiffuse);
//      vec3 diffuse  = tex;
//      vec3 diffuse  = (light.ambient * vec3(texture(material.diffuseTexture, TexCoords)));
//      vec3 diffuse  = vec3(texture(material.diffuseTexture, TexCoords));
      vec3 specular = light.specular * spec * material.specular * texSpecular;
//       return (ambient + diffuse + specular);

      float shadow = 0;
      if (shadowsEnabled) {
        shadow = ShadowCalculation(FragPosLightSpaceDir, light.shadowMap, normal, lightDir);
      }

       return ((1.0 - shadow) * (ambient + diffuse + specular));
}

vec3 CalcPointLight(PointLight light, vec3 normal, vec3 fragPos, vec3 viewDir, vec4 fragPosLightSpace)
{

    vec3 lightDir = normalize(light.position - fragPos);
    // diffuse shading
    float diff = max(dot(normal, lightDir), 0.0);
    // specular shading
    vec3 reflectDir = reflect(-lightDir, normal);
    float spec = pow(max(dot(viewDir, reflectDir), 0.0), material.shininess);
    // attenuation
    float distance    = length(light.position - fragPos);
    float attenuation = 1.0 / (light.constant + light.linear * distance + light.quadratic * (distance * distance));
    // combine results

    float shadow = 0;
    if (shadowsEnabled && light.shadowsEnabled) {
        shadow = ShadowCalculation(fragPosLightSpace, light.shadowMap, normal, lightDir);
    }

//    vec3 ambient  = light.ambient  * vec3(texture(material.ambient, TexCoords));
//    vec3 diffuse  = light.diffuse * diff * vec3(texture(material.diffuse, TexCoords));
//    vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoords));
    vec3 ambient  = light.ambient  * material.ambient;
        vec3 tex = vec3(1.0f);
        vec3 texSpecular = vec3(1.0f);
        if (drawTextures) {
//            tex = texture2D(material.diffuseTexture, TexCoords).xyz;
            tex = vec3(texture(material.diffuseTexture, TexCoords));
            texSpecular = vec3(texture(material.specularTexture, TexCoords));
        }

      vec3 diffuse  = (light.diffuse * material.diffuse * tex);
    vec3 specular = light.specular * spec * material.specular * texSpecular;

    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;

    return ((1.0 - shadow) * (ambient + diffuse + specular));
}


void main(void) {
    // properties
    vec3 norm = normalize(Normal);
    vec3 viewDir = normalize(viewPos - FragPos);

if (doLighting) {
    // phase 1: Directional lighting
    vec3 result = vec3(0,0,0);
    if (dirLight.enabled) {
        result += CalcDirLight(dirLight, norm, viewDir);
    }
    // phase 2: Point lights
    for(int i = 0; i < NR_POINT_LIGHTS; i++) {
        if (pointLights[i].enabled) {
            result += CalcPointLight(pointLights[i], norm, FragPos, viewDir, FragPosLightSpacePoint[i]);
        }
    }

    FragColor = vec4(result, 1.0);
    }
    else {
        FragColor = vec4(texture(material.diffuseTexture, TexCoords).xyz, 1.0);
    }
}