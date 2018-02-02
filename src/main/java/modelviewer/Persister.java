package modelviewer;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.Map;

public class Persister {
    private static final String DB_NAME = "settings.db";

    public static <T> T get(String key) {
        DB db = DBMaker.fileDB(DB_NAME).closeOnJvmShutdown().make();
        Map persisted = db.hashMap("map").createOrOpen();
        T out = (T) persisted.get(key);
        db.close();
        return out;
    }

    public static <T> T getOrElse(String key, T def) {
        DB db = DBMaker.fileDB(DB_NAME).closeOnJvmShutdown().make();
        Map persisted = db.hashMap("map").createOrOpen();
        T out = (T) persisted.get(key);
        db.close();
        if (out != null) {
            return out;
        }
        else {
            return def;
        }
    }

    public static <T> void put(String key, T value) {
        DB db = DBMaker.fileDB(DB_NAME).closeOnJvmShutdown().make();
        Map persisted = db.hashMap("map").createOrOpen();
        persisted.put(key, value);
        db.close();
    }
}
