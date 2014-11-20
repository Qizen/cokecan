package com.example.android.opengl;

import android.util.Log;

/**
 * Created by Owner on 16/11/2014.
 */
public class Circle {

    public final int numT = 40;          //defines number of triangles being drawn
    Triangle myTriangles [];
    private double mAngle = (2.0*Math.PI)/numT;
    private float radius = 0.5f;

    public Circle(){            //give the triangles that make circle their coordinates

        double angle = mAngle;  //use to increase the angle each time
        float coords [] = new float[9];
        float x1,x2,y1,y2;
        myTriangles = new Triangle[numT];
        //Triangle tempT;


        x1 = radius;
        y1 = 0.0f;

        for(int x=0; x < numT; x++)
        {
            //add the inital coords in here
            x2 = ((float)Math.cos(angle)) * radius;     //finds the next coords
            y2 = ((float)Math.sin(angle)) * radius;

            //set the coords of the current triangle
            coords =  new float []{
                    0.0f, 0.0f, 0.0f,       //center point
                    x1, y1, 0.0f,           //point 1
                    x2, y2, 0.0f            //point 2
            };

           // tempT = new Triangle(coords);
            myTriangles[x] = new Triangle(coords);             //puts the initial coords in the triagle

            //update coords for next triangle
            x1 = x2;
            y1 = y2;
            angle += mAngle;     //increase the angle for each triangle
        }


    }

    public void draw(float [] mMVPmatrix){

        for(int i=0; i < numT; i++)             //draw each triangle
            myTriangles[i].draw(mMVPmatrix);
    }
}
