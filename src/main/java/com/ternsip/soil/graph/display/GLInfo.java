package com.ternsip.soil.graph.display;

import lombok.extern.slf4j.Slf4j;
import org.joml.Vector2i;
import org.joml.Vector3i;
import org.joml.Vector4i;
import org.lwjgl.BufferUtils;

import java.nio.IntBuffer;
import java.text.NumberFormat;

import static org.lwjgl.opengl.ARBImaging.GL_BLEND_COLOR;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER_BINDING;
import static org.lwjgl.opengl.GL15.GL_ELEMENT_ARRAY_BUFFER_BINDING;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL43.*;

@Slf4j
public class GLInfo {

    public static final NumberFormat NUMBER_FORMAT_INT = NumberFormat.getIntegerInstance();

    public static void logAttributeInfo(int programID) {
        int activeAttributes = glGetProgrami(programID, GL_ACTIVE_ATTRIBUTES);
        IntBuffer attributeType = BufferUtils.createIntBuffer(1);
        IntBuffer attributeSize = BufferUtils.createIntBuffer(1);
        for (int index = 0; index < Math.min(256, activeAttributes); ++index) {
            String attributeName = glGetActiveAttrib(programID, index, attributeSize, attributeType);
            log.debug("Active attribute[{}]: {}", index, attributeName);
        }
    }

    public static void logShaderStorageInfo() {

        // When used with indexed variants of glGet (such as glGetInteger64i_v), data returns a single value, the start offset of the binding range for each indexed shader storage buffer binding. The initial value is 0 for all bindings. See glBindBufferRange.
        log.debug("Shader storage buffer start: {}", glGetInteger64i(GL_SHADER_STORAGE_BUFFER_START, 0));

        // When used with indexed variants of glGet (such as glGetInteger64i_v), data returns a single value, the size of the binding range for each indexed shader storage buffer binding. The initial value is 0 for all bindings. See glBindBufferRange.
        log.debug("Shader storage buffer size: {}", glGetInteger64i(GL_SHADER_STORAGE_BUFFER_SIZE, 0));

    }

    public static void logFeedbackInfo() {
        // When used with non-indexed variants of glGet (such as glGetIntegerv), data returns a single value, the name of the buffer object currently bound to the target GL_TRANSFORM_FEEDBACK_BUFFER. If no buffer object is bound to this target, 0 is returned. When used with indexed variants of glGet (such as glGetIntegeri_v), data returns a single value, the name of the buffer object bound to the indexed transform feedback attribute stream. The initial value is 0 for all targets. See glBindBuffer, glBindBufferBase, and glBindBufferRange.
        log.debug("Transform feedback buffer binding: {}", glGetInteger(GL_TRANSFORM_FEEDBACK_BUFFER_BINDING));

        // When used with indexed variants of glGet (such as glGetInteger64i_v), data returns a single value, the start offset of the binding range for each transform feedback attribute stream. The initial value is 0 for all streams. See glBindBufferRange.
        log.debug("Transform feedback buffer start: {}", glGetInteger64i(GL_TRANSFORM_FEEDBACK_BUFFER_START, 0));

        // When used with indexed variants of glGet (such as glGetInteger64i_v), data returns a single value, the size of the binding range for each transform feedback attribute stream. The initial value is 0 for all streams. See glBindBufferRange.
        log.debug("Transform feedback buffer size: {}", glGetInteger64i(GL_TRANSFORM_FEEDBACK_BUFFER_SIZE, 0));

    }

    public static void logTextureInfo() {

        // data returns a single value indicating the active multitexture unit. The initial value is GL_TEXTURE0. See glActiveTexture.
        log.debug("Active texture: {}", glGetInteger(GL_ACTIVE_TEXTURE));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_1D. The initial value is 0. See glBindTexture.
        log.debug("Texture binding 1D: {}", glGetInteger(GL_TEXTURE_BINDING_1D));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_1D_ARRAY. The initial value is 0. See glBindTexture.
        log.debug("Texture binding 1D array: {}", glGetInteger(GL_TEXTURE_BINDING_1D_ARRAY));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_2D. The initial value is 0. See glBindTexture.
        log.debug("Texture binding 2D: {}", glGetInteger(GL_TEXTURE_BINDING_2D));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_2D_ARRAY. The initial value is 0. See glBindTexture.
        log.debug("Texture binding 2D array: {}", glGetInteger(GL_TEXTURE_BINDING_2D_ARRAY));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_2D_MULTISAMPLE. The initial value is 0. See glBindTexture.
        log.debug("Texture binding 2D multi sample: {}", glGetInteger(GL_TEXTURE_BINDING_2D_MULTISAMPLE));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_2D_MULTISAMPLE_ARRAY. The initial value is 0. See glBindTexture.
        log.debug("Texture binding 2D multi sample array: {}", glGetInteger(GL_TEXTURE_BINDING_2D_MULTISAMPLE_ARRAY));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_3D. The initial value is 0. See glBindTexture.
        log.debug("Texture binding 3D: {}", glGetInteger(GL_TEXTURE_BINDING_3D));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_BUFFER. The initial value is 0. See glBindTexture.
        log.debug("Texture binding buffer: {}", glGetInteger(GL_TEXTURE_BINDING_BUFFER));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_CUBE_MAP. The initial value is 0. See glBindTexture.
        log.debug("Texture binding cube map: {}", glGetInteger(GL_TEXTURE_BINDING_CUBE_MAP));

        // data returns a single value, the name of the texture currently bound to the target GL_TEXTURE_RECTANGLE. The initial value is 0. See glBindTexture.
        log.debug("Texture binding rectangle: {}", glGetInteger(GL_TEXTURE_BINDING_RECTANGLE));

        // data returns a single value indicating the mode of the texture compression hint. The initial value is GL_DONT_CARE.
        log.debug("Texture compression hint: {}", glGetInteger(GL_TEXTURE_COMPRESSION_HINT));

        // data returns a single value, the name of the buffer object currently bound to the GL_TEXTURE_BUFFER buffer binding point. The initial value is 0. See glBindBuffer.
        log.debug("Texture binding buffer: {}", glGetInteger(GL_TEXTURE_BINDING_BUFFER));

        // data returns a single value, the minimum required alignment for texture buffer sizes and offset. The initial value is 1. See glUniformBlockBinding.
        log.debug("Texture buffer offset alignment: {}", glGetInteger(GL_TEXTURE_BUFFER_OFFSET_ALIGNMENT));

    }

