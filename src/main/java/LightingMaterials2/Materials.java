package LightingMaterials2;

import enterthematrix.Vector3;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

class Material {
    public String getName() {
        return name;
    }

    public Vector3 getAmbient() {
        // Compute ambientIntensity as (0.212671*ambr + 0.715160*ambg + 0.072169*ambb)/(0.212671*difr + 0.715160*difg + 0.072169*difb)
        return ambient;
    }

    public float getAmbientIntensity() {
        // Compute ambientIntensity as (0.212671*ambr + 0.715160*ambg + 0.072169*ambb)/(0.212671*difr + 0.715160*difg + 0.072169*difb)
        return (float) (0.212671f*ambient.x() + 0.715160f*ambient.y() + 0.072169f*ambient.z() / 0.212671*diffuse.x() + 0.715160*diffuse.y() + 0.072169*diffuse.z());
    }

    public Vector3 getDiffuse() {
        return diffuse;
    }

    public Vector3 getSpecular() {
        return specular;
    }

    public float getShininess() {
        return shininess;
    }

    private final String name;
    private final Vector3 ambient;
    private final Vector3 diffuse;
    private final Vector3 specular;
    private final float shininess;

    Material(String name, Vector3 ambient, Vector3 diffuse, Vector3 specular, float shininess) {
        this.name = name;
        this.ambient = ambient;
        this.diffuse = diffuse;
        this.specular = specular;
        this.shininess = shininess;
    }
}

class Materials {
    // http://devernay.free.fr/cours/opengl/materials.html
    static private String materialsRaw = "emerald\t0.0215\t0.1745\t0.0215\t0.07568\t0.61424\t0.07568\t0.633\t0.727811" +
            "\t0.633\t0.6\n" +
            "jade\t0.135\t0.2225\t0.1575\t0.54\t0.89\t0.63\t0.316228\t0.316228\t0.316228\t0.1\n" +
            "obsidian\t0.05375\t0.05\t0.06625\t0.18275\t0.17\t0.22525\t0.332741\t0.328634\t0.346435\t0.3\n" +
            "pearl\t0.25\t0.20725\t0.20725\t1\t0.829\t0.829\t0.296648\t0.296648\t0.296648\t0.088\n" +
            "ruby\t0.1745\t0.01175\t0.01175\t0.61424\t0.04136\t0.04136\t0.727811\t0.626959\t0.626959\t0.6\n" +
            "turquoise\t0.1\t0.18725\t0.1745\t0.396\t0.74151\t0.69102\t0.297254\t0.30829\t0.306678\t0.1\n" +
            "brass\t0.329412\t0.223529\t0.027451\t0.780392\t0.568627\t0.113725\t0.992157\t0.941176\t0.807843" +
            "\t0.21794872\n" +
            "bronze\t0.2125\t0.1275\t0.054\t0.714\t0.4284\t0.18144\t0.393548\t0.271906\t0.166721\t0.2\n" +
            "chrome\t0.25\t0.25\t0.25\t0.4\t0.4\t0.4\t0.774597\t0.774597\t0.774597\t0.6\n" +
            "copper\t0.19125\t0.0735\t0.0225\t0.7038\t0.27048\t0.0828\t0.256777\t0.137622\t0.086014\t0.1\n" +
            "gold\t0.24725\t0.1995\t0.0745\t0.75164\t0.60648\t0.22648\t0.628281\t0.555802\t0.366065\t0.4\n" +
            "silver\t0.19225\t0.19225\t0.19225\t0.50754\t0.50754\t0.50754\t0.508273\t0.508273\t0.508273\t0.4\n" +
            "black plastic\t0.0\t0.0\t0.0\t0.01\t0.01\t0.01\t0.50\t0.50\t0.50\t0.25\n" +
            "cyan plastic\t0.0\t0.1\t0.06\t0.0\t0.50980392\t0.50980392\t0.50196078\t0.50196078\t0.50196078\t0.25\n" +
            "green plastic\t0.0\t0.0\t0.0\t0.1\t0.35\t0.1\t0.45\t0.55\t0.45\t0.25\n" +
            "red plastic\t0.0\t0.0\t0.0\t0.5\t0.0\t0.0\t0.7\t0.6\t0.6\t0.25\n" +
            "white plastic\t0.0\t0.0\t0.0\t0.55\t0.55\t0.55\t0.70\t0.70\t0.70\t0.25\n" +
            "yellow plastic\t0.0\t0.0\t0.0\t0.5\t0.5\t0.0\t0.60\t0.60\t0.50\t0.25\n" +
            "black rubber\t0.02\t0.02\t0.02\t0.01\t0.01\t0.01\t0.4\t0.4\t0.4\t.078125\n" +
            "cyan rubber\t0.0\t0.05\t0.05\t0.4\t0.5\t0.5\t0.04\t0.7\t0.7\t.078125\n" +
            "green rubber\t0.0\t0.05\t0.0\t0.4\t0.5\t0.4\t0.04\t0.7\t0.04\t.078125\n" +
            "red rubber\t0.05\t0.0\t0.0\t0.5\t0.4\t0.4\t0.7\t0.04\t0.04\t.078125\n" +
            "white rubber\t0.05\t0.05\t0.05\t0.5\t0.5\t0.5\t0.7\t0.7\t0.7\t.078125\n" +
            "yellow rubber\t0.05\t0.05\t0.0\t0.5\t0.5\t0.4\t0.7\t0.7\t0.04\t.078125";
    private final List<Material> materials;

    Materials() {
        materials = Arrays.stream(materialsRaw.split("\n")).map(line -> {
//            String[] tokens = line.split("\t");
            Scanner scan = new Scanner(line);
            scan.useDelimiter("\t");
            Material m = new Material(
                    scan.next(),
                    new Vector3(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()),
                    new Vector3(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()),
                    new Vector3(scan.nextFloat(), scan.nextFloat(), scan.nextFloat()),
                    scan.nextFloat() * 128.0f);
            return m;
        }).collect(Collectors.toList());
    }

    public int getLength() { return materials.size(); }
    public Material get(int idx) { return materials.get(idx); }
}
