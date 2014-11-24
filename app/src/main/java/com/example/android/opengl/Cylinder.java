package com.example.android.opengl;

import android.util.Log;

/**
 * Created by Owner on 16/11/2014.
 */
public class Cylinder {

    public final int numT = 40;          //defines number of triangles being drawn
    public final int numsqT = numT * 2;  //need 2 triangles for body where every
    Triangle Circle1 [], Circle2 [];
    Triangle  Cbody[];
    private double mAngle = (2.0*Math.PI)/numT;
    private float radius = 0.5f;
    private float z_offset = 1.0f;

    public Cylinder(){            //give the triangles that make circle their coordinates

        double angle = mAngle;  //use to increase the angle each time
        float Tcoords [], SqCoords[];
        float x1,x2,y1,y2;
        Circle1 = new Triangle[numT];
        Circle2 = new Triangle[numT];
        Cbody = new Triangle[numsqT];
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
            Circle1[x] = new Triangle(Tcoords);

            //change z coords
            Tcoords[2] = z_offset;
            Tcoords[5] = z_offset;
            Tcoords[8] = z_offset;

            Circle2[x] = new Triangle(Tcoords);

            //update coords for next triangle
            x1 = x2;
            y1 = y2;
            angle += mAngle;     //increase the angle for each triangle
        }

        x1 = radius;
        y1 = 0.0f;
        angle = mAngle;

        for(int y=0; y < numsqT; y += 2) {

            x2 = ((float) Math.cos(angle)) * radius;     //finds the next coords
            y2 = ((float) Math.sin(angle)) * radius;

            Tcoords = new float[]{
                    x1, y1, 0.0f,       //center point
                    x1, y1, z_offset,           //point 1
                    x2, y2, z_offset            //point 2
            };

            Cbody[y] = new Triangle(Tcoords);

            Tcoords = new float[]{
                    x1, y1, 0.0f,
                    x2, y2, 0.0f,           //point 1
                    x2, y2, z_offset            //point 2
            };

            Cbody[y+1] = new Triangle(Tcoords);

            //update coords for next triangle
            x1 = x2;
            y1 = y2;
            angle += mAngle;     //increase the angle for each triangle
        }


    }

    public void draw(float [] mMVPmatrix){

        for(int i=0; i < numT; i++)             //draw each triangle
        {
            Circle2[i].draw(mMVPmatrix);
            Cbody[i].draw(mMVPmatrix);
            Circle1[i].draw(mMVPmatrix);

        }
    }
}
