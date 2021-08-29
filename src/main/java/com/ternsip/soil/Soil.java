package com.ternsip.soil;

import com.google.common.collect.Sets;
import com.ternsip.soil.general.Threads;

import java.util.Set;

/**
 * The main entry point of the application
 * Initializes graphic, network and logic thread
 * Graphic thread should always be main by multi-platform purposes
 * <p>
 * In case you have GPU-dump crashes:
 * - checkout memory buffers (for instance that all of them rewind() after reading)
 * - try to avoid memory buffers if possible
 * - check memory buffers' explicit free calls
 * - check data that you send to GPU i.e. number of vertices/textures/indices/colors etc.
 * - in case you want to debug errors - use debug mode
 * - be careful with @Delegate sometimes it breaks navigation or debugger, also recursive delegate does not allowed
 *
 * @author Ternsip
 * TODO test double monitors swap (including different Hz and color pallet) and screen resize
 * TODO use nglfwGetJoystickAxes instead of glfwGetJoystickAxes to not copy buffer etc.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 * Не создавать объект каждый раз при INPUT операциях - напрямую вызывать функции подписанные на событие, если это ещё не так
 * Иметь множество шейдеров, да это страшно, но это правильно, иначе появляется ветвление внутри шейдера, что сильно замедляет его работу
 * Множество шейдеров помогает не сваливать всё в одну кучу и таким образом больше порядка
 * Использовать входные аттрибуты и управление ими, так как это стандарты OPENGL и лучше от них не отходить для читаемости
 * Использовать входные аттрибуты потому что не известно как буфферные SSBO компоненты могут себя повести (возможно стоит сделать переключение как опцию)
 * Исправить 3d Texture на 2D texture array иначе mipmap не будет работать https://stackoverflow.com/questions/35818027/understanding-the-difference-between-a-2d-texture-array-and-a-3d-texture
 * Сейчас из-за того что 3d texture когда mipmap выбирает следующий уровень, количество кадров анимации сжимается что приведет к ошибке
 * Проверить что максимальный размер текстур может быть больше 2048
 * Uniform блоки более удобны и могут использоваться сразу несколькими шейдерами
 *
 * GPU-анимация не должна никак задействовать CPU, например, частицы разлетаются в рандомные стороны, но результат отпечаетывается в буфере
 * Для GPU анимации стоит использовать преобразование обратной связи (glFeedbackBuffer ?)
 *
 */
public class Soil {

    public static final Threads THREADS = new Threads();

    public static void main(String[] args) {
        Set<String> input = Sets.newHashSet(args);
        if (input.contains("--server")) {
            THREADS.runServer();
        } else {
            THREADS.runClient();
        }
    }

}
