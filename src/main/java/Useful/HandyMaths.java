package Useful;

import static java.lang.Math.PI;

public class HandyMaths {

    static public float coTangent(float angle) {
        return (float)(1f / Math.tan(angle));
    }

    static public float degreesToRadians(float degrees) {
        return degrees * (float)(PI / 180d);
    }

}