    // TODO should not work, it's just bunch for sorting
    public static void logBufferInfo() {

        // data returns a single value, the name of the buffer object currently bound to the target GL_ARRAY_BUFFER. If no buffer object is bound to this target, 0 is returned. The initial value is 0. See glBindBuffer.
        log.debug("Array buffer binding: {}", glGetInteger(GL_ARRAY_BUFFER_BINDING));

        // data returns a single value, the name of the buffer object currently bound to the target GL_DISPATCH_INDIRECT_BUFFER. If no buffer object is bound to this target, 0 is returned. The initial value is 0. See glBindBuffer.
        log.debug("Dispatch indirect buffer binding: {}", glGetInteger(GL_DISPATCH_INDIRECT_BUFFER_BINDING));

        // data returns one value, a symbolic constant indicating which buffers are being drawn to. See glDrawBuffer. The initial value is GL_BACK if there are back buffers, otherwise it is GL_FRONT.
        log.debug("Draw buffer: {}", glGetInteger(GL_DRAW_BUFFER));

        // GL_DRAW_BUFFER i data returns one value, a symbolic constant indicating which buffers are being drawn to by the corresponding output color. See glDrawBuffers. The initial value of GL_DRAW_BUFFER0 is GL_BACK if there are back buffers, otherwise it is GL_FRONT. The initial values of draw buffers for all other output colors is GL_NONE.
        log.debug("Draw buffer: {}", glGetInteger(GL_DRAW_BUFFER0));

        // data returns one value, the name of the framebuffer object currently bound to the GL_DRAW_FRAMEBUFFER target. If the default framebuffer is bound, this value will be zero. The initial value is zero. See glBindFramebuffer.
        log.debug("Draw framebuffer binding: {}", glGetInteger(GL_DRAW_FRAMEBUFFER_BINDING));

        // data returns one value, the name of the framebuffer object currently bound to the GL_READ_FRAMEBUFFER target. If the default framebuffer is bound, this value will be zero. The initial value is zero. See glBindFramebuffer.
        log.debug("Read framebuffer binding: {}", glGetInteger(GL_READ_FRAMEBUFFER_BINDING));

        // data returns a single value, the name of the buffer object currently bound to the target GL_ELEMENT_ARRAY_BUFFER. If no buffer object is bound to this target, 0 is returned. The initial value is 0. See glBindBuffer.
        log.debug("Element array buffer binding: {}", glGetInteger(GL_ELEMENT_ARRAY_BUFFER_BINDING));

        // data returns a single value, the name of the buffer object currently bound to the target GL_PIXEL_PACK_BUFFER. If no buffer object is bound to this target, 0 is returned. The initial value is 0. See glBindBuffer.
        log.debug("Pixel pack buffer binding: {}", glGetInteger(GL_PIXEL_PACK_BUFFER_BINDING));

        // data returns a single value, the name of the buffer object currently bound to the target GL_PIXEL_UNPACK_BUFFER. If no buffer object is bound to this target, 0 is returned. The initial value is 0. See glBindBuffer.
        log.debug("Pixel unpack buffer bindign: {}", glGetInteger(GL_PIXEL_UNPACK_BUFFER_BINDING));

        // data returns a single value, the name of the renderbuffer object currently bound to the target GL_RENDERBUFFER. If no renderbuffer object is bound to this target, 0 is returned. The initial value is 0. See glBindRenderbuffer.
        log.debug("Render buffer binding: {}", glGetInteger(GL_RENDERBUFFER_BINDING));

        // When used with non-indexed variants of glGet (such as glGetIntegerv), data returns a single value, the name of the buffer object currently bound to the target GL_SHADER_STORAGE_BUFFER. If no buffer object is bound to this target, 0 is returned. When used with indexed variants of glGet (such as glGetIntegeri_v), data returns a single value, the name of the buffer object bound to the indexed shader storage buffer binding points. The initial value is 0 for all targets. See glBindBuffer, glBindBufferBase, and glBindBufferRange.
        log.debug("Shader storage buffer binding: {}", glGetInteger(GL_SHADER_STORAGE_BUFFER_BINDING));

        // When used with non-indexed variants of glGet (such as glGetIntegerv), data returns a single value, the name of the buffer object currently bound to the target GL_UNIFORM_BUFFER. If no buffer object is bound to this target, 0 is returned. When used with indexed variants of glGet (such as glGetIntegeri_v), data returns a single value, the name of the buffer object bound to the indexed uniform buffer binding point. The initial value is 0 for all targets. See glBindBuffer, glBindBufferBase, and glBindBufferRange.
        log.debug("Uniform buffer binding: {}", glGetInteger(GL_UNIFORM_BUFFER_BINDING));

        // data returns a single value, the name of the vertex array object currently bound to the context. If no vertex array object is bound to the context, 0 is returned. The initial value is 0. See glBindVertexArray.
        log.debug("Unpack vertex array binding: {}", glGetInteger(GL_VERTEX_ARRAY_BINDING));

        // When used with indexed variants of glGet (such as glGetInteger64i_v), data returns a single value, the size of the binding range for each indexed uniform buffer binding. The initial value is 0 for all bindings. See glBindBufferRange.
        log.debug("Uniform buffer size: {}", glGetInteger64i(GL_UNIFORM_BUFFER_SIZE, 0));

        // When used with indexed variants of glGet (such as glGetInteger64i_v), data returns a single value, the start offset of the binding range for each indexed uniform buffer binding. The initial value is 0 for all bindings. See glBindBufferRange.
        log.debug("Uniform buffer start: {}", glGetInteger64i(GL_UNIFORM_BUFFER_START, 0));

        // Accepted by the indexed forms. data returns a single integer value representing the instance step divisor of the first element in the bound buffer's data store for vertex attribute bound to index.
        log.debug("Vertex binding divisor: {}", glGetIntegeri(GL_VERTEX_BINDING_DIVISOR, 0));

        // Accepted by the indexed forms. data returns a single integer value representing the byte offset of the first element in the bound buffer's data store for vertex attribute bound to index.
        log.debug("Vertex binding offset: {}", glGetIntegeri(GL_VERTEX_BINDING_OFFSET, 0));

        // Accepted by the indexed forms. data returns a single integer value representing the byte offset between the start of each element in the bound buffer's data store for vertex attribute bound to index.
        log.debug("Vertex binding stride: {}", glGetIntegeri(GL_VERTEX_BINDING_STRIDE, 0));

        // Accepted by the indexed forms. data returns a single integer value representing the name of the buffer bound to vertex binding index.
        log.debug("Vertex binding buffer: {}", glGetIntegeri(GL_VERTEX_BINDING_BUFFER, 0));

    }

