package com.ternsip.soil.graph.display;

import lombok.RequiredArgsConstructor;

import java.io.File;

@RequiredArgsConstructor
public enum CursorType {

    SIMPLE(new File("soil/interface/cursor_simple.png")),
    SELECT(new File("soil/interface/grass.gif")),
    LOADING(new File("soil/interface/loading.gif"));

    public final File file;

}