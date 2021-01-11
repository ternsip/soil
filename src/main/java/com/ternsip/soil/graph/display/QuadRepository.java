package com.ternsip.soil.graph.display;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;

@Slf4j
public class QuadRepository {

    public final ArrayList<Quad> quads = new ArrayList<>();
    public final ArrayList<Integer> quadOrder = new ArrayList<>();
    public final TreeMap<Integer, Integer> layerToQuadOrderOffset = new TreeMap<>(Collections.reverseOrder());

    public int getNumberOfQuads() {
        return quads.size();
    }

}
