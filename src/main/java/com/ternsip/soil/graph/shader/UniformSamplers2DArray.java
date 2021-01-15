package com.ternsip.soil.graph.shader;

public class UniformSamplers2DArray extends Uniform {

    private UniformSampler[] uniformSampler2DArrays;

    public UniformSamplers2DArray(int size) {
        uniformSampler2DArrays = new UniformSampler[size];
        for (int i = 0; i < size; i++) {
            uniformSampler2DArrays[i] = new UniformSampler();
        }
    }

    @Override
    public void locate(int programID, String name) {
        for (int i = 0; i < uniformSampler2DArrays.length; ++i) {
            uniformSampler2DArrays[i].locate(programID, name + "[" + i + "]");
        }
    }

    public void load(int index, int value) {
        uniformSampler2DArrays[index].load(value);
    }

    public void load(int[] value) {
        for (int i = 0; i < value.length; i++) {
            uniformSampler2DArrays[i].load(value[i]);
        }
    }

    public void loadDefault() {
        for (int i = 0; i < uniformSampler2DArrays.length; i++) {
            uniformSampler2DArrays[i].load(i);
        }
    }

}
