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

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.provider.Settings;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    private Cylinder mCylinder;
    public Context mActivityContext;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    public final float[] mMVPMatrix = new float[16];
    public static float[] mMVMatrix = new float[16];
    public final float[] mProjectionMatrix = new float[16];
    public final float[] mViewMatrix = new float[16];
    public final float[] mRotationMatrix = new float[16];
    public final float[] mModelMatrix = new float[16];
    public final float[] mCurrentRotation = new float[16];
    public final float[] mCurrentRotationX = new float[16];
    public final float[] mCurrentRotationY = new float[16];
    public final float[] mAccumulatedRotation = new float[16];
    public final int cameraMode =0;
    /*
        0 -- model rotation
        1 -- camera rotation
    */
    // create a temporary matrix for calculation purposes,
// to avoid the same matrix on the right and left side of multiplyMM later
// see http://stackoverflow.com/questions/13480043/opengl-es-android-matrix-transformations#comment18443759_13480364
    private float[] mTempMatrix = new float[16];

    private float mAngle;
    private float mX;
    private float mY;

    // Handles to shader programs and things needed by same
    public static int mShapeProgramHandle;
    public static int mPointProgramHandle;
    public static int mMVPMatrixHandle;
    public static int mPositionHandle;
    public static int mColorHandle;
    public static int mNormalHandle;
    public static int mLightPosHandle;
    public static int mMVMatrixHandle;
    public static int mViewMatrixHandle;

    // Lighting position matrices
    public static float[] mLightPosInModelSpace = new float[]{0.0f, 0.0f, 0.0f, 1.0f};
    public static float[] mLightPosInWorldSpace = new float[4];
    public static float[] mLightPosInEyeSpace = new float[4];
    public static float[] mLightModelMatrix = new float[16];

    //Texturing Stuff
    public static int mTextureDataHandle;
    public static int mTextureUniformHandle;
    public static int mTextureCoordinateHandle;

    public MyGLRenderer(Context context) {
        mActivityContext = context;
    }

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        GLES20.glEnable(GLES20.GL_CULL_FACE);
        GLES20.glEnable(GLES20.GL_DEPTH_TEST);

        Matrix.setIdentityM(mAccumulatedRotation, 0);
        mCylinder = new Cylinder();

        final String vertexShaderSource = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.vertexshadersource);
        final String fragmentShaderSource = RawResourceReader.readTextFileFromRawResource(mActivityContext, R.raw.fragmentshadersource);

        final int vertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, vertexShaderSource);
        final int fragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderSource);

        mShapeProgramHandle = createAndLinkProgram(vertexShaderHandle, fragmentShaderHandle,
                new String[]{"a_Position", "a_Color", "a_Normal", "a_TexCoordinate"});

        mTextureDataHandle = loadTexture(mActivityContext, R.raw.cokecan);

        final String pointVertexShader =
                "uniform mat4 u_MVPMatrix; \n"
                        +	"attribute vec4 a_Position; \n"
                        + "void main() \n"
                        + "{ \n"
                        + " gl_Position = u_MVPMatrix \n"
                        + " * a_Position; \n"
                        + " gl_PointSize = 5.0; \n"
                        + "} \n";
        final String pointFragmentShader =
                "precision mediump float; \n"
                        + "void main() \n"
                        + "{ \n"
                        + " gl_FragColor = vec4(1.0, \n"
                        + " 1.0, 1.0, 1.0); \n"
                        + "} \n";

        final int pointVertexShaderHandle = compileShader(GLES20.GL_VERTEX_SHADER, pointVertexShader);
        final int pointFragmentShaderHandle = compileShader(GLES20.GL_FRAGMENT_SHADER, pointFragmentShader);
        mPointProgramHandle = createAndLinkProgram(pointVertexShaderHandle, pointFragmentShaderHandle,
                new String[]{"a_Position"});


        //GLES20.glDisable(GLES20.GL_CULL_FACE);

        // GLES20.glEnable(GLES20.GL_DEPTH_TEST);
    }

    @Override
    public void onDrawFrame(GL10 unused) {
        float[] scratch = new float[16];

//Matrix.translateM();
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);

        // GLES20.glEnable(GLES20.GL_CULL_FACE);


        long time = SystemClock.uptimeMillis() % 10000L;
        float angleInDegrees = (360.0f / 10000.0f) * ((int) time);


        // Set our per-vertex lighting program.
        GLES20.glUseProgram(mShapeProgramHandle);
