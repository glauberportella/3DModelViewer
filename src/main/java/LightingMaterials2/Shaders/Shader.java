package LightingMaterials2.Shaders;

import LightingMaterials2.AppWrapper;
import Useful.ShaderUtils;
import enterthematrix.Vector3;
import org.lwjgl.opengl.GL20;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.lwjgl.opengl.GL20.*;

enum ShaderVariableRequired {
    CHANGES_INFREQUENTLY,
    CHANGES_EVERY_RUN
}

class ShaderVariable {
    final ShaderVariableRequired required;
    final String name;
    final boolean ignoreUnknown;

    ShaderVariable(String name, ShaderVariableRequired required, boolean ignoreUnknown) {
        this.required = required;
        this.name = name;
        this.ignoreUnknown = ignoreUnknown;
    }

    @Override public int hashCode() {
        return name.hashCode();
    }

    public static ShaderVariable changesEveryRun(String name) {
        return new ShaderVariable(name, ShaderVariableRequired.CHANGES_EVERY_RUN, false);
    }

    public static ShaderVariable changesInfrequently(String name) {
        return new ShaderVariable(name, ShaderVariableRequired.CHANGES_INFREQUENTLY, false);
    }

    public static ShaderVariable changesEveryRunIgnoreUnknown(String name) {
        return new ShaderVariable(name, ShaderVariableRequired.CHANGES_EVERY_RUN, true);
    }
}

class ShaderVariableUse {
    private final Map<String, ShaderVariable> variables = new HashMap<>();
    private final Map<String, Integer> variableUseThisRun = new HashMap<>();
    private final Set<String> variableUseEver = new HashSet<>();
    private boolean first = true;
    private final String vtxFilename, fragFilename;
    private final String boilerplate;
    private final boolean ignoreUnknownVariables;
    private boolean usedThisRun = false;
    private final boolean logErrors = false;

    ShaderVariableUse(String vtxFilename, String fragFilename, boolean ignoreUnknownVariables) {
        this.vtxFilename = vtxFilename;
        this.fragFilename = fragFilename;
        this.ignoreUnknownVariables = ignoreUnknownVariables;
        boilerplate = " shader=(" + vtxFilename + ", " + fragFilename + ")";
    }

    public void addVariable(ShaderVariable v) {
        assert (!variables.containsKey(v.name));
        variables.put(v.name, v);
        variableUseThisRun.put(v.name, 0);
    }

    public void checkSettingVariable(String name) {
        usedThisRun = true;
        if (!variables.containsKey(name)) {
            if (!ignoreUnknownVariables) {
                if (logErrors) {
                    System.err.println("Setting shader variable '" + name + "' but haven't been told about it " + boilerplate);
                }
            }
        }
        else {
            ShaderVariable var = variables.get(name);
            int count = variableUseThisRun.get(name);
            variableUseThisRun.put(name, count + 1);
            if (!variableUseEver.contains(name)) variableUseEver.add(name);

            if (var.required == ShaderVariableRequired.CHANGES_EVERY_RUN) {
                if (count >= 1) {
                    if (logErrors) {
                        System.err.println("Setting shader variable '" + name + "' but it has already been set" + boilerplate);
                    }
                }
            }
        }
    }

    public void reset() {
        if (!first) {
            if (usedThisRun) {
                variables.entrySet().stream().forEach(kw -> {
                    ShaderVariable var = kw.getValue();
                    String name = kw.getKey();

                    if (var.required == ShaderVariableRequired.CHANGES_EVERY_RUN) {
                        int count = variableUseThisRun.get(name);
                        if (count != 1) {
                            if (logErrors) {
                                System.err.println("Shader variable '" + name + "' has not been set" + boilerplate);
                            }
                        }
                    } else if (var.required == ShaderVariableRequired.CHANGES_INFREQUENTLY) {
                        if (!variableUseEver.contains(name)) {
                            if (logErrors) {
                                System.err.println("Shader variable '" + name + "' has not been set ever" + boilerplate);
                            }
                        }
                    }

                    variableUseThisRun.put(name, 0);
                });
            }

        }
        first = false;
    }
}

