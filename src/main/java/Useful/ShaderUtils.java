package Useful;

import org.lwjgl.opengl.GL20;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class ShaderUtils {
    public static boolean checkShaderStatus(int shaderId) {

        if (GL20.glGetShaderi(shaderId, GL20.GL_COMPILE_STATUS) == 0) {
            String infoLog = GL20.glGetShaderInfoLog(shaderId);
            System.err.println("Failed to compile shader: " + infoLog);
            return false;
        }

        return true;
    }

    public static int loadShader(String filename, int type) {
        int shaderId = 0;

        try {

            BufferedReader reader = new BufferedReader(new FileReader(filename));
            shaderId = loadShaderInternal(reader, type);
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }

        return shaderId;
    }

    public static int loadShader(URL resource, int type) {
        assert (resource != null);
        int shaderId = 0;

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream()));
            shaderId = loadShaderInternal(reader, type);
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }

        return shaderId;
    }


    private static int loadShaderInternal(BufferedReader reader, int type) {
        StringBuilder shaderSource = new StringBuilder();
        int shaderID = 0;

        try {
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
        } catch (IOException e) {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }

        shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
        ShaderUtils.checkShaderStatus(shaderID);
        return shaderID;
    }
}
