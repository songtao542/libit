package com.liabit.cropper.glrenderer;

// This mimics corresponding GL functions.
public interface GLId {
    int generateTexture();

    void glGenBuffers(int n, int[] buffers, int offset);

}
