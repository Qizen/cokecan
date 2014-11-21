package com.example.android.opengl;

import android.util.Log;

/**
 * Created by Owner on 16/11/2014.
 */
public class Cylinder {

    public final int numT = 40;          //defines number of triangles being drawn
    Triangle Circle1 [], Circle2 [];
    Square  Cbody[];
    private double mAngle = (2.0*Math.PI)/numT;
    private float radius = 0.5f;

    public Cylinder(){            //give the triangles that make circle their coordinates

        double angle = mAngle;  //use to increase the angle each time
        float Tcoords [], SqCoords[];
        float x1,x2,y1,y2;
        Circle1 = new Triangle[numT];
        Circle2 = new Triangle[numT];
        Cbody = new Square[numT];
        float color[] = new float[] {0.3f, 0.5f, 0.44f, 0.11f};


        x1 = radius;
        y1 = 0.0f;

        for(int x=0; x < numT; x++) {
            //add the inital coords in here
            x2 = ((float) Math.cos(angle)) * radius;     //finds the next coords
            y2 = ((float) Math.sin(angle)) * radius;

            //set the coords of the current triangle
            Tcoords = new float[]{
                    0.0f, 0.0f, 0.0f,       //center point
                    x1, y1, 0.0f,           //point 1
                    x2, y2, 0.0f            //point 2
            };

                Circle1[x] = new Triangle(Tcoords,color);             //puts the initial coords in the triagle


            //change z coords
            Tcoords[2] = 1.0f;
            Tcoords[5] = 1.0f;
            Tcoords[8] = 1.0f;

            Circle2[x] = new Triangle(Tcoords);

            //get the body of the cylinder coords
            SqCoords = new float[]{
                    x1, y1, 0.0f,   // circle 1 connection
                    x2, y2, 0.0f,
                    x1, y1, Tcoords[2],   //cirlce 2 connection
                    x2, y2, Tcoords[2]};

            Cbody[x] = new Square(SqCoords);

            //update coords for next triangle
            x1 = x2;
            y1 = y2;
            angle += mAngle;     //increase the angle for each triangle
        }


    }

    public void draw(float [] mMVPmatrix){

        for(int i=0; i < numT; i++)             //draw each triangle
        {
            Circle1[i].draw(mMVPmatrix);
           // Circle2[i].draw(mMVPmatrix);
            //Cbody[i].draw(mMVPmatrix);
        }
    }
}
