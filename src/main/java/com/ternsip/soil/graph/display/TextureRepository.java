package com.ternsip.soil.graph.display;

import com.madgag.gif.fmsware.GifDecoder;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.ternsip.soil.common.Utils;
import com.ternsip.soil.graph.shader.TextureType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage3D;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

/**
 * There are two types of atlases:
 * - GPU 3d array of textures
 */
@Slf4j
public class TextureRepository {

    public final static int MIPMAP_LEVELS = 4;
    public final static File MISSING_TEXTURE = new File("soil/tools/missing.jpg");
    public final static String[] EXTENSIONS = {"jpg", "png", "bmp", "jpeg", "gif"};
    public final static int[] ATLAS_RESOLUTIONS = new int[]{16, 32, 64, 128, 256, 512, 1024, 2048, 4096};

    private final int[] atlases;
    private final Map<File, Texture> fileToTexture;

    public TextureRepository() {

        ArrayList<Image> images = Utils.getResourceListing(EXTENSIONS)
                .stream()
                .map(Image::new)
                .collect(Collectors.toCollection(ArrayList::new));

        Set<Image> usedImages = new HashSet<>();

        this.atlases = new int[ATLAS_RESOLUTIONS.length];
        this.fileToTexture = new HashMap<>();

        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {

            this.atlases[atlasNumber] = glGenTextures();

            glBindTexture(GL_TEXTURE_2D_ARRAY, this.atlases[atlasNumber]);

            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST_MIPMAP_NEAREST);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP);

            final int atlasResolution = ATLAS_RESOLUTIONS[atlasNumber];

            ArrayList<Image> suitableImages = images
                    .stream()
                    .filter(image -> !usedImages.contains(image) && image.width <= atlasResolution && image.height <= atlasResolution)
                    .collect(Collectors.toCollection(ArrayList::new));

            usedImages.addAll(suitableImages);

            int depth = suitableImages.stream().mapToInt(e -> e.frameData.length).sum();

            glTexStorage3D(GL_TEXTURE_2D_ARRAY, MIPMAP_LEVELS, GL_RGBA8, atlasResolution, atlasResolution, Math.max(1, depth));
            ByteBuffer cleanData = Utils.arrayToBuffer(new byte[atlasResolution * atlasResolution * 4]);

            int layer = 0;
            for (Image image : suitableImages) {
                int layerStart = layer;
                for (int frame = 0; frame < image.frameData.length; ++frame) {
                    cleanData.rewind();
                    // set the whole texture to transparent (so min/mag filters don't find bad data off the edge of the actual image data)
                    glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, atlasResolution, atlasResolution, 1, GL_RGBA, GL_UNSIGNED_BYTE, cleanData);
                    glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, image.width, image.height, 1, GL_RGBA, GL_UNSIGNED_BYTE, Utils.arrayToBuffer(image.frameData[frame]));
                    layer++;
                }
                Texture texture = new Texture(atlasNumber, layerStart, layer - 1, image.width / (float) atlasResolution, image.height / (float) atlasResolution);
                this.fileToTexture.put(image.file, texture);
            }
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        }

        images.forEach(image -> {
            if (!usedImages.contains(image)) {
                log.error(String.format("Image %s has not been loaded into atlas because it exceeds maximal size", image.file));
            }
        });

        bind();
    }

    public void updateTexture(Texture texture, ByteBuffer byteBuffer, int startX, int startY, int width, int height, int layerOffset) {
        glActiveTexture(GL_TEXTURE0 + texture.atlasNumber);
        glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, startX, startY, texture.layerStart + layerOffset, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
    }

    public Texture getTexture(TextureType textureType) {
        return getTexture(textureType.file);
    }

    public Texture getTexture(File file) {
        if (!fileToTexture.containsKey(file)) {
            log.warn(String.format("Texture %s has not been found", file));
            return fileToTexture.get(MISSING_TEXTURE);
        }
        return fileToTexture.get(file);
    }

    public int[] getAtlases() {
        return atlases;
    }

    public boolean isTextureExists(File file) {
        return fileToTexture.containsKey(file);
    }

    public void finish() {
        unbind();
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glDeleteTextures(atlases[atlasNumber]);
        }
    }

    private void bind() {
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glActiveTexture(GL_TEXTURE0 + atlasNumber);
            glBindTexture(GL_TEXTURE_2D_ARRAY, atlases[atlasNumber]);
        }
    }

    private void unbind() {
        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {
            glActiveTexture(GL_TEXTURE0 + atlasNumber);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        }
    }

    public static class Image {

        public static int COMPONENT_RGBA = 4;

        public final File file;
        public final int width;
        public final int height;
        public final byte[][] frameData;
        public final int[] frameDelay;

        @SneakyThrows
        Image(File file) {
            this.file = file;
            if (file.getName().endsWith("gif")) {
                ImageReader imageReader = new GIFImageReader(new GIFImageReaderSpi());
                imageReader.setInput(ImageIO.createImageInputStream(Utils.loadResourceAsStream(file)));
                int frameCount = imageReader.getNumImages(true);
                if (frameCount <= 0) {
                    throw new IllegalArgumentException(String.format("File %s has 0 animation frames", file.getName()));
                }
                this.frameData = new byte[frameCount][];
                this.frameDelay = new int[frameCount];
                GifDecoder d = new GifDecoder();
                d.read(Utils.loadResourceAsStream(file));
                this.width = d.getFrameSize().width;
                this.height = d.getFrameSize().height;
                for (int frame = 0; frame < d.getFrameCount(); frame++) {
                    frameDelay[frame] = d.getDelay(frame);
                    frameData[frame] = bufferedImageToBitmapRGBA(d.getFrame(frame));
                }
            } else {
                ByteBuffer imageData = Utils.loadResourceToByteBuffer(file);
                IntBuffer w = BufferUtils.createIntBuffer(1);
                IntBuffer h = BufferUtils.createIntBuffer(1);
                IntBuffer avChannels = BufferUtils.createIntBuffer(1);
                this.frameData = new byte[][]{Utils.bufferToArray(stbi_load_from_memory(imageData, w, h, avChannels, COMPONENT_RGBA))};
                this.frameDelay = new int[]{0};
                this.width = w.get();
                this.height = h.get();
            }
        }

        private static byte[] bufferedImageToBitmapRGBA(BufferedImage image) {
            int width = image.getWidth();
            int height = image.getHeight();
            byte[] dataRGBA = new byte[width * height * COMPONENT_RGBA];
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    int argb = image.getRGB(x, y);
                    int offset = (y * width + x) * COMPONENT_RGBA;
                    dataRGBA[offset] = (byte) (argb >>> 16);
                    dataRGBA[offset + 1] = (byte) (argb >>> 8);
                    dataRGBA[offset + 2] = (byte) argb;
                    dataRGBA[offset + 3] = (byte) (argb >>> 24);
                }
            }
            return dataRGBA;
        }

    }

}