    public static void logSettings() {

        // data returns a single boolean value indicating whether blending is enabled. The initial value is GL_FALSE. See glBlendFunc.
        log.debug("Blending: {}", glGetInteger(GL_BLEND));

        // data returns four values, the red, green, blue, and alpha values which are the components of the blend color. See glBlendColor.
        log.debug("Blend color: {}", glGetVector4i(GL_BLEND_COLOR).toString(NUMBER_FORMAT_INT));

        // data returns one value, the symbolic constant identifying the alpha destination blend function. The initial value is GL_ZERO. See glBlendFunc and glBlendFuncSeparate.
        log.debug("Blend dst alpha: {}", glGetInteger(GL_BLEND_DST_ALPHA));

        // data returns one value, the symbolic constant identifying the RGB destination blend function. The initial value is GL_ZERO. See glBlendFunc and glBlendFuncSeparate.
        log.debug("Blend dst rgb: {}", glGetInteger(GL_BLEND_DST_RGB));

        // data returns one value, a symbolic constant indicating whether the RGB blend equation is GL_FUNC_ADD, GL_FUNC_SUBTRACT, GL_FUNC_REVERSE_SUBTRACT, GL_MIN or GL_MAX. See glBlendEquationSeparate.
        log.debug("Blend equation rgb: {}", glGetInteger(GL_BLEND_EQUATION_RGB));

        // data returns one value, a symbolic constant indicating whether the Alpha blend equation is GL_FUNC_ADD, GL_FUNC_SUBTRACT, GL_FUNC_REVERSE_SUBTRACT, GL_MIN or GL_MAX. See glBlendEquationSeparate.
        log.debug("Blend equation alpha: {}", glGetInteger(GL_BLEND_EQUATION_ALPHA));

        // data returns one value, the symbolic constant identifying the alpha source blend function. The initial value is GL_ONE. See glBlendFunc and glBlendFuncSeparate.
        log.debug("Blend src alpha: {}", glGetInteger(GL_BLEND_SRC_ALPHA));

        // data returns one value, the symbolic constant identifying the RGB source blend function. The initial value is GL_ONE. See glBlendFunc and glBlendFuncSeparate.
        log.debug("Blend src rgb: {}", glGetInteger(GL_BLEND_SRC_RGB));

        // data returns four values: the red, green, blue, and alpha values used to clear the color buffers. Integer values, if requested, are linearly mapped from the internal floating-point representation such that 1.0 returns the most positive representable integer value, and −1.0 returns the most negative representable integer value. The initial value is (0, 0, 0, 0). See glClearColor.
        log.debug("Color clear value: {}", glGetVector4i(GL_COLOR_CLEAR_VALUE).toString(NUMBER_FORMAT_INT));

        // data returns a single boolean value indicating whether a fragment's RGBA color values are merged into the framebuffer using a logical operation. The initial value is GL_FALSE. See glLogicOp.
        log.debug("Color logic op: {}", glGetInteger(GL_COLOR_LOGIC_OP));

        // data returns four boolean values: the red, green, blue, and alpha write enables for the color buffers. The initial value is (GL_TRUE, GL_TRUE, GL_TRUE, GL_TRUE). See glColorMask.
        log.debug("Color write mask: {}", glGetVector4i(GL_COLOR_WRITEMASK).toString(NUMBER_FORMAT_INT));

        // data returns a single boolean value indicating whether polygon culling is enabled. The initial value is GL_FALSE. See glCullFace.
        log.debug("Cull face: {}", glGetInteger(GL_CULL_FACE));

        // data returns a single value indicating the mode of polygon culling. The initial value is GL_BACK. See glCullFace.
        log.debug("Cull face mode: {}", glGetInteger(GL_CULL_FACE_MODE));

        // data returns one value, the value that is used to clear the depth buffer. Integer values, if requested, are linearly mapped from the internal floating-point representation such that 1.0 returns the most positive representable integer value, and −1.0 returns the most negative representable integer value. The initial value is 1. See glClearDepth.
        log.debug("Depth clear value: {}", glGetInteger(GL_DEPTH_CLEAR_VALUE));

        // data returns one value, the symbolic constant that indicates the depth comparison function. The initial value is GL_LESS. See glDepthFunc.
        log.debug("Depth func: {}", glGetInteger(GL_DEPTH_FUNC));

        // data returns two values: the near and far mapping limits for the depth buffer. Integer values, if requested, are linearly mapped from the internal floating-point representation such that 1.0 returns the most positive representable integer value, and −1.0 returns the most negative representable integer value. The initial value is (0, 1). See glDepthRange.
        log.debug("Depth range: {}", glGetVector2i(GL_DEPTH_RANGE).toString(NUMBER_FORMAT_INT));

        // data returns a single boolean value indicating whether depth testing of fragments is enabled. The initial value is GL_FALSE. See glDepthFunc and glDepthRange.
        log.debug("Depth test: {}", glGetInteger(GL_DEPTH_TEST));

        // data returns a single boolean value indicating if the depth buffer is enabled for writing. The initial value is GL_TRUE. See glDepthMask.
        log.debug("Depth write mask: {}", glGetInteger(GL_DEPTH_WRITEMASK));

        // data returns a single boolean value indicating whether dithering of fragment colors and indices is enabled. The initial value is GL_TRUE.
        log.debug("Dither: {}", glGetInteger(GL_DITHER));

        // data returns one value, a symbolic constant indicating the mode of the derivative accuracy hint for fragment shaders. The initial value is GL_DONT_CARE. See glHint.
        log.debug("Fragment shader derivative hint: {}", glGetInteger(GL_FRAGMENT_SHADER_DERIVATIVE_HINT));

        // data returns a single boolean value indicating whether antialiasing of lines is enabled. The initial value is GL_FALSE. See glLineWidth.
        log.debug("Line smooth: {}", glGetInteger(GL_LINE_SMOOTH));

        // data returns one value, a symbolic constant indicating the mode of the line antialiasing hint. The initial value is GL_DONT_CARE. See glHint.
        log.debug("Line smooth hint: {}", glGetInteger(GL_LINE_SMOOTH_HINT));

        // data returns one value, the line width as specified with glLineWidth. The initial value is 1.
        log.debug("Line width: {}", glGetInteger(GL_LINE_WIDTH));

        // data returns one value, a symbolic constant indicating the selected logic operation mode. The initial value is GL_COPY. See glLogicOp.
        log.debug("Logic op mode: {}", glGetInteger(GL_LOGIC_OP_MODE));

        // data returns one value, the byte alignment used for writing pixel data to memory. The initial value is 4. See glPixelStore.
        log.debug("Pack alignment: {}", glGetInteger(GL_PACK_ALIGNMENT));

        // data returns one value, the image height used for writing pixel data to memory. The initial value is 0. See glPixelStore.
        log.debug("Pack image height: {}", glGetInteger(GL_PACK_IMAGE_HEIGHT));

        // data returns a single boolean value indicating whether single-bit pixels being written to memory are written first to the least significant bit of each unsigned byte. The initial value is GL_FALSE. See glPixelStore.
        log.debug("Pack lsb first: {}", glGetInteger(GL_PACK_LSB_FIRST));

        // data returns one value, the row length used for writing pixel data to memory. The initial value is 0. See glPixelStore.
        log.debug("Pack row length: {}", glGetInteger(GL_PACK_ROW_LENGTH));

        // data returns one value, the number of pixel images skipped before the first pixel is written into memory. The initial value is 0. See glPixelStore.
        log.debug("Pack skip images: {}", glGetInteger(GL_PACK_SKIP_IMAGES));

        // data returns one value, the number of pixel locations skipped before the first pixel is written into memory. The initial value is 0. See glPixelStore.
        log.debug("Pack skip pixels: {}", glGetInteger(GL_PACK_SKIP_PIXELS));

        // data returns one value, the number of rows of pixel locations skipped before the first pixel is written into memory. The initial value is 0. See glPixelStore.
        log.debug("Pack skip rows: {}", glGetInteger(GL_PACK_SKIP_ROWS));

        // data returns a single boolean value indicating whether the bytes of two-byte and four-byte pixel indices and components are swapped before being written to memory. The initial value is GL_FALSE. See glPixelStore.
        log.debug("Pack swap bytes: {}", glGetInteger(GL_PACK_SWAP_BYTES));

        // data returns one value, the current primitive restart index. The initial value is 0. See glPrimitiveRestartIndex.
        log.debug("Primitive restart index: {}", glGetInteger(GL_PRIMITIVE_RESTART_INDEX));

        // data returns one value, the currently selected provoking vertex convention. The initial value is GL_LAST_VERTEX_CONVENTION. See glProvokingVertex.
        log.debug("Provoking vertex: {}", glGetInteger(GL_PROVOKING_VERTEX));

        // data returns one value, the point size as specified by glPointSize. The initial value is 1.
        log.debug("Point size: {}", glGetInteger(GL_POINT_SIZE));

        // data returns a single boolean value indicating whether vertex program point size mode is enabled. If enabled, then the point size is taken from the shader built-in gl_PointSize. If disabled, then the point size is taken from the point state as specified by glPointSize. The initial value is GL_FALSE.
        log.debug("Program point size: {}", glGetInteger(GL_PROGRAM_POINT_SIZE));

        // data returns one value, the scaling factor used to determine the variable offset that is added to the depth value of each fragment generated when a polygon is rasterized. The initial value is 0. See glPolygonOffset.
        log.debug("Polygon offset factor: {}", glGetInteger(GL_POLYGON_OFFSET_FACTOR));

        // data returns one value. This value is multiplied by an implementation-specific value and then added to the depth value of each fragment generated when a polygon is rasterized. The initial value is 0. See glPolygonOffset.
        log.debug("Polygon offset units: {}", glGetInteger(GL_POLYGON_OFFSET_UNITS));

        // data returns a single boolean value indicating whether polygon offset is enabled for polygons in fill mode. The initial value is GL_FALSE. See glPolygonOffset.
        log.debug("Polygon offset fill: {}", glGetInteger(GL_POLYGON_OFFSET_FILL));

        // data returns a single boolean value indicating whether polygon offset is enabled for polygons in line mode. The initial value is GL_FALSE. See glPolygonOffset.
        log.debug("Polygon offset line: {}", glGetInteger(GL_POLYGON_OFFSET_LINE));

        // data returns a single boolean value indicating whether polygon offset is enabled for polygons in point mode. The initial value is GL_FALSE. See glPolygonOffset.
        log.debug("Polygon offset point: {}", glGetInteger(GL_POLYGON_OFFSET_POINT));

        // data returns a single boolean value indicating whether antialiasing of polygons is enabled. The initial value is GL_FALSE. See glPolygonMode.
        log.debug("Polygon smooth: {}", glGetInteger(GL_POLYGON_SMOOTH));

        // data returns one value, a symbolic constant indicating the mode of the polygon antialiasing hint. The initial value is GL_DONT_CARE. See glHint.
        log.debug("Polygon smooth hint: {}", glGetInteger(GL_POLYGON_SMOOTH_HINT));

        // data returns one value, a symbolic constant indicating which color buffer is selected for reading. The initial value is GL_BACK if there is a back buffer, otherwise it is GL_FRONT. See glReadPixels.
        log.debug("Read buffer: {}", glGetInteger(GL_READ_BUFFER));

        // data returns a single value, the name of the sampler object currently bound to the active texture unit. The initial value is 0. See glBindSampler.
        log.debug("Sampler binding: {}", glGetInteger(GL_SAMPLER_BINDING));

        // data returns four values: the x and y window coordinates of the scissor box, followed by its width and height. Initially the x and y window coordinates are both 0 and the width and height are set to the size of the window. See glScissor.
        log.debug("Scissor box: {}", glGetVector4i(GL_SCISSOR_BOX).toString(NUMBER_FORMAT_INT));

        // data returns a single boolean value indicating whether scissoring is enabled. The initial value is GL_FALSE. See glScissor.
        log.debug("Scissor test: {}", glGetInteger(GL_SCISSOR_TEST));

        // data returns a single value, the minimum required alignment for shader storage buffer sizes and offset. The initial value is 1. See glShaderStorageBlockBinding.
        log.debug("Shader storage buffer offset alignment: {}", glGetInteger(GL_SHADER_STORAGE_BUFFER_OFFSET_ALIGNMENT));

        // data returns one value, a symbolic constant indicating what action is taken for back-facing polygons when the stencil test fails. The initial value is GL_KEEP. See glStencilOpSeparate.
        log.debug("Stencil back fail: {}", glGetInteger(GL_STENCIL_BACK_FAIL));

        // data returns one value, a symbolic constant indicating what function is used for back-facing polygons to compare the stencil reference value with the stencil buffer value. The initial value is GL_ALWAYS. See glStencilFuncSeparate.
        log.debug("Stencil back func: {}", glGetInteger(GL_STENCIL_BACK_FUNC));

        // data returns one value, a symbolic constant indicating what action is taken for back-facing polygons when the stencil test passes, but the depth test fails. The initial value is GL_KEEP. See glStencilOpSeparate.
        log.debug("Stencil back pass depth fail: {}", glGetInteger(GL_STENCIL_BACK_PASS_DEPTH_FAIL));

        // data returns one value, a symbolic constant indicating what action is taken for back-facing polygons when the stencil test passes and the depth test passes. The initial value is GL_KEEP. See glStencilOpSeparate.
        log.debug("Stencil back pass depth pass: {}", glGetInteger(GL_STENCIL_BACK_PASS_DEPTH_PASS));

        // data returns one value, the reference value that is compared with the contents of the stencil buffer for back-facing polygons. The initial value is 0. See glStencilFuncSeparate.
        log.debug("Stencil back ref: {}", glGetInteger(GL_STENCIL_BACK_REF));

        // data returns one value, the mask that is used for back-facing polygons to mask both the stencil reference value and the stencil buffer value before they are compared. The initial value is all 1's. See glStencilFuncSeparate.
        log.debug("Stencil back value mask: {}", glGetInteger(GL_STENCIL_BACK_VALUE_MASK));

        // data returns one value, the mask that controls writing of the stencil bitplanes for back-facing polygons. The initial value is all 1's. See glStencilMaskSeparate.
        log.debug("Stencil back write mask: {}", glGetInteger(GL_STENCIL_BACK_WRITEMASK));

        // data returns one value, the index to which the stencil bitplanes are cleared. The initial value is 0. See glClearStencil.
        log.debug("Stencil clear value: {}", glGetInteger(GL_STENCIL_CLEAR_VALUE));

        // data returns one value, a symbolic constant indicating what action is taken when the stencil test fails. The initial value is GL_KEEP. See glStencilOp. This stencil state only affects non-polygons and front-facing polygons. Back-facing polygons use separate stencil state. See glStencilOpSeparate.
        log.debug("Stencil fail: {}", glGetInteger(GL_STENCIL_FAIL));

        // data returns one value, a symbolic constant indicating what function is used to compare the stencil reference value with the stencil buffer value. The initial value is GL_ALWAYS. See glStencilFunc. This stencil state only affects non-polygons and front-facing polygons. Back-facing polygons use separate stencil state. See glStencilFuncSeparate.
        log.debug("Stencil func: {}", glGetInteger(GL_STENCIL_FUNC));

        // data returns one value, a symbolic constant indicating what action is taken when the stencil test passes, but the depth test fails. The initial value is GL_KEEP. See glStencilOp. This stencil state only affects non-polygons and front-facing polygons. Back-facing polygons use separate stencil state. See glStencilOpSeparate.
        log.debug("Stencil pass depth fail: {}", glGetInteger(GL_STENCIL_PASS_DEPTH_FAIL));

        // data returns one value, a symbolic constant indicating what action is taken when the stencil test passes and the depth test passes. The initial value is GL_KEEP. See glStencilOp. This stencil state only affects non-polygons and front-facing polygons. Back-facing polygons use separate stencil state. See glStencilOpSeparate.
        log.debug("Stencil pass depth pass: {}", glGetInteger(GL_STENCIL_PASS_DEPTH_PASS));

        // data returns one value, the reference value that is compared with the contents of the stencil buffer. The initial value is 0. See glStencilFunc. This stencil state only affects non-polygons and front-facing polygons. Back-facing polygons use separate stencil state. See glStencilFuncSeparate.
        log.debug("Stencil ref: {}", glGetInteger(GL_STENCIL_REF));

        // data returns a single boolean value indicating whether stencil testing of fragments is enabled. The initial value is GL_FALSE. See glStencilFunc and glStencilOp.
        log.debug("Stencil test: {}", glGetInteger(GL_STENCIL_TEST));

        // data returns one value, the mask that is used to mask both the stencil reference value and the stencil buffer value before they are compared. The initial value is all 1's. See glStencilFunc. This stencil state only affects non-polygons and front-facing polygons. Back-facing polygons use separate stencil state. See glStencilFuncSeparate.
        log.debug("Stencil value mask: {}", glGetInteger(GL_STENCIL_VALUE_MASK));

        // data returns one value, the mask that controls writing of the stencil bitplanes. The initial value is all 1's. See glStencilMask. This stencil state only affects non-polygons and front-facing polygons. Back-facing polygons use separate stencil state. See glStencilMaskSeparate.
        log.debug("Stencil write mask: {}", glGetInteger(GL_STENCIL_WRITEMASK));

        // data returns one value, the byte alignment used for reading pixel data from memory. The initial value is 4. See glPixelStore.
        log.debug("Unpack alignment: {}", glGetInteger(GL_UNPACK_ALIGNMENT));

        // data returns one value, the image height used for reading pixel data from memory. The initial is 0. See glPixelStore.
        log.debug("Unpack image height: {}", glGetInteger(GL_UNPACK_IMAGE_HEIGHT));

        // data returns a single boolean value indicating whether single-bit pixels being read from memory are read first from the least significant bit of each unsigned byte. The initial value is GL_FALSE. See glPixelStore.
        log.debug("Unpack LSB first: {}", glGetInteger(GL_UNPACK_LSB_FIRST));

        // data returns one value, the row length used for reading pixel data from memory. The initial value is 0. See glPixelStore.
        log.debug("Unpack row length: {}", glGetInteger(GL_UNPACK_ROW_LENGTH));

        // data returns one value, the number of pixel images skipped before the first pixel is read from memory. The initial value is 0. See glPixelStore.
        log.debug("Unpack skip images: {}", glGetInteger(GL_UNPACK_SKIP_IMAGES));

        // data returns one value, the number of pixel locations skipped before the first pixel is read from memory. The initial value is 0. See glPixelStore.
        log.debug("Unpack skip pixels: {}", glGetInteger(GL_UNPACK_SKIP_PIXELS));

        // data returns one value, the number of rows of pixel locations skipped before the first pixel is read from memory. The initial value is 0. See glPixelStore.
        log.debug("Unpack skip rows: {}", glGetInteger(GL_UNPACK_SKIP_ROWS));

        // data returns a single boolean value indicating whether the bytes of two-byte and four-byte pixel indices and components are swapped after being read from memory. The initial value is GL_FALSE. See glPixelStore.
        log.debug("Unpack swap bytes: {}", glGetInteger(GL_UNPACK_SWAP_BYTES));

        // data returns a single value, the minimum required alignment for uniform buffer sizes and offset. The initial value is 1. See glUniformBlockBinding.
        log.debug("Uniform buffer offset alignment: {}", glGetInteger(GL_UNIFORM_BUFFER_OFFSET_ALIGNMENT));

    }

