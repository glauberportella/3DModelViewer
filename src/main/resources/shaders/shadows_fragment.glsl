#version 330 core


struct DirLight {
    bool enabled;
    vec3 direction;

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
    sampler2D texture;
    sampler2D diffuse;
    sampler2D specular;
    // vec3 ambient;
    // vec3 diffuse;
    //vec3 specular;
    float shininess;
};

#define NR_POINT_LIGHTS 4
uniform PointLight pointLights[NR_POINT_LIGHTS];
uniform DirLight dirLight;
uniform Material material;
uniform vec3 viewPos;
uniform bool shadowsEnabled;

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
    //float closestDepth = texture(shadowMap, projCoords.xy).r;
    // get depth of current fragment from light's perspective
    float currentDepth = projCoords.z;

    // Bias gets rid of weird moire pattern
//    float bias = 0.005;
    float bias = max(0.05 * (1.0 - dot(normal, lightDir)), 0.005);

    // check whether current frag pos is in shadow
    //float shadow = currentDepth - bias > closestDepth  ? 1.0 : 0.0;

    float shadow = 0.0;
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
      vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, TexCoords));
      vec3 diffuse  = light.diffuse  * diff * vec3(texture(material.diffuse, TexCoords));
      vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoords));

      float shadow = 0;
      if (shadowsEnabled) {
        shadow = ShadowCalculation(FragPosLightSpaceDir, light.shadowMap, normal, lightDir);
      }

       return ((1.0 - shadow) * (ambient + diffuse + specular));
//     if (shadow > 0.5) {
//        return vec3(1,0,0);
//    }
//    else {
//    return ambient + diffuse + specular;
//    }
    // return vec3(shadow, 1.0);

//return vec3(FragPosLightSpace.w);

        // perform perspective divide
        vec3 projCoords = FragPosLightSpaceDir.xyz / FragPosLightSpaceDir.w;
        // transform to [0,1] range
        projCoords = projCoords * 0.5 + 0.5;
        return projCoords;
        // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
        float closestDepth = texture(light.shadowMap, projCoords.xy).r;
        // get depth of current fragment from light's perspective
        float currentDepth = projCoords.z;
        // check whether current frag pos is in shadow
        float shadowRet = currentDepth > closestDepth  ? 1.0 : 0.0;
        //return 1.0f;
        //return shadow;
//        return vec3(texture(light.shadowMap, projCoords.xy));
        return vec3(texture(light.shadowMap, projCoords.xy));
        shadow = 1 / (currentDepth - closestDepth);

       return ((1.0 - shadow) * (ambient + diffuse + specular));

//if (shadow > 0.5) {
//        return vec3(1,0,0);
//    }
//    else {
//    return ambient + diffuse + specular;
//    }

    //return vec3(closestDepth, currentDepth, 0);
    //return vec3(currentDepth);

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

    vec3 ambient  = light.ambient  * vec3(texture(material.diffuse, TexCoords));
    vec3 diffuse  = light.diffuse * diff * vec3(texture(material.diffuse, TexCoords));
    vec3 specular = light.specular * spec * vec3(texture(material.specular, TexCoords));

    ambient  *= attenuation;
    diffuse  *= attenuation;
    specular *= attenuation;

//
//
//        // perform perspective divide
//        vec3 projCoords = FragPosLightSpace.xyz / FragPosLightSpace.w;
//
//        // transform to [0,1] range
//        projCoords = projCoords * 0.5 + 0.5;
////        return FragPosLightSpace.xyz;
//        // get closest depth value from light's perspective (using [0,1] range fragPosLight as coords)
//        float closestDepth = texture(light.shadowMap, projCoords.xy).r;
//        // get depth of current fragment from light's perspective
//        float currentDepth = projCoords.z;
//        // check whether current frag pos is in shadow
//        float shadowRet = currentDepth > closestDepth  ? 1.0 : 0.0;
        //return 1.0f;
        //return shadow;
        //return closestDepth;

//    return vec3(closestDepth, currentDepth, 0);

//      if (shadow > 0.5) {
//        return vec3(1,0,0);
//    }
//    else {
//    return ambient + diffuse + specular;
//    }

    return ((1.0 - shadow) * (ambient + diffuse + specular));
    //return (ambient + (diffuse + specular));
}


void main(void) {
    // properties
    vec3 norm = normalize(Normal);
    vec3 viewDir = normalize(viewPos - FragPos);

    // phase 1: Directional lighting
    vec3 result = vec3(0,0,0);
    if (dirLight.enabled) {
        result += CalcDirLight(dirLight, norm, viewDir);
    }
    //vec3 result = CalcDirLight(dirLight, norm, viewDir);
    // phase 2: Point lights
    for(int i = 0; i < NR_POINT_LIGHTS; i++) {
        if (pointLights[i].enabled) {
            result += CalcPointLight(pointLights[i], norm, FragPos, viewDir, FragPosLightSpacePoint[i]);
        }
    }
    // phase 3: Spot light
    //result += CalcSpotLight(spotLight, norm, FragPos, viewDir);

    FragColor = vec4(result, 1.0);
    //FragColor = texture(material.texture, TexCoords);
}