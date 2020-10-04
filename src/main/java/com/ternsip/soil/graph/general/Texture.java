package com.ternsip.soil.graph.general;

import com.ternsip.soil.Soil;
import com.ternsip.soil.graph.visual.repository.TextureRepository;
import lombok.Getter;
import org.joml.Vector4f;
import org.joml.Vector4fc;

import javax.annotation.Nullable;
import java.io.File;

import static com.ternsip.soil.graph.visual.repository.TextureRepository.MISSING_TEXTURE;

@Getter
public class Texture {

    public static final Vector4f DEFAULT_COLOR = new Vector4f(1.0f, 1.0f, 1.0f, 1.0f);

    private final File file;
    private final boolean texturePresent;
    private final boolean colorPresent;
    private final Vector4fc color;
    private final TextureRepository.Texture atlasTexture;

    public Texture() {
        this(null, null);
    }

    public Texture(@Nullable Vector4fc color, @Nullable File file) {
        this.file = file != null ? file : MISSING_TEXTURE;
        this.texturePresent = file != null;
        this.colorPresent = color != null;
        this.color = colorPresent ? color : DEFAULT_COLOR;
        this.atlasTexture = Soil.THREADS.getGraphics().textureRepository.getTexture(this.file);
    }

    public Texture(File file) {
        this(null, file);
    }

    public Texture(Vector4f color) {
        this(color, null);
    }

}
