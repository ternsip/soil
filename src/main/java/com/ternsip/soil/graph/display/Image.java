package com.ternsip.soil.graph.display;

import com.madgag.gif.fmsware.GifDecoder;
import com.sun.imageio.plugins.gif.GIFImageReader;
import com.sun.imageio.plugins.gif.GIFImageReaderSpi;
import com.ternsip.soil.common.Maths;
import com.ternsip.soil.common.Utils;
import lombok.SneakyThrows;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import java.awt.image.BufferedImage;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Image {

    public static final String[] EXTENSIONS = {"jpg", "png", "bmp", "jpeg", "gif"};
    public static final int COMPONENT_RGBA = 4;

    public final File file;
    public final int width;
    public final int height;
    public final byte[][] frameData;
    public final int[] frameDelay;

    @SneakyThrows
    public Image(File file) {
        this.file = file;
        if (file.getName().endsWith("gif")) {
            // TODO  com.sun.imageio.plugins.gif.GIFImageReader(Spi) is internal proprietary API and may be removed in a future release
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
            this.frameData = new byte[][]{Utils.bufferToArray(STBImage.stbi_load_from_memory(imageData, w, h, avChannels, COMPONENT_RGBA))};
            this.frameDelay = new int[]{0};
            this.width = w.get();
            this.height = h.get();
        }
    }

    public static byte[] bufferedImageToBitmapRGBA(BufferedImage image) {
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

    public int getOffset(int x, int y) {
        return (y * width + x) * COMPONENT_RGBA;
    }

    public int getRGBA(int frame, int x, int y) {
        int offset = getOffset(x, y);
        return Maths.packRGBA(
                frameData[frame][offset],
                frameData[frame][offset + 1],
                frameData[frame][offset + 2],
                frameData[frame][offset + 3]
        );
    }

}