    public static void logComputeLimits() {

        // Accepted by the indexed versions of glGet. data the maximum number of work groups that may be dispatched to a compute shader. Indices 0, 1, and 2 correspond to the X, Y and Z dimensions, respectively.
        log.debug("Max compute work group count: {}", glGetVector3iIndexed(GL_MAX_COMPUTE_WORK_GROUP_COUNT));

        // Accepted by the indexed versions of glGet. data the maximum size of a work groups that may be used during compilation of a compute shader. Indices 0, 1, and 2 correspond to the X, Y and Z dimensions, respectively.
        log.debug("Max compute work group size: {}", glGetVector3iIndexed(GL_MAX_COMPUTE_WORK_GROUP_SIZE));
    }

    public static void logShaderInfo() {
        // data returns one value, the name of the program object that is currently active, or 0 if no program object is active. See glUseProgram.
        log.debug("Current program: {}", glGetInteger(GL_CURRENT_PROGRAM));
    }

    public static void logInfo() {

        // TODO finish this - Если раскоментировать то некоторые комманды могут привести к случайным крашам, нужно аккуратно раскоменчивать и проверять
        // TODO многие логгеры нужно вынести на уровень только там где это доступно, например работа с атрибутами или буферами или фидбеком итп

        // https://www.khronos.org/registry/OpenGL-Refpages/gl4/html/glGet.xhtml

        // data returns a pair of values indicating the range of widths supported for aliased lines. See glLineWidth.
        log.debug("Aliased line width range: {}", glGetVector2i(GL_ALIASED_LINE_WIDTH_RANGE).toString(NUMBER_FORMAT_INT));

        // data returns a single integer value indicating the number of available compressed texture formats. The minimum value is 4. See glCompressedTexImage2D.
        log.debug("Number of compressed texture formats: {}", glGetInteger(GL_NUM_COMPRESSED_TEXTURE_FORMATS));

        // data returns a list of symbolic constants of length GL_NUM_COMPRESSED_TEXTURE_FORMATS indicating which compressed texture formats are available. See glCompressedTexImage2D.
        log.debug("Compressed texture formats: {}", glGetArray(GL_COMPRESSED_TEXTURE_FORMATS, glGetInteger(GL_NUM_COMPRESSED_TEXTURE_FORMATS)));

        // data returns one value, the maximum number of active shader storage blocks that may be accessed by a compute shader.
        log.debug("Max compute shader storage blocks: {}", glGetInteger(GL_MAX_COMPUTE_SHADER_STORAGE_BLOCKS));

        // data returns one value, the maximum total number of active shader storage blocks that may be accessed by all active shaders.
        log.debug("Max combined shader storage blocks: {}", glGetInteger(GL_MAX_COMBINED_SHADER_STORAGE_BLOCKS));

        // data returns one value, the maximum number of uniform blocks per compute shader. The value must be at least 14. See glUniformBlockBinding.
        log.debug("Max compute uniform blocks: {}", glGetInteger(GL_MAX_COMPUTE_UNIFORM_BLOCKS));

        // data returns one value, the maximum supported texture image units that can be used to access texture maps from the compute shader. The value may be at least 16. See glActiveTexture.
        log.debug("Max compute texture image units: {}", glGetInteger(GL_MAX_COMPUTE_TEXTURE_IMAGE_UNITS));

        // data returns one value, the maximum number of individual floating-point, integer, or boolean values that can be held in uniform variable storage for a compute shader. The value must be at least 1024. See glUniform.
        log.debug("Max compute uniform components: {}", glGetInteger(GL_MAX_COMPUTE_UNIFORM_COMPONENTS));

        // data returns a single value, the maximum number of atomic counters available to compute shaders.
        log.debug("Max compute atomic counters: {}", glGetInteger(GL_MAX_COMPUTE_ATOMIC_COUNTERS));

        // data returns a single value, the maximum number of atomic counter buffers that may be accessed by a compute shader.
        log.debug("Max compute atomic counter buffers: {}", glGetInteger(GL_MAX_COMPUTE_ATOMIC_COUNTER_BUFFERS));

        // data returns one value, the number of words for compute shader uniform variables in all uniform blocks (including default). The value must be at least 1. See glUniform.
        log.debug("Max combined compute uniform components: {}", glGetInteger(GL_MAX_COMBINED_COMPUTE_UNIFORM_COMPONENTS));

        // data returns one value, the number of invocations in a single local work group (i.e., the product of the three dimensions) that may be dispatched to a compute shader.
        log.debug("Max compute work group invocations: {}", glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS));

