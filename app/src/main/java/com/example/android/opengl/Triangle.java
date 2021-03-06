/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.util.Log;


/**
 * A two-dimensional triangle for use as a drawn object in OpenGL ES 2.0.
 */
public class Triangle {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            // the matrix must be included as a modifier of gl_Position
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final FloatBuffer colorBuffer;
    private final FloatBuffer normalBuffer;
    private final FloatBuffer textureBuffer;
    private int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
            // in counterclockwise order:
            0.0f,  0.622008459f, 0.0f,   // top
           -0.5f, -0.311004243f, 0.0f,   // bottom left
            0.5f, -0.311004243f, 0.0f    // bottom right
    };
    // Stores normal data for lighting (must be set!)
    float normalCoords[] = {
            // x, y,    z
            0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            1.0f, -1.0f, 1.0f
    };

    float texCoords[] = {
            //x, y
            0.5f, 0.5f,
            0.5f, 0.5f,
            0.5f, 0.5f
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex
    private final int textureDataSize = 2;
    private final int normalDataSize = 3;

    float color[] = {1.000f, 1.000f, 1.0f, 0.5f};

    public Triangle(float[] coord)
    {

        triangleCoords = coord; //set the triangles coordinates


        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //initialise colorBuffer
        ByteBuffer cb = ByteBuffer.allocateDirect(color.length*4).order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer().put(color);
        colorBuffer.position(0);

        //initialise normalBuffer
        ByteBuffer nb = ByteBuffer.allocateDirect(normalCoords.length*4).order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer().put(normalCoords);
        normalBuffer.position(0);

        //initialise textureBuffer
        ByteBuffer tb = ByteBuffer.allocateDirect(texCoords.length*4).order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer().put(texCoords);
        textureBuffer.position(0);


        // prepare shaders and OpenGL program
        /*int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
*/
    }

    public Triangle(float[] coord, float[] norms)
    {

        triangleCoords = coord; //set the triangles coordinates
        normalCoords = norms;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //initialise colorBuffer
        ByteBuffer cb = ByteBuffer.allocateDirect(color.length*4).order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer().put(color);
        colorBuffer.position(0);

        //initialise normalBuffer
        ByteBuffer nb = ByteBuffer.allocateDirect(normalCoords.length*4).order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer().put(normalCoords);
        normalBuffer.position(0);

        //initialise textureBuffer
        ByteBuffer tb = ByteBuffer.allocateDirect(texCoords.length*4).order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer().put(texCoords);
        textureBuffer.position(0);


        // prepare shaders and OpenGL program
        /*int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
*/
    }

    public Triangle(float[] coord, float[] norms, float[] tex)
    {

        triangleCoords = coord; //set the triangles coordinates
        normalCoords = norms;
        texCoords = tex;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //initialise colorBuffer
        ByteBuffer cb = ByteBuffer.allocateDirect(color.length*4).order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer().put(color);
        colorBuffer.position(0);

        //initialise normalBuffer
        ByteBuffer nb = ByteBuffer.allocateDirect(normalCoords.length*4).order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer().put(normalCoords);
        normalBuffer.position(0);

        //initialise textureBuffer
        ByteBuffer tb = ByteBuffer.allocateDirect(texCoords.length*4).order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer().put(texCoords);
        textureBuffer.position(0);


        // prepare shaders and OpenGL program
       /* int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
*/
    }

    public Triangle(float[] coord,float[] inColor, float[] norms, float[] tex)
    {

        triangleCoords = coord; //set the triangles coordinates
        normalCoords = norms;
        texCoords = tex;

        color = inColor;

        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        //initialise colorBuffer
        ByteBuffer cb = ByteBuffer.allocateDirect(color.length*4).order(ByteOrder.nativeOrder());
        colorBuffer = cb.asFloatBuffer().put(color);
        colorBuffer.position(0);

        //initialise normalBuffer
        ByteBuffer nb = ByteBuffer.allocateDirect(normalCoords.length*4).order(ByteOrder.nativeOrder());
        normalBuffer = nb.asFloatBuffer().put(normalCoords);
        normalBuffer.position(0);

        //initialise textureBuffer
        ByteBuffer tb = ByteBuffer.allocateDirect(texCoords.length*4).order(ByteOrder.nativeOrder());
        textureBuffer = tb.asFloatBuffer().put(texCoords);
        textureBuffer.position(0);


        // prepare shaders and OpenGL program
       /* int vertexShader = MyGLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = MyGLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
*/
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void drawOLD(float[] mvpMatrix) {

        //Log.w("called tri draw", "triangle");

        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member

        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "v_Position");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "v_Color");
        GLES20.glEnableVertexAttribArray(mColorHandle);
        GLES20.glVertexAttribPointer(mColorHandle, 4, GLES20.GL_FLOAT, false, 4, colorBuffer);

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
       // mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(MyGLRenderer.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void draw(float[] mvpMatrix) {

        //Log.w("called tri draw", "triangle");

        // Add program to OpenGL environment
        GLES20.glUseProgram(MyGLRenderer.mShapeProgramHandle);

        // get handle to vertex shader's vPosition member

        mProgram = MyGLRenderer.mShapeProgramHandle;
        mPositionHandle =  MyGLRenderer.mPositionHandle;
        mColorHandle = MyGLRenderer.mColorHandle;
        mMVPMatrixHandle = MyGLRenderer.mMVPMatrixHandle;

        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "a_Color");

        // Set color for drawing the triangle


        //mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        GLES20.glVertexAttrib4fv(mColorHandle, color, 0);

        MyGLRenderer.checkGlError("Before Color Thing");
        //GLES20.glUniform4fv(mColorHandle, 1, new float[] {0.5f, 0.5f, 0.5f, 0.5f}, 0);
        MyGLRenderer.checkGlError("Color thing");
        // get handle to fragment shader's vColor member
        //mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        //GLES20.glUniform4fv(mColorHandle, 0, color, 0);

        //pass in normal info
        normalBuffer.position(0);
        GLES20.glVertexAttribPointer(MyGLRenderer.mNormalHandle, normalDataSize, GLES20.GL_FLOAT, false,
                0, normalBuffer);
        GLES20.glEnableVertexAttribArray(MyGLRenderer.mNormalHandle);

        //pass in texture info
        textureBuffer.position(0);
        GLES20.glVertexAttribPointer(MyGLRenderer.mTextureCoordinateHandle, textureDataSize, GLES20.GL_FLOAT, false,
                0, textureBuffer);
        GLES20.glEnableVertexAttribArray(MyGLRenderer.mTextureCoordinateHandle);

        // get handle to shape's transformation matrix
        //mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        //MyGLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        //GLES20.glUniformMatrix4fv(MyGLRenderer.mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        //MyGLRenderer.checkGlError("glUniformMatrix4fv");

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }

    public void setColor(){

        //colors
        float[] old = {0.63671875f, 0.76953125f, 0.22265625f, 0.0f};
        float[] new1 = {0.3f, 0.5f, 0.44f, 0.11f};
        float[] n2 = {0.2f, 0.01f, 0.2f, 0.1f};
        float[]n3 = {1.0f,0f,0f,0f};

        Random r = new Random();



        int randInt = r.nextInt(4);

        Log.w("color change", "r = " + randInt);

        switch(randInt){
            case 0:
                color = old;
                break;
            case 1:
                color = new1;
                break;
            case 2:
                color = n2;
                break;
            case 3:
                color = n3;
                break;
        }

    }

}
