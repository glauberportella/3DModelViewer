package BasicModels;

import java.util.ArrayList;
import java.util.List;

enum ObjFileTokenType {
    Comment,
    VertexMarker,
    Vertex,
    TextureMarker,
    Texture,
    NormalMarker,
    Normal,
    ParameterSpaceMarker,
    ParameterSpace,
    FaceMarker,
    Face,
    MtlLibMarker,
    MtlLib,
    Unknown
}

abstract class ObjFileToken {
    ObjFileTokenType typ;
    ObjFileToken(ObjFileTokenType type) {
        this.typ = type;
    }
}

class ObjFileTokenString extends ObjFileToken {
    String value;
    ObjFileTokenString(ObjFileTokenType type, String value) {
        super(type);
        this.value = value;
    }
}

class ObjFileTokenFloat extends ObjFileToken {
    float value;
    ObjFileTokenFloat(ObjFileTokenType type, float value) {
        super(type);
        this.value = value;
    }
}

public class ObjFileParser {
    static List<ObjFileToken> parseLine(String line) {
        List<ObjFileToken> out = new ArrayList<>();
        String[] tokens = line.split(" ");

        boolean comment = false;
        boolean vertex = false;
        boolean texture = false;
        boolean normal = false;
        boolean parameterSpace = false;
        boolean face = false;
        boolean mtllib = false;

        switch (tokens[0]) {
            case "#":
                comment = true;
                out.add(new ObjFileTokenString(ObjFileTokenType.Comment, tokens[0]));
                break;
            case "v":
                vertex = true;
                out.add(new ObjFileTokenString(ObjFileTokenType.VertexMarker, tokens[0]));
                break;
            case "vt":
                texture = true;
                out.add(new ObjFileTokenString(ObjFileTokenType.TextureMarker, tokens[0]));
                break;
            case "vn":
                normal = true;
                out.add(new ObjFileTokenString(ObjFileTokenType.NormalMarker, tokens[0]));
                break;
            case "vp":
                parameterSpace = true;
                out.add(new ObjFileTokenString(ObjFileTokenType.ParameterSpaceMarker, tokens[0]));
                break;
            case "f":
                face = true;
                out.add(new ObjFileTokenString(ObjFileTokenType.FaceMarker, tokens[0]));
                break;
            case "mtllib":
                mtllib = true;
                out.add(new ObjFileTokenString(ObjFileTokenType.MtlLibMarker, tokens[0]));
                break;
            default:
                out.add(new ObjFileTokenString(ObjFileTokenType.Unknown, tokens[0]));
//                throw new RuntimeException("Unknown token '" + tokens[0] + "'");
        }

        for (int i = 1; i < tokens.length; i += 1) {
            if (comment) out.add(new ObjFileTokenString(ObjFileTokenType.Comment, tokens[i]));
            else if (face) {
                String[] faceTokens = tokens[i].split("//");
                
//                if (
//                out.add(new ObjFileTokenFloat(ObjFileTokenType.Texture, value));
            }
            else {
                float value = Float.parseFloat(tokens[i]);
                if (vertex) out.add(new ObjFileTokenFloat(ObjFileTokenType.Vertex, value));
                else if (texture) out.add(new ObjFileTokenFloat(ObjFileTokenType.Texture, value));
                else if (normal) out.add(new ObjFileTokenFloat(ObjFileTokenType.Normal, value));
                else if (parameterSpace) out.add(new ObjFileTokenFloat(ObjFileTokenType.ParameterSpace, value));
                else if (mtllib) out.add(new ObjFileTokenString(ObjFileTokenType.MtlLib, tokens[i]));
            }
        }

        return out;
    }
}