        // data returns a single value, the maximum depth of the debug message group stack.
        log.debug("Max debug group stack depth: {}", glGetInteger(GL_MAX_DEBUG_GROUP_STACK_DEPTH));

        // data returns a single value, the current depth of the debug message group stack.
        log.debug("Debug group stack depth: {}", glGetInteger(GL_DEBUG_GROUP_STACK_DEPTH));

        // data returns one value, the flags with which the context was created (such as debugging functionality).
        log.debug("Context flags: {}", glGetInteger(GL_CONTEXT_FLAGS));

        // data returns a single boolean value indicating whether double buffering is supported.
        log.debug("Double buffer: {}", glGetInteger(GL_DOUBLEBUFFER));

        // data returns a single GLenum value indicating the implementation's preferred pixel data format. See glReadPixels.
        log.debug("Implementation color read format: {}", glGetInteger(GL_IMPLEMENTATION_COLOR_READ_FORMAT));

        // data returns a single GLenum value indicating the implementation's preferred pixel data type. See glReadPixels.
        log.debug("Implementation color read type: {}", glGetInteger(GL_IMPLEMENTATION_COLOR_READ_TYPE));

        // data returns one value, the implementation dependent specifc vertex of a primitive that is used to select the rendering layer. If the value returned is equivalent to GL_PROVOKING_VERTEX, then the vertex selection follows the convention specified by glProvokingVertex. If the value returned is equivalent to GL_FIRST_VERTEX_CONVENTION, then the selection is always taken from the first vertex in the primitive. If the value returned is equivalent to GL_LAST_VERTEX_CONVENTION, then the selection is always taken from the last vertex in the primitive. If the value returned is equivalent to GL_UNDEFINED_VERTEX, then the selection is not guaranteed to be taken from any specific vertex in the primitive.
        log.debug("Layer provoking vertex: {}", glGetInteger(GL_LAYER_PROVOKING_VERTEX));

        // data returns one value, the major version number of the OpenGL API supported by the current context.
        log.debug("Major version: {}", glGetInteger(GL_MAJOR_VERSION));

        // data returns one value, a rough estimate of the largest 3D texture that the GL can handle. The value must be at least 64. Use GL_PROXY_TEXTURE_3D to determine if a texture is too large. See glTexImage3D.
        log.debug("Max 3d texture (W/H/D) size: {}", glGetInteger(GL_MAX_3D_TEXTURE_SIZE));

        // data returns one value. The value indicates the maximum number of layers allowed in an array texture, and must be at least 256. See glTexImage2D.
        log.debug("Max array texture layers: {}", glGetInteger(GL_MAX_ARRAY_TEXTURE_LAYERS));

        // data returns one value, the maximum number of application-defined clipping distances. The value must be at least 8.
        log.debug("Max clip distances: {}", glGetInteger(GL_MAX_CLIP_DISTANCES));

        // data returns one value, the maximum number of samples in a color multisample texture.
        log.debug("Max color texture samples: {}", glGetInteger(GL_MAX_COLOR_TEXTURE_SAMPLES));

        // data returns a single value, the maximum number of atomic counters available to all active shaders.
        log.debug("Max combined atomic counters: {}", glGetInteger(GL_MAX_COMBINED_ATOMIC_COUNTERS));

        // data returns one value, the number of words for fragment shader uniform variables in all uniform blocks (including default). The value must be at least 1. See glUniform.
        log.debug("Max combined fragment uniform components: {}", glGetInteger(GL_MAX_COMBINED_FRAGMENT_UNIFORM_COMPONENTS));

