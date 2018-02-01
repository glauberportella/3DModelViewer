package modelviewer;

import enterthematrix.Matrix4x4;
import enterthematrix.Vector3;
import jassimp.AiWrapperProvider;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

class Colour {
    float r, g, b, a;
}

public class JassimpWrapperProvider implements AiWrapperProvider {
    @Override
    public Object wrapVector3f(ByteBuffer buffer, int offset, int numComponents) {
        FloatBuffer b = buffer.asFloatBuffer();
        return new Vector3(b.get(offset), b.get(offset + 1), b.get(offset + 2));
    }

    @Override
    public Object wrapMatrix4f(float[] data) {
        return new Matrix4x4(data[0],data[1],data[2],data[3],
                data[4],data[5],data[6],data[7],
                data[8],data[9],data[10],data[11],
                data[12],data[13],data[14],data[15]);
    }

    @Override
    public Object wrapColor(ByteBuffer buffer, int offset) {
        FloatBuffer b = buffer.asFloatBuffer();
        Colour c = new Colour();
        c.r = b.get(offset);
        c.g = b.get(offset + 1);
        c.b = b.get(offset + 2);
        c.a = b.get(offset + 3);
        return c;
    }

    @Override
    public Object wrapSceneNode(Object parent, Object matrix, int[] meshReferences, String name) {
//        assert(false);
        return null;
    }

    @Override
    public Object wrapQuaternion(ByteBuffer buffer, int offset) {
//        assert(false);
        return null;
    }
}