public class Shader {
    public int getShaderId() {
        return shaderProgram;
    }

    private final int shaderProgram;
    private boolean inUse = false;
    private final String vtxFilename;
    private final String fragFilename;
    private final ShaderVariableUse variables;
//    private final boolean ignoreUnknownVariables;

    // Prefer using the ShaderStore over directly creating Shaders.  Allows more safety
    public static Shader create(String vertexResourceFilename, String fragmentResourceFilename) {
        return new Shader(vertexResourceFilename, fragmentResourceFilename, false);
    }

    public  static Shader create(String vertexResourceFilename, String fragmentResourceFilename, boolean ignoreUnknownVariables) {
        return new Shader(vertexResourceFilename, fragmentResourceFilename, ignoreUnknownVariables);
    }

    public Shader(String vertexResourceFilename, String fragmentResourceFilename, boolean ignoreUnknownVariables) {
        this.vtxFilename = vertexResourceFilename;
        this.fragFilename = fragmentResourceFilename;
//        this.ignoreUnknownVariables = ignoreUnknownVariables;
        variables = new ShaderVariableUse(vtxFilename, fragFilename, ignoreUnknownVariables);
        // Load the vertex shader
        int vertexShader = ShaderUtils.loadShader(AppWrapper.class.getResource(vertexResourceFilename), GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        int fragmentShader = ShaderUtils.loadShader(AppWrapper.class.getResource(fragmentResourceFilename), GL20.GL_FRAGMENT_SHADER);

        // Final steps to use the shaders
        shaderProgram = glCreateProgram();
        glAttachShader(shaderProgram, vertexShader);
        glAttachShader(shaderProgram, fragmentShader);

        // Position information will be attribute 0
        GL20.glBindAttribLocation(shaderProgram, 0, "aPos");
        // Color information will be attribute 1
//        GL20.glBindAttribLocation(shaderProgram, 1, "in_Color");
        // TextureFromFile information will be attribute 2
//        GL20.glBindAttribLocation(shaderProgram, 2, "in_TextureCoord");

        glLinkProgram(shaderProgram);
        GL20.glValidateProgram(shaderProgram);

        if (glGetProgrami(shaderProgram, GL_LINK_STATUS) == 0) {
            String error = glGetProgramInfoLog(shaderProgram);
            System.err.println("Failed to link shader: " + error);
        }

        // Cleanup
        glDeleteShader(vertexShader);
        glDeleteShader(fragmentShader);
    }

    public void addVariable(ShaderVariable v) {
        variables.addVariable(v);
    }

    public void reset() {
        variables.reset();
    }

    private void assertInUse() {
        if (!inUse) {
            System.err.println("Not in use");
            assert (false);
        }
    }

    public void setFloat(String name, Float v) {
        variables.checkSettingVariable(name);
        assertInUse();
        int location = GL20.glGetUniformLocation(getShaderId(), name);
        glUniform1f(location, v);
    }

    public void setInt(String name, int v) {
        variables.checkSettingVariable(name);
        assertInUse();
        int location = GL20.glGetUniformLocation(getShaderId(), name);
        glUniform1i(location, v);
    }

    public void setBoolean(String name, boolean v) {
        variables.checkSettingVariable(name);
        assertInUse();
        int location = GL20.glGetUniformLocation(getShaderId(), name);
        glUniform1i(location, v ? 1 : 0);
    }

    public void setVec3(String name, float x, float y, float z) {
        variables.checkSettingVariable(name);
        assertInUse();

//        try (ShaderUse wrap = new ShaderUse(this)) {
        int location = GL20.glGetUniformLocation(getShaderId(), name);
//            if (location == -1) {
//                System.err.println("Could not get shader " + name);
//                assert (location != -1);
//            }
        glUniform3f(location, x, y, z);
//        }

    }

    public void setVec3(String name, Vector3 vec) {
        setVec3(name, vec.x(), vec.y(), vec.z());
    }

    public void use() {
        glUseProgram(shaderProgram);
        inUse = true;
    }

    public void stop() {
        glUseProgram(0);
        inUse = false;
    }

    public boolean isInUse() {
        return inUse;
    }
}