        // data returns one value, the number of words for geometry shader uniform variables in all uniform blocks (including default). The value must be at least 1. See glUniform.
        log.debug("Max combined geometry uniform components: {}", glGetInteger(GL_MAX_COMBINED_GEOMETRY_UNIFORM_COMPONENTS));

        // data returns one value, the maximum supported texture image units that can be used to access texture maps from the vertex shader and the fragment processor combined. If both the vertex shader and the fragment processing stage access the same texture image unit, then that counts as using two texture image units against this limit. The value must be at least 48. See glActiveTexture.
        log.debug("Max max combined texture image units: {}", glGetInteger(GL_MAX_COMBINED_TEXTURE_IMAGE_UNITS));

        // data returns one value, the maximum number of uniform blocks per program. The value must be at least 70. See glUniformBlockBinding.
        log.debug("Max combined uniform blocks: {}", glGetInteger(GL_MAX_COMBINED_UNIFORM_BLOCKS));

        // data returns one value, the number of words for vertex shader uniform variables in all uniform blocks (including default). The value must be at least 1. See glUniform.
        log.debug("Max combined vertex uniform components: {}", glGetInteger(GL_MAX_COMBINED_VERTEX_UNIFORM_COMPONENTS));

        // data returns one value. The value gives a rough estimate of the largest cube-map texture that the GL can handle. The value must be at least 1024. Use GL_PROXY_TEXTURE_CUBE_MAP to determine if a texture is too large. See glTexImage2D.
        log.debug("Max cube map texture (W/H) size: {}", glGetInteger(GL_MAX_CUBE_MAP_TEXTURE_SIZE));

        // data returns one value, the maximum number of samples in a multisample depth or depth-stencil texture.
        log.debug("Max depth texture samples: {}", glGetInteger(GL_MAX_DEPTH_TEXTURE_SAMPLES));

        // data returns one value, the maximum number of simultaneous outputs that may be written in a fragment shader. The value must be at least 8. See glDrawBuffers.
        log.debug("Max draw buffers: {}", glGetInteger(GL_MAX_DRAW_BUFFERS));

        // data returns one value, the maximum number of active draw buffers when using dual-source blending. The value must be at least 1. See glBlendFunc and glBlendFuncSeparate.
        log.debug("Max dual source draw buffers: {}", glGetInteger(GL_MAX_DUAL_SOURCE_DRAW_BUFFERS));

        // data returns one value, the recommended maximum number of vertex array indices. See glDrawRangeElements.
        log.debug("Max elements indices: {}", glGetInteger(GL_MAX_ELEMENTS_INDICES));

        // data returns one value, the recommended maximum number of vertex array vertices. See glDrawRangeElements.
        log.debug("Max elements vertices: {}", glGetInteger(GL_MAX_ELEMENTS_VERTICES));

        // data returns a single value, the maximum number of atomic counters available to fragment shaders.
        log.debug("Max fragment atomic counters: {}", glGetInteger(GL_MAX_FRAGMENT_ATOMIC_COUNTERS));

        // data returns one value, the maximum number of active shader storage blocks that may be accessed by a fragment shader.
        log.debug("Max fragment shader storage blocks: {}", glGetInteger(GL_MAX_FRAGMENT_SHADER_STORAGE_BLOCKS));

        // data returns one value, the maximum number of components of the inputs read by the fragment shader, which must be at least 128.
        log.debug("Max fragment input components: {}", glGetInteger(GL_MAX_FRAGMENT_INPUT_COMPONENTS));

        // data returns one value, the maximum number of individual floating-point, integer, or boolean values that can be held in uniform variable storage for a fragment shader. The value must be at least 1024. See glUniform.
        log.debug("Max fragment uniform components: {}", glGetInteger(GL_MAX_FRAGMENT_UNIFORM_COMPONENTS));

        // data returns one value, the maximum number of individual 4-vectors of floating-point, integer, or boolean values that can be held in uniform variable storage for a fragment shader. The value is equal to the value of GL_MAX_FRAGMENT_UNIFORM_COMPONENTS divided by 4 and must be at least 256. See glUniform.
        log.debug("Max fragment uniform vectors: {}", glGetInteger(GL_MAX_FRAGMENT_UNIFORM_VECTORS));

        // data returns one value, the maximum number of uniform blocks per fragment shader. The value must be at least 12. See glUniformBlockBinding.
        log.debug("Max fragment uniform blocks : {}", glGetInteger(GL_MAX_FRAGMENT_UNIFORM_BLOCKS));

        // data returns one value, the maximum width for a framebuffer that has no attachments, which must be at least 16384. See glFramebufferParameter.
        log.debug("Max framebuffer width: {}", glGetInteger(GL_MAX_FRAMEBUFFER_WIDTH));

        // data returns one value, the maximum height for a framebuffer that has no attachments, which must be at least 16384. See glFramebufferParameter.
        log.debug("Max framebuffer height: {}", glGetInteger(GL_MAX_FRAMEBUFFER_HEIGHT));

        // data returns one value, the maximum number of layers for a framebuffer that has no attachments, which must be at least 2048. See glFramebufferParameter.
        log.debug("Max framebuffer layers: {}", glGetInteger(GL_MAX_FRAMEBUFFER_LAYERS));

        // data returns one value, the maximum samples in a framebuffer that has no attachments, which must be at least 4. See glFramebufferParameter.
        log.debug("Max framebuffer samples: {}", glGetInteger(GL_MAX_FRAMEBUFFER_SAMPLES));

        // data returns a single value, the maximum number of atomic counters available to geometry shaders.
        log.debug("Max geometry atomic: {}", glGetInteger(GL_MAX_GEOMETRY_ATOMIC_COUNTERS));

        // data returns one value, the maximum number of active shader storage blocks that may be accessed by a geometry shader.
        log.debug("Max geometry shader storage blocks: {}", glGetInteger(GL_MAX_GEOMETRY_SHADER_STORAGE_BLOCKS));

        // data returns one value, the maximum number of components of inputs read by a geometry shader, which must be at least 64.
        log.debug("Max geometry input components: {}", glGetInteger(GL_MAX_GEOMETRY_INPUT_COMPONENTS));

        // data returns one value, the maximum number of components of outputs written by a geometry shader, which must be at least 128.
        log.debug("Max geometry output components: {}", glGetInteger(GL_MAX_GEOMETRY_OUTPUT_COMPONENTS));

        // data returns one value, the maximum supported texture image units that can be used to access texture maps from the geometry shader. The value must be at least 16. See glActiveTexture.
        log.debug("Max geometry texture image units: {}", glGetInteger(GL_MAX_GEOMETRY_TEXTURE_IMAGE_UNITS));

        // data returns one value, the maximum number of uniform blocks per geometry shader. The value must be at least 12. See glUniformBlockBinding.
        log.debug("Max geometry uniform blocks: {}", glGetInteger(GL_MAX_GEOMETRY_UNIFORM_BLOCKS));

        // data returns one value, the maximum number of individual floating-point, integer, or boolean values that can be held in uniform variable storage for a geometry shader. The value must be at least 1024. See glUniform.
        log.debug("Max geometry uniform components: {}", glGetInteger(GL_MAX_GEOMETRY_UNIFORM_COMPONENTS));

        // data returns one value, the maximum number of samples supported in integer format multisample buffers.
        log.debug("Max integer samples: {}", glGetInteger(GL_MAX_INTEGER_SAMPLES));

        // data returns one value, the minimum alignment in basic machine units of pointers returned fromglMapBuffer and glMapBufferRange. This value must be a power of two and must be at least 64.
        log.debug("Min map buffer alignment: {}", glGetInteger(GL_MIN_MAP_BUFFER_ALIGNMENT));

        // data returns one value, the maximum length of a label that may be assigned to an object. See glObjectLabel and glObjectPtrLabel.
        log.debug("Max label length: {}", glGetInteger(GL_MAX_LABEL_LENGTH));

        // data returns one value, the maximum texel offset allowed in a texture lookup, which must be at least 7.
        log.debug("Max program texel offset: {}", glGetInteger(GL_MAX_PROGRAM_TEXEL_OFFSET));

