package com.ternsip.soil.graph.display;

import com.ternsip.soil.common.Utils;
import com.ternsip.soil.graph.shader.TextureType;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

/**
 * There are two types of atlases:
 * - GPU 3d array of textures
 * It is important for mipmap levels to be strictly one due to next reasons:
 * For fast-dynamic-texture changes
 * To not smooth 2d picture in far distance
 * There is no significant impact on performance
 */
@Slf4j
public class TextureRepository {

    public final static File MISSING_TEXTURE = new File("soil/tools/missing.jpg");
    public final static int[] ATLAS_RESOLUTIONS = new int[]{16, 32, 64, 128, 256, 512, 1024, 2048, 4096, 8192};
    public final Texture2D[] atlases;
    public final Map<File, Texture> fileToTexture;
    public final List<Texture2D> textures2D = new ArrayList<>();

    public TextureRepository() {

        ArrayList<Image> images = Utils.getResourceListing(new File("soil"), Image.EXTENSIONS)
                .stream()
                .map(Image::new)
                .collect(Collectors.toCollection(ArrayList::new));

        Set<Image> usedImages = new HashSet<>();

        this.atlases = new Texture2D[ATLAS_RESOLUTIONS.length];
        this.fileToTexture = new HashMap<>();

        for (int atlasNumber = 0; atlasNumber < ATLAS_RESOLUTIONS.length; ++atlasNumber) {

            final int atlasResolution = ATLAS_RESOLUTIONS[atlasNumber];
            this.atlases[atlasNumber] = new Texture2D(atlasResolution, atlasResolution, glGenTextures(), textures2D.size());
            textures2D.add(this.atlases[atlasNumber]);

            glBindTexture(GL_TEXTURE_2D_ARRAY, this.atlases[atlasNumber].bindId);

            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP);
            glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP);


            ArrayList<Image> suitableImages = images
                    .stream()
                    .filter(image -> !usedImages.contains(image) && image.width <= atlasResolution && image.height <= atlasResolution)
                    .collect(Collectors.toCollection(ArrayList::new));

            usedImages.addAll(suitableImages);

            int depth = suitableImages.stream().mapToInt(e -> e.frameData.length).sum();

            glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, atlasResolution, atlasResolution, Math.max(1, depth));
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

        for (Texture2D atlas : atlases) {
            atlas.bind();
        }
    }

    public void updateTexture(Texture texture, ByteBuffer byteBuffer, int startX, int startY, int width, int height, int layerOffset) {
        glActiveTexture(GL_TEXTURE0 + atlases[texture.atlasNumber].activationId);
        glTexSubImage3D(GL_TEXTURE_2D_ARRAY, 0, startX, startY, texture.layerStart + layerOffset, width, height, 1, GL_RGBA, GL_UNSIGNED_BYTE, byteBuffer);
        glActiveTexture(GL_TEXTURE0);
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

    public Texture2D registerTexture2D(int width, int height) {
        int textureBindId = glGenTextures();
        int textureActivationId = textures2D.size();
        glActiveTexture(GL_TEXTURE0 + textureActivationId);
        glBindTexture(GL_TEXTURE_2D, textureBindId);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_INT, (ByteBuffer) null);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, 0);
        Texture2D texture2D = new Texture2D(width, height, textureBindId, textureActivationId);
        textures2D.add(texture2D);
        return texture2D;
    }

    public boolean isTextureExists(File file) {
        return fileToTexture.containsKey(file);
    }

    public void finish() {
        for (Texture2D texture2D : textures2D) {
            texture2D.unbind();
            texture2D.delete();
        }
    }

}
