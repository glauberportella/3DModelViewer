package modelviewer;

import jassimp.AiTextureMapMode;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.io.*;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.system.MemoryStack.stackPush;

interface Texture {
    public int getTextureId();
}

class TextureFromExisting implements Texture {
    int textureId;

    public int getTextureId() {
        return textureId;
    }

    TextureFromExisting(int textureId) {
        this.textureId = textureId;
    }

}

class TextureFromFile implements Texture {
    public int getTextureId() {
        return textureId;
    }

    final private int textureId;

    public TextureFromFile(URL resource) throws IOException {
        this(resource, AiTextureMapMode.WRAP, AiTextureMapMode.WRAP);
    }

    public TextureFromFile(URL resource, AiTextureMapMode mapModeS, AiTextureMapMode mapModeT) throws IOException {
        this(resource.getFile(), mapModeS, mapModeT);
    }

    public TextureFromFile(String filenameFull) throws IOException {
        this(filenameFull, AiTextureMapMode.WRAP, AiTextureMapMode.WRAP);
    }

    public TextureFromFile(String filenameFull, AiTextureMapMode mapModeS, AiTextureMapMode mapModeT) throws
            IOException {
        // Create a new texture object in memory and bind it
        ByteBuffer imageBuffer;
        imageBuffer = ioResourceToByteBuffer(new File(filenameFull), 8 * 1024);

        try (MemoryStack stack = stackPush()) {
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);

            // Use info to read image metadata without decoding the entire image.
            // We don't need this for this demo, just testing the API.
            if (!stbi_info_from_memory(imageBuffer, w, h, comp)) {
                throw new IOException("Failed to read image information: " + stbi_failure_reason());
            } else {
//                System.out.println("OK with reason: " + stbi_failure_reason());
            }

//            System.out.println("Image: " + filenameFull);
//            System.out.println("Image width: " + w.get(0));
//            System.out.println("Image height: " + h.get(0));
//            System.out.println("Image components: " + comp.get(0));
//            System.out.println("Image HDR: " + stbi_is_hdr_from_memory(imageBuffer));

            // Decode the image
            ByteBuffer image = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (image == null) {
                throw new RuntimeException("Failed to load image: " + stbi_failure_reason());
            }

            textureId = GL11.glGenTextures();
//        GL13.glActiveTexture(textureUnit);
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId);

            // All RGB bytes are aligned to each other and each component is 1 byte
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);

            // Upload the texture data and generate mip maps (for scaling)
            if (comp.get(0) == 4) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, w.get(0), h.get(0), 0, GL11.GL_RGBA,
                        GL11.GL_UNSIGNED_BYTE, image);
            } else if (comp.get(0) == 3) {
                GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGB, w.get(0), h.get(0), 0, GL11.GL_RGB,
                        GL11.GL_UNSIGNED_BYTE, image);
            } else {
                assert (false);
            }
//            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);

            // Setup the ST coordinate system
            switch (mapModeS) {
                case WRAP:
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_REPEAT);
                    break;
                case CLAMP:
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL11.GL_CLAMP);
                    break;
                default:
                    System.out.println("Warning: cannot handle texture mode");
            }

            switch (mapModeT) {
                case WRAP:
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_REPEAT);
                    break;
                case CLAMP:
                    GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL11.GL_CLAMP);
                    break;
                default:
                    System.out.println("Warning: cannot handle texture mode");
            }

            // Setup what to do when the texture has to be scaled
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
//            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR_MIPMAP_LINEAR);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
//        GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_LINEAR);

            stbi_image_free(image);
        }

    }


    public static ByteBuffer ioResourceToByteBuffer(File file, int bufferSize) throws IOException {
        ByteBuffer buffer;
        if (file.isFile()) {
            FileInputStream fis = new FileInputStream(file);
            FileChannel fc = fis.getChannel();
            buffer = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            fc.close();
            fis.close();
        } else {
            buffer = BufferUtils.createByteBuffer(bufferSize);
            InputStream source = new FileInputStream(file);
            if (source == null)
                throw new FileNotFoundException(file.getCanonicalPath());
            try {
                ReadableByteChannel rbc = Channels.newChannel(source);
                try {
                    while (true) {
                        int bytes = rbc.read(buffer);
                        if (bytes == -1)
                            break;
                        if (buffer.remaining() == 0)
                            buffer = resizeBuffer(buffer, buffer.capacity() * 2);
                    }
                    buffer.flip();
                } finally {
                    rbc.close();
                }
            } finally {
                source.close();
            }
        }
        return buffer;
    }

    private static ByteBuffer resizeBuffer(ByteBuffer buffer, int newCapacity) {
        ByteBuffer newBuffer = BufferUtils.createByteBuffer(newCapacity);
        buffer.flip();
        newBuffer.put(buffer);
        return newBuffer;
    }

}