        // data returns one value, the minimum texel offset allowed in a texture lookup, which must be at most -8.
        log.debug("Min program texel offset: {}", glGetInteger(GL_MIN_PROGRAM_TEXEL_OFFSET));

        // data returns one value. The value gives a rough estimate of the largest rectangular texture that the GL can handle. The value must be at least 1024. Use GL_PROXY_TEXTURE_RECTANGLE to determine if a texture is too large. See glTexImage2D.
        log.debug("Max rectangle texture size: {}", glGetInteger(GL_MAX_RECTANGLE_TEXTURE_SIZE));

        // data returns one value. The value indicates the maximum supported size for renderbuffers. See glFramebufferRenderbuffer.
        log.debug("Max render buffer size: {}", glGetInteger(GL_MAX_RENDERBUFFER_SIZE));

        // data returns one value, the maximum number of sample mask words.
        log.debug("Max samples mask words: {}", glGetInteger(GL_MAX_SAMPLE_MASK_WORDS));

        // data returns one value, the maximum glWaitSync timeout interval.
        log.debug("Max server wait timeout: {}", glGetInteger(GL_MAX_SERVER_WAIT_TIMEOUT));

        // data returns one value, the maximum number of shader storage buffer binding points on the context, which must be at least 8.
        log.debug("Max shader storage buffer bindings: {}", glGetInteger(GL_MAX_SHADER_STORAGE_BUFFER_BINDINGS));

        // data returns a single value, the maximum number of atomic counters available to tessellation control shaders.
        log.debug("Max tess control atomic counters: {}", glGetInteger(GL_MAX_TESS_CONTROL_ATOMIC_COUNTERS));

        // data returns a single value, the maximum number of atomic counters available to tessellation evaluation shaders.
        log.debug("Max tess evaluation atomic counters: {}", glGetInteger(GL_MAX_TESS_EVALUATION_ATOMIC_COUNTERS));

        // data returns one value, the maximum number of active shader storage blocks that may be accessed by a tessellation control shader.
        log.debug("Max tess control shader storage blocks: {}", glGetInteger(GL_MAX_TESS_CONTROL_SHADER_STORAGE_BLOCKS));

        // data returns one value, the maximum number of active shader storage blocks that may be accessed by a tessellation evaluation shader.
        log.debug("Max tess evaluation shader storage blocks: {}", glGetInteger(GL_MAX_TESS_EVALUATION_SHADER_STORAGE_BLOCKS));

        // data returns one value. The value gives the maximum number of texels allowed in the texel array of a texture buffer object. Value must be at least 65536.
        log.debug("Max texture buffer size: {}", glGetInteger(GL_MAX_TEXTURE_BUFFER_SIZE));

