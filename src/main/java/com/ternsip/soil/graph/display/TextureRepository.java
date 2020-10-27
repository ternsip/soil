package com.ternsip.soil.graph.display;

import com.sun.imageio.plugins.gif.GIFImageMetadata;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.ternsip.soil.common.Utils;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.*;
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
                    .filter(image -> !usedImages.contains(image) && image.getWidth() <= atlasResolution && image.getHeight() <= atlasResolution)
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
                    glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, 0, 0, layer, image.getWidth(), image.getHeight(), 1, GL_RGBA, GL_UNSIGNED_BYTE, Utils.arrayToBuffer(image.frameData[frame]));
                    layer++;
                }
                Texture texture = new Texture(atlasNumber, layerStart, layer - 1, image.getWidth() / (float) atlasResolution, image.getHeight() / (float) atlasResolution);
                this.fileToTexture.put(image.getFile(), texture);
            }
            glGenerateMipmap(GL_TEXTURE_2D_ARRAY);
            glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
        }

        images.forEach(image -> {
            if (!usedImages.contains(image)) {
                log.error(String.format("Image %s has not been loaded into atlas because it exceeds maximal size", image.getFile()));
            }
        });

        bind();
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

    @Getter
    private static class Image {

        public static int COMPONENT_RGBA = 4;

        private final File file;
        private final int width;
        private final int height;
        private final byte[][] frameData;

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
                this.width = imageReader.getWidth(0);
                this.height = imageReader.getHeight(0);
                BufferedImage prevFrame = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                BufferedImage canvas = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
                Graphics graphics = canvas.getGraphics();
                for (int i = 0; i < frameCount; i++) {
                    BufferedImage frame = imageReader.read(i);
                    GIFImageMetadata meta = (GIFImageMetadata) imageReader.getImageMetadata(i);
                    if (meta.disposalMethod == 1) {
                        // Do Not Dispose (Leave As Is), meaning leave canvas from previous frame
                        graphics.drawImage(frame, meta.imageLeftPosition, meta.imageTopPosition, null);
                    } else if (meta.disposalMethod == 2) {
                        // Restore to background, meaning clear canvas to background color
                        fillBufferImage(canvas, 0);
                        graphics.drawImage(frame, meta.imageLeftPosition, meta.imageTopPosition, null);
                    } else if (meta.disposalMethod == 3) {
                        // Restore to previous, meaning clear canvas to frame before last
                        fillBufferImage(canvas, 0);
                        graphics.drawImage(prevFrame, 0, 0, null);
                        graphics.drawImage(frame, meta.imageLeftPosition, meta.imageTopPosition, null);
                    } else {
                        // Unspecified disposal method, full replace
                        fillBufferImage(canvas, 0);
                        graphics.drawImage(frame, meta.imageLeftPosition, meta.imageTopPosition, null);
                    }
                    prevFrame = frame;
                    frameData[i] = bufferedImageToBitmapRGBA(canvas);
                }
                graphics.dispose();
            } else {
                ByteBuffer imageData = Utils.loadResourceToByteBuffer(file);
                IntBuffer w = BufferUtils.createIntBuffer(1);
                IntBuffer h = BufferUtils.createIntBuffer(1);
                IntBuffer avChannels = BufferUtils.createIntBuffer(1);
                this.frameData = new byte[][]{Utils.bufferToArray(stbi_load_from_memory(imageData, w, h, avChannels, COMPONENT_RGBA))};
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

        private static void fillBufferImage(BufferedImage bufferedImage, int argb) {
            for (int x = 0; x < bufferedImage.getWidth(); x++) {
                for (int y = 0; y < bufferedImage.getHeight(); y++) {
                    bufferedImage.setRGB(x, y, argb);
                }
            }
        }

    }

}
