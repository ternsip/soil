package com.ternsip.soil.common.events.display;

import com.ternsip.soil.common.events.base.Event;
import com.ternsip.soil.graph.shader.base.Shader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ShaderRegisteredEvent implements Event {

    private final Shader shader;

}