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

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    public final MyGLRenderer mRenderer;
    private float distance = 0;


    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {


        if(e != null) {
            // MotionEvent reports input details from the touch screen
            // and other input controls. In this case, you are only
            // interested in events where the touch position changed.

            float x = e.getX();
            float y = e.getY();
            float newDist = 0;

            switch (e.getAction()) {
                case MotionEvent.ACTION_MOVE:

                    if (e.getPointerCount()==1) {
                        float dx = x - mPreviousX;
                        float dy = y - mPreviousY;

                        mRenderer.setX(mRenderer.getmX() - dx);
                        mRenderer.setY(mRenderer.getmY() - dy);
                    }

                    // pinch to zoom
                    if (e.getPointerCount() == 2)
                    {

                        if (distance == 0)
                        {
                            distance = fingerDist(e);
                        }
                        newDist = fingerDist(e);
                        float d = distance / newDist;
                        mRenderer.zoom(d);
                        distance = newDist;
                    }
                    requestRender();
            }

            distance = newDist;
            mPreviousX = x;
            mPreviousY = y;
            return true;
        }
        else
        {
            return super.onTouchEvent(e);
        }

    }
    protected final float fingerDist(MotionEvent event)
    {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

}