// Set program handles for cube drawing.
        mViewMatrixHandle = GLES20.glGetUniformLocation(mShapeProgramHandle, "u_ViewMatrix");
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mShapeProgramHandle, "u_MVPMatrix");
        mMVMatrixHandle = GLES20.glGetUniformLocation(mShapeProgramHandle, "u_MVMatrix");
        mLightPosHandle = GLES20.glGetUniformLocation(mShapeProgramHandle, "u_LightPos");
        mTextureUniformHandle = GLES20.glGetAttribLocation(mShapeProgramHandle, "u_Texture");
        mPositionHandle = GLES20.glGetAttribLocation(mShapeProgramHandle, "a_Position");
        mColorHandle = GLES20.glGetAttribLocation(mShapeProgramHandle, "a_Color");
        mNormalHandle = GLES20.glGetAttribLocation(mShapeProgramHandle, "a_Normal");
        mTextureCoordinateHandle = GLES20.glGetAttribLocation(mShapeProgramHandle, "a_TexCoordinate");


        //GLES20.glUniform4fv(mColorHandle, 1, new float[]{1.0f,1.0f,1.0f,1.0f}, 0);
        //checkGlError("glUniform4fv");

        //set active texture to texture unit 0  (gfx cards have a certain number of tex units)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        //bind the texture to this unit
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureDataHandle);

        //tell the tex uniform sampler to use this texture by binding it to 0
        int i = 0;
        GLES20.glUniform1i(mTextureUniformHandle, i);
        Matrix.setLookAtM(mViewMatrix,  0,          0,      0,    -2f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);
        //Matrix.setLookAtM(rm,         rmOffset,   eyex,   eyey, eyez, centerx, centery, centerz, upx, upy, upz);
        if(cameraMode==1) {
            // ROTATION INPUT -- for rotating camera position
            Matrix.setIdentityM(mCurrentRotationX, 0);
            Matrix.setIdentityM(mCurrentRotationY, 0);

            Matrix.setRotateM(mCurrentRotationX, 0, mX, 0, -1f, 0f);
            Matrix.setRotateM(mCurrentRotationY, 0, mY, 1.0f, 0, 0f);
            Matrix.multiplyMM(mCurrentRotation, 0, mCurrentRotationX, 0, mCurrentRotationY, 0);
            mX = 0.0f;
            mY = 0.0f;

            Matrix.multiplyMM(mTempMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
            System.arraycopy(mTempMatrix, 0, mAccumulatedRotation, 0, 16);
            Matrix.multiplyMM(mTempMatrix, 0, mViewMatrix, 0, mAccumulatedRotation, 0);
            System.arraycopy(mTempMatrix, 0, mViewMatrix, 0, 16);
            //ROTATE ENDS
        }

        // Calculate position of the light. Rotate and then push into the distance.
        Matrix.setIdentityM(mLightModelMatrix, 0);
        //Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, -5.0f);
        Matrix.rotateM(mLightModelMatrix, 0, angleInDegrees, 0.0f, 1.0f, 0.0f);
        Matrix.translateM(mLightModelMatrix, 0, 0.0f, 0.0f, 2.0f);
        Matrix.multiplyMV(mLightPosInWorldSpace, 0, mLightModelMatrix, 0, mLightPosInModelSpace, 0);
        Matrix.multiplyMV(mLightPosInEyeSpace, 0, mViewMatrix, 0, mLightPosInWorldSpace, 0);

        //Coke Can Stuff
        Matrix.setIdentityM(mModelMatrix, 0);
        //Matrix.translateM(mModelMatrix, 0, 0, 0, -1.25f);

        if(cameraMode==0) {
            // ROTATION INPUT -- model
            Matrix.setIdentityM(mCurrentRotationX, 0);
            Matrix.setIdentityM(mCurrentRotationY, 0);

            Matrix.setRotateM(mCurrentRotationX, 0, mX, 0, -1f, 0f);
            Matrix.setRotateM(mCurrentRotationY, 0, mY, 1.0f, 0, 0f);
            Matrix.multiplyMM(mCurrentRotation, 0, mCurrentRotationX, 0, mCurrentRotationY, 0);
            mX = 0.0f;
            mY = 0.0f;

            Matrix.multiplyMM(mTempMatrix, 0, mCurrentRotation, 0, mAccumulatedRotation, 0);
            System.arraycopy(mTempMatrix, 0, mAccumulatedRotation, 0, 16);
            Matrix.multiplyMM(mTempMatrix, 0, mModelMatrix, 0, mAccumulatedRotation, 0);
            System.arraycopy(mTempMatrix, 0, mModelMatrix, 0, 16);

        }

        //Matrix.rotateM(mModelMatrix, 0, 270, 1.0f, 0.0f, 0.0f);


        Matrix.multiplyMM(mMVMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);

        //Matrix.translateM(mMVMatrix, 0, 0, 0, 1.0f);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVMatrix, 0);

        //pass int the view matrix to shader
        GLES20.glUniformMatrix4fv(mViewMatrixHandle, 1, false, mViewMatrix, 0);
        //pass in the modelview matrix (to the shader program)
        GLES20.glUniformMatrix4fv(mMVMatrixHandle, 1, false, mMVMatrix, 0);
        // Pass in the MVPMatrix
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);
        // Pass in the Light Position
        GLES20.glUniform3f(mLightPosHandle, mLightPosInEyeSpace[0], mLightPosInEyeSpace[1], mLightPosInEyeSpace[2]);

        GLES20.glUseProgram(mShapeProgramHandle);
        mCylinder.draw(mMVPMatrix);

        drawLight();


        //  GLES20.glDisable(GLES20.GL_CULL_FACE);
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        final float ratio = (float) width/height;
        final float left = -ratio;
        final float right = ratio;
        final float bottom = -1.0f;
        final float top = 1.0f;
        final float near = 1.0f;
        final float far = 10.0f;
        Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     * <p/>
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type       - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode) {

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     * <p/>
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

    /**
     * Returns the rotation angle of the triangle shape (mTriangle).
     *
     * @return - A float representing the rotation angle.
     */
    public float getAngle() {
        return mAngle;
    }

    /**
     * Sets the rotation angle of the triangle shape (mTriangle).
     */
    public void setAngle(float angle) {
        mAngle = angle;
    }

    public float getmY() {
        return mY;
    }

    public float getmX() {
        return mX;
    }

    public void setX(float newX) {
        mX = newX;
    }

    public void setY(float newY) {
        mY = newY;
    }

    public static int loadTexture(final Context context, final int resourceId) {
        final int[] textureHandle = new int[1];

        GLES20.glGenTextures(1, textureHandle, 0);

        if (textureHandle[0] != 0) {

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inScaled = false;   // No pre-scaling


            // Read in the resource
            final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);

            // Bind to the texture in OpenGL
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);

            // Set filtering
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
            GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);

            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_REPEAT);
            GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_REPEAT);

            // Load the bitmap into the bound texture.
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);

            // Recycle the bitmap, since its data has been loaded into OpenGL.
            bitmap.recycle();
        }

        if (textureHandle[0] == 0) {
            throw new RuntimeException("Error loading texture.");
        }

        return textureHandle[0];
    }

    public static int compileShader(final int shaderType, final String shaderSource) {
        int shaderHandle = GLES20.glCreateShader(shaderType);
        if (shaderHandle != 0) {
            // Pass in the shader source.
            GLES20.glShaderSource(shaderHandle, shaderSource);
            // Compile the shader.
            GLES20.glCompileShader(shaderHandle);
            // Get the compilation status.
            final int[] compileStatus = new int[1];
            GLES20.glGetShaderiv(shaderHandle, GLES20.GL_COMPILE_STATUS, compileStatus, 0);
            // If the compilation failed, delete the shader.
            if (compileStatus[0] == 0) {
                Log.e(TAG, "Error compiling shader: " + GLES20.glGetShaderInfoLog(shaderHandle));
                GLES20.glDeleteShader(shaderHandle);
                //shaderHandle = 0;
            }
        }
        if (shaderHandle == 0) {
            throw new RuntimeException("Error creating shader.");
        }
        return shaderHandle;
    }

    public static int createAndLinkProgram(final int vertexShaderHandle, final int fragmentShaderHandle,
                                           final String[] attributes) {
        int programHandle = GLES20.glCreateProgram();
        if (programHandle != 0) {
// Bind the vertex shader to the program.
            GLES20.glAttachShader(programHandle, vertexShaderHandle);
// Bind the fragment shader to the program.
            GLES20.glAttachShader(programHandle, fragmentShaderHandle);
// Bind attributes
            if (attributes != null) {
                final int size = attributes.length;
                for (int i = 0; i < size; i++) {
                    GLES20.glBindAttribLocation(programHandle, i, attributes[i]);
                }
            }
// Link the two shaders together into a program.
            GLES20.glLinkProgram(programHandle);
// Get the link status.
            final int[] linkStatus = new int[1];
            GLES20.glGetProgramiv(programHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);
// If the link failed, delete the program.
            if (linkStatus[0] == 0) {
                Log.e(TAG, "Error compiling program: " + GLES20.glGetProgramInfoLog(programHandle));
                GLES20.glDeleteProgram(programHandle);
                throw new RuntimeException("Error creating program.");
                //programHandle = 0;
            }
        }
        if (programHandle == 0) {
            throw new RuntimeException("Error creating program.");
        }
        return programHandle;
    }


    private void drawLight() {

        GLES20.glUseProgram(mPointProgramHandle);
        final int pointMVPMatrixHandle = GLES20.glGetUniformLocation(mPointProgramHandle, "u_MVPMatrix");
        final int pointPositionHandle = GLES20.glGetAttribLocation(mPointProgramHandle, "a_Position");
// Pass in the position.
        GLES20.glVertexAttrib3f(pointPositionHandle, mLightPosInModelSpace[0], mLightPosInModelSpace[1], mLightPosInModelSpace[2]);
// Since we are not using a buffer object, disable vertex arrays for this attribute.
        GLES20.glDisableVertexAttribArray(pointPositionHandle);
// Pass in the transformation matrix.
        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mLightModelMatrix, 0);
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);
        GLES20.glUniformMatrix4fv(pointMVPMatrixHandle, 1, false, mMVPMatrix, 0);
// Draw the point.
        GLES20.glDrawArrays(GLES20.GL_POINTS, 0, 1);
    }
}