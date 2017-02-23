/*
 * Copyright (c) 2017 UniqueStudio
 *
 *
 * This file is part of ParsingPlayer.
 *
 * ParsingPlayer is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with ParsingPlayer; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
 */

package com.hustunique.parsingplayer.player.view.gles;

import android.opengl.GLES20;
import android.util.Log;

import java.nio.FloatBuffer;

/**
 * GL program and supporting functions for flat-shaded rendering.
 */
public class FlatShadedProgram {
    private static final String TAG = GlUtil.TAG;

    private static final String VERTEX_SHADER =
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 aPosition;" +
            "void main() {" +
            "    gl_Position = uMVPMatrix * aPosition;" +
            "}";

    private static final String FRAGMENT_SHADER =
            "precision mediump float;" +
            "uniform vec4 uColor;" +
            "void main() {" +
            "    gl_FragColor = uColor;" +
            "}";

    // Handles to the GL program and various components of it.
    private int mProgramHandle = -1;
    private int muColorLoc = -1;
    private int muMVPMatrixLoc = -1;
    private int maPositionLoc = -1;


    /**
     * Prepares the program in the current EGL context.
     */
    public FlatShadedProgram() {
        mProgramHandle = GlUtil.createProgram(VERTEX_SHADER, FRAGMENT_SHADER);
        if (mProgramHandle == 0) {
            throw new RuntimeException("Unable to create program");
        }
        Log.d(TAG, "Created program " + mProgramHandle);

        // get locations of attributes and uniforms

        maPositionLoc = GLES20.glGetAttribLocation(mProgramHandle, "aPosition");
        GlUtil.checkLocation(maPositionLoc, "aPosition");
        muMVPMatrixLoc = GLES20.glGetUniformLocation(mProgramHandle, "uMVPMatrix");
        GlUtil.checkLocation(muMVPMatrixLoc, "uMVPMatrix");
        muColorLoc = GLES20.glGetUniformLocation(mProgramHandle, "uColor");
        GlUtil.checkLocation(muColorLoc, "uColor");
    }

    /**
     * Releases the program.
     */
    public void release() {
        GLES20.glDeleteProgram(mProgramHandle);
        mProgramHandle = -1;
    }

    /**
     * Issues the draw call.  Does the full setup on every call.
     *
     * @param mvpMatrix The 4x4 projection matrix.
     * @param color A 4-element color vector.
     * @param vertexBuffer Buffer with vertex data.
     * @param firstVertex Index of first vertex to use in vertexBuffer.
     * @param vertexCount Number of vertices in vertexBuffer.
     * @param coordsPerVertex The number of coordinates per vertex (e.g. x,y is 2).
     * @param vertexStride Width, in bytes, of the data for each vertex (often vertexCount *
     *        sizeof(float)).
     */
    public void draw(float[] mvpMatrix, float[] color, FloatBuffer vertexBuffer,
            int firstVertex, int vertexCount, int coordsPerVertex, int vertexStride) {
        GlUtil.checkGlError("draw start");

        // Select the program.
        GLES20.glUseProgram(mProgramHandle);
        GlUtil.checkGlError("glUseProgram");

        // Copy the model / view / projection matrix over.
        GLES20.glUniformMatrix4fv(muMVPMatrixLoc, 1, false, mvpMatrix, 0);
        GlUtil.checkGlError("glUniformMatrix4fv");

        // Copy the color vector in.
        GLES20.glUniform4fv(muColorLoc, 1, color, 0);
        GlUtil.checkGlError("glUniform4fv ");

        // Enable the "aPosition" vertex attribute.
        GLES20.glEnableVertexAttribArray(maPositionLoc);
        GlUtil.checkGlError("glEnableVertexAttribArray");

        // Connect vertexBuffer to "aPosition".
        GLES20.glVertexAttribPointer(maPositionLoc, coordsPerVertex,
            GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
        GlUtil.checkGlError("glVertexAttribPointer");

        // Draw the rect.
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, firstVertex, vertexCount);
        GlUtil.checkGlError("glDrawArrays");

        // Done -- disable vertex array and program.
        GLES20.glDisableVertexAttribArray(maPositionLoc);
        GLES20.glUseProgram(0);
    }
}