        // data returns one value, the maximum supported texture image units that can be used to access texture maps from the fragment shader. The value must be at least 16. See glActiveTexture.
        log.debug("Max texture image units: {}", glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS));

        // data returns one value, the maximum, absolute value of the texture level-of-detail bias. The value must be at least 2.0.
        log.debug("Max texture LOD bias: {}", glGetInteger(GL_MAX_TEXTURE_LOD_BIAS));

        // data returns one value. The value gives a rough estimate of the largest texture that the GL can handle. The value must be at least 1024. Use a proxy texture target such as GL_PROXY_TEXTURE_1D or GL_PROXY_TEXTURE_2D to determine if a texture is too large. See glTexImage1D and glTexImage2D.
        log.debug("Max texture size: {}", glGetInteger(GL_MAX_TEXTURE_SIZE));

        // data returns one value. Maximal texture coordinates (not from documentation). See glActiveTexture
        log.debug("Max max texture coordinates: {}", glGetInteger(GL_MAX_TEXTURE_COORDS));

        // data returns one value, the maximum number of uniform buffer binding points on the context, which must be at least 36.
        log.debug("Max uniform buffer bindings: {}", glGetInteger(GL_MAX_UNIFORM_BUFFER_BINDINGS));

        // data returns one value, the maximum size in basic machine units of a uniform block, which must be at least 16384.
        log.debug("Max uniform block size: {}", glGetInteger(GL_MAX_UNIFORM_BLOCK_SIZE));

        // data returns one value, the maximum number of explicitly assignable uniform locations, which must be at least 1024.
        log.debug("Max uniform locations: {}", glGetInteger(GL_MAX_UNIFORM_LOCATIONS));

        // data returns one value, the number components for varying variables, which must be at least 60.
        log.debug("Max varying components: {}", glGetInteger(GL_MAX_VARYING_COMPONENTS));

        // data returns one value, the number 4-vectors for varying variables, which is equal to the value of GL_MAX_VARYING_COMPONENTS and must be at least 15.
        log.debug("Max varying vectors: {}", glGetInteger(GL_MAX_VARYING_VECTORS));

        // data returns one value, the maximum number of interpolators available for processing varying variables used by vertex and fragment shaders. This value represents the number of individual floating-point values that can be interpolated; varying variables declared as vectors, matrices, and arrays will all consume multiple interpolators. The value must be at least 32.
        log.debug("Max varying floats: {}", glGetInteger(GL_MAX_VARYING_FLOATS));

        // data returns a single value, the maximum number of atomic counters available to vertex shaders.
        log.debug("Max vertex atomic counters: {}", glGetInteger(GL_MAX_VERTEX_ATOMIC_COUNTERS));

        // data returns one value, the maximum number of 4-component generic vertex attributes accessible to a vertex shader. The value must be at least 16. See glVertexAttrib.
        log.debug("Max vertex attributes: {}", glGetInteger(GL_MAX_VERTEX_ATTRIBS));

        // data returns one value, the maximum number of active shader storage blocks that may be accessed by a vertex shader.
        log.debug("Max vertex shader storage blocks: {}", glGetInteger(GL_MAX_VERTEX_SHADER_STORAGE_BLOCKS));

        // data returns one value, the maximum supported texture image units that can be used to access texture maps from the vertex shader. The value may be at least 16. See glActiveTexture.
        log.debug("Max vertex texture image units: {}", glGetInteger(GL_MAX_VERTEX_TEXTURE_IMAGE_UNITS));

        // data returns one value, the maximum number of individual floating-point, integer, or boolean values that can be held in uniform variable storage for a vertex shader. The value must be at least 1024. See glUniform.
        log.debug("Max vertex uniform components: {}", glGetInteger(GL_MAX_VERTEX_UNIFORM_COMPONENTS));

        // data returns one value, the maximum number of 4-vectors that may be held in uniform variable storage for the vertex shader. The value of GL_MAX_VERTEX_UNIFORM_VECTORS is equal to the value of GL_MAX_VERTEX_UNIFORM_COMPONENTS and must be at least 256.
        log.debug("Max vertex uniform vectors: {}", glGetInteger(GL_MAX_VERTEX_UNIFORM_VECTORS));

        // data returns one value, the maximum number of components of output written by a vertex shader, which must be at least 64.
        log.debug("Max vertex output components: {}", glGetInteger(GL_MAX_VERTEX_OUTPUT_COMPONENTS));

        // data returns one value, the maximum number of uniform blocks per vertex shader. The value must be at least 12. See glUniformBlockBinding.
        log.debug("Max vertex uniform blocks: {}", glGetInteger(GL_MAX_VERTEX_UNIFORM_BLOCKS));

        // Returns max number of samples available in application (not from documentation)
        log.debug("Max samples: {}", glGetInteger(GL_MAX_SAMPLES));

        // data returns two values: the maximum supported width and height of the viewport. These must be at least as large as the visible dimensions of the display being rendered to. See glViewport.
        log.debug("Max viewport dimensions: {}", glGetVector2i(GL_MAX_VIEWPORT_DIMS).toString(NUMBER_FORMAT_INT));

        // data returns one value, the maximum number of simultaneous viewports that are supported. The value must be at least 16. See glViewportIndexed.
        log.debug("Max viewports: {}", glGetInteger(GL_MAX_VIEWPORTS));

        // When used with non-indexed variants of glGet (such as glGetIntegerv), data returns four values: the x and y window coordinates of the viewport, followed by its width and height. Initially the x and y window coordinates are both set to 0, and the width and height are set to the width and height of the window into which the GL will do its rendering. See glViewport.
        // When used with indexed variants of glGet (such as glGetIntegeri_v), data returns four values: the x and y window coordinates of the indexed viewport, followed by its width and height. Initially the x and y window coordinates are both set to 0, and the width and height are set to the width and height of the window into which the GL will do its rendering. See glViewportIndexedf.
        log.debug("Viewport: {}", glGetVector4i(GL_VIEWPORT).toString(NUMBER_FORMAT_INT));

        // data returns two values, the minimum and maximum viewport bounds range. The minimum range should be at least [-32768, 32767].
        log.debug("Viewport bounds range: {}", glGetVector2i(GL_VIEWPORT_BOUNDS_RANGE).toString(NUMBER_FORMAT_INT));

        // data returns one value, the implementation dependent specifc vertex of a primitive that is used to select the viewport index. If the value returned is equivalent to GL_PROVOKING_VERTEX, then the vertex selection follows the convention specified by glProvokingVertex. If the value returned is equivalent to GL_FIRST_VERTEX_CONVENTION, then the selection is always taken from the first vertex in the primitive. If the value returned is equivalent to GL_LAST_VERTEX_CONVENTION, then the selection is always taken from the last vertex in the primitive. If the value returned is equivalent to GL_UNDEFINED_VERTEX, then the selection is not guaranteed to be taken from any specific vertex in the primitive.
        log.debug("Viewport index provoking vertex: {}", glGetInteger(GL_VIEWPORT_INDEX_PROVOKING_VERTEX));

        // data returns a single value, the number of bits of sub-pixel precision which the GL uses to interpret the floating point viewport bounds. The minimum value is 0.
        log.debug("Viewport subpixel bits: {}", glGetInteger(GL_VIEWPORT_SUBPIXEL_BITS));

        // data returns one value, the minor version number of the OpenGL API supported by the current context.
        log.debug("Minor version: {}", glGetInteger(GL_MINOR_VERSION));

        // data returns one value, the number of extensions supported by the GL implementation for the current context. See glGetString.
        log.debug("Number of extensions: {}", glGetInteger(GL_NUM_EXTENSIONS));

        // data returns one value, the number of program binary formats supported by the implementation.
        log.debug("Number of program binary formats: {}", glGetInteger(GL_NUM_PROGRAM_BINARY_FORMATS));

        // data an array of GL_NUM_PROGRAM_BINARY_FORMATS values, indicating the proram binary formats supported by the implementation.
        log.debug("Program binary formats: {}", glGetArray(GL_PROGRAM_BINARY_FORMATS, glGetInteger(GL_NUM_PROGRAM_BINARY_FORMATS)));

        // data returns one value, the number of binary shader formats supported by the implementation. If this value is greater than zero, then the implementation supports loading binary shaders. If it is zero, then the loading of binary shaders by the implementation is not supported.
        log.debug("Number of shader binary formats: {}", glGetInteger(GL_NUM_SHADER_BINARY_FORMATS));

        // params returns a list of symbolic constants of length GL_NUM_SHADER_BINARY_FORMATS indicating which shader binary formats are available. See glShaderBinary.
        log.debug("Program binary formats: {}", glGetArray(GL_SHADER_BINARY_FORMATS, glGetInteger(GL_NUM_SHADER_BINARY_FORMATS)));

        // data returns one value, the point size threshold for determining the point size. See glPointParameter.
        log.debug("Point fade threshold size: {}", glGetInteger(GL_POINT_FADE_THRESHOLD_SIZE));

        // data a single value, the name of the currently bound program pipeline object, or zero if no program pipeline object is bound. See glBindProgramPipeline.
        log.debug("Program pipeline binding: {}", glGetInteger(GL_PROGRAM_PIPELINE_BINDING));

        // data returns one value, the size difference between adjacent supported sizes for antialiased points. See glPointSize.
        log.debug("Point size granularity {}", glGetInteger(GL_POINT_SIZE_GRANULARITY));

        // data returns two values: the smallest and largest supported sizes for antialiased points. The smallest size must be at most 1, and the largest size must be at least 1. See glPointSize.
        log.debug("Point size range: {}", glGetInteger(GL_POINT_SIZE_RANGE));

        // data returns a single integer value indicating the number of sample buffers associated with the framebuffer. See glSampleCoverage.
        log.debug("Sample buffers: {}", glGetInteger(GL_SAMPLE_BUFFERS));

        // data returns a single positive floating-point value indicating the current sample coverage value. See glSampleCoverage.
        log.debug("Sample coverage value: {}", glGetInteger(GL_SAMPLE_COVERAGE_VALUE));

        // data returns a single boolean value indicating if the temporary coverage value should be inverted. See glSampleCoverage.
        log.debug("Sample coverage invert: {}", glGetInteger(GL_SAMPLE_COVERAGE_INVERT));

        // data returns a single integer value indicating the coverage mask size. See glSampleCoverage.
        log.debug("Samples: {}", glGetInteger(GL_SAMPLES));

        // data returns a single boolean value indicating whether an online shader compiler is present in the implementation. All desktop OpenGL implementations must support online shader compilations, and therefore the value of GL_SHADER_COMPILER will always be GL_TRUE.
        log.debug("Shader compiler: {}", glGetInteger(GL_SHADER_COMPILER));

        // data returns a pair of values indicating the range of widths supported for smooth (antialiased) lines. See glLineWidth.
        log.debug("Smooth line width range: {}", glGetVector2i(GL_SMOOTH_LINE_WIDTH_RANGE).toString(NUMBER_FORMAT_INT));

        // data returns a single value indicating the level of quantization applied to smooth line width parameters.
        log.debug("Smooth line width granularity: {}", glGetInteger(GL_SMOOTH_LINE_WIDTH_GRANULARITY));

        // data returns a single boolean value indicating whether stereo buffers (left and right) are supported.
        log.debug("Stereo: {}", glGetInteger(GL_STEREO));

        // data returns one value, an estimate of the number of bits of subpixel resolution that are used to position rasterized geometry in window coordinates. The value must be at least 4.
        log.debug("Subpixel bits: {}", glGetInteger(GL_SUBPIXEL_BITS));

        // data returns a single value, the 64-bit value of the current GL time. See glQueryCounter.
        log.debug("Timestamp: {}", glGetInteger(GL_TIMESTAMP));

        // data returns a single integer value containing the maximum offset that may be added to a vertex binding offset.
        log.debug("Max vertex attribute relative offset: {}", glGetInteger(GL_MAX_VERTEX_ATTRIB_RELATIVE_OFFSET));

        // data returns a single integer value containing the maximum number of vertex buffers that may be bound.
        log.debug("Max vertex attribute bindings: {}", glGetInteger(GL_MAX_VERTEX_ATTRIB_BINDINGS));

        // data returns a single value, the maximum index that may be specified during the transfer of generic vertex attributes to the GL.
        log.debug("Max element index: {}", glGetInteger(GL_MAX_ELEMENT_INDEX));

    }

    private static int[] glGetArray(int parameterName, int size) {
        int[] arr = new int[size];
        glGetIntegerv(parameterName, arr);
        return arr;
    }

    private static Vector2i glGetVector2i(int parameterName) {
        int[] arr = new int[2];
        glGetIntegerv(parameterName, arr);
        return new Vector2i(arr[0], arr[1]);
    }

    private static Vector3i glGetVector3i(int parameterName) {
        int[] arr = new int[3];
        glGetIntegerv(parameterName, arr);
        return new Vector3i(arr[0], arr[1], arr[2]);
    }

    private static Vector4i glGetVector4i(int parameterName) {
        int[] arr = new int[4];
        glGetIntegerv(parameterName, arr);
        return new Vector4i(arr[0], arr[1], arr[2], arr[3]);
    }

    private static Vector3i glGetVector3iIndexed(int parameterName) {
        return new Vector3i(
                glGetIntegeri(parameterName, 0),
                glGetIntegeri(parameterName, 1),
                glGetIntegeri(parameterName, 2)
        );
    }

}
