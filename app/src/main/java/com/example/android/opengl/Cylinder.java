package com.example.android.opengl;

import android.util.Log;

/**
 * Created by Owner on 16/11/2014.
 */
public class Cylinder {

    public final int numT = 20;          //defines number of triangles being drawn
    //  Triangle Circle1 [], Circle2 [];
    Triangle CircleT1[],CircleT2[];
    Triangle  Cbody1[], Cbody2[], BottomTapeBody1[], BottomTapeBody2[], TopTapeBody1[], TopTapeBody2[];
    private double mAngle = (2.0*Math.PI)/numT;
    private float radius = 0.4f;
    private final float y_offset = 0.4f;
    private float bottom_taper_ratio = 0.75f;
    private float bottom_taper_dist = y_offset*1.15f;
    private float top_taper_dist = y_offset*1.2f;
    private float top_taper_ratio = 0.75f;
    private float taper_size = radius * bottom_taper_ratio;
    private int taperNormalAngle;

    public Cylinder(){            //give the triangles that make circle their coordinates

        double angle = mAngle;  //use to increase the angle each time
        float Tcoords [];
        float normCoords[];
        float texCoords[];
        float x1,x2,z1,z2;
        //  Circle1 = new Triangle[numT];
        //  Circle2 = new Triangle[numT];
        CircleT1 = new Triangle[numT];       //taper circle top
        CircleT2 = new Triangle[numT];
        Cbody1 = new Triangle[numT];
        Cbody2 = new Triangle[numT];
        BottomTapeBody1 = new Triangle[numT];
        BottomTapeBody2 = new Triangle[numT];
        TopTapeBody1 = new Triangle[numT];
        TopTapeBody2 = new Triangle[numT];
        float color[] = new float[] {1.0f, 0.0f, 0.0f, 1.0f};


        z1 = radius;
        x1 = 0.0f;

        for(int x=0; x < numT; x++) {
            //add the inital coords in here
            z2 = ((float) Math.cos(angle)) * radius;     //finds the next coords
            x2 = ((float) Math.sin(angle)) * radius;

            //body coords
            /*Tcoords = new float[]{
                    x1, y_offset, z1,       //center point
                    x2, y_offset, z2,    //point 1
                    x2, -1*y_offset, z2//point 2
            };*/

             Tcoords = new float[]{
                         //center point

                    x1, y_offset, z1,
                    x1, -1*y_offset, z1,
                    x2, -1*y_offset, z2,           //point 2
            };

            normCoords = new float[]{
                    x1/radius, 0.0f, z1/radius,
                    x1/radius, 0.0f, z1/radius,
                    x2/radius, 0.0f, z2/radius
            };

            texCoords = new float[]{
                    (x*2.0f/numT), 0.0f,
                    (x*2.0f/numT), 1.0f,
                    (2.0f+x*2.0f)/numT, 1.0f
            };
            Cbody1[x] = new Triangle(Tcoords, normCoords, texCoords);

            //cbody2
            /*Tcoords = new float[]{
                    x2, -1*y_offset, z2,       //center point
                    x1, -1*y_offset, z1,           //point 1
                    x1, y_offset, z1            //point 2
            };*/
            Tcoords = new float[]{
                    x1, y_offset, z1,       //center point
                    x2, -1*y_offset, z2,    //point 1
                    x2, y_offset, z2//point 2
            };

            normCoords = new float[]{
                    x1/radius, 0.0f, z1/radius,
                    x2/radius, 0.0f, z2/radius,
                    x2/radius, 0.0f, z2/radius
            };

            texCoords = new float[]{
                    x*2.0f/numT, 0.0f,
                    (2.0f+x*2.0f)/numT, 1.0f,
                    (2.0f+x*2.0f)/numT, 0.0f
            };

            Cbody2[x] = new Triangle(Tcoords, normCoords, texCoords);

            //update coords for next triangle
            x1 = x2;
            z1 = z2;
            angle += mAngle;     //increase the angle for each triangle
        }

        //reset the circle values for the taper
        z1 = taper_size;
        x1 = 0.0f;
        angle = mAngle;

        for(int x=0; x < numT; x++) {

            z2 = ((float) Math.cos(angle)) * radius * bottom_taper_ratio;     //finds the next coords
            x2 = ((float) Math.sin(angle)) * radius * bottom_taper_ratio;

            //set the coords of the current triangle
            Tcoords = new float[]{
                    x1, -1*bottom_taper_dist, z1,
                    0.0f, -1*bottom_taper_dist, 0.0f,
                    x2, -1*bottom_taper_dist, z2
            };
            normCoords = new float[]{
                    0f, -1f, 0f,
                    0f, -1f, 0f,
                    0f, -1f, 0f
            };

            texCoords= new float[]{
                    0.56f, 0.56f,
                    0.56f, 0.56f,
                    0.56f, 0.56f
            };
            CircleT1[x] = new Triangle(Tcoords,color, normCoords, texCoords);

            Tcoords = new float[]{
                    0.0f, top_taper_dist, 0.0f,
                    x1, top_taper_dist, z1,
                    x2, top_taper_dist, z2

            };

            //Normals face the opposite direction for top disc
            normCoords[1] = 1.0f;
            normCoords[4] = 1.0f;
            normCoords[7] = 1.0f;

            CircleT2[x] = new Triangle(Tcoords,color, normCoords, texCoords);

            Tcoords = new float[]{
                    x1/bottom_taper_ratio, -1*y_offset, z1/bottom_taper_ratio,
                    x1, -1*bottom_taper_dist, z1,
                    x2, -1*bottom_taper_dist, z2,
            };



            BottomTapeBody1[x] = new Triangle(Tcoords,color);

            Tcoords = new float[]{
                    x1/bottom_taper_ratio, -1*y_offset, z1/bottom_taper_ratio,
                    x2, -1*bottom_taper_dist, z2,
                    x2/bottom_taper_ratio, -1*y_offset, z2/bottom_taper_ratio
            };

            BottomTapeBody2[x] = new Triangle(Tcoords,color);

            Tcoords = new float[]{

                    x1, top_taper_dist, z1,
                    x1/top_taper_ratio, y_offset, z1/top_taper_ratio,
                    x2, top_taper_dist, z2//point 2
            };

            TopTapeBody1[x] = new Triangle(Tcoords);

            Tcoords = new float[]{
                    x1/top_taper_ratio, y_offset, z1/top_taper_ratio,
                    x2/top_taper_ratio, y_offset, z2/top_taper_ratio,
                    x2, top_taper_dist, z2,

            };

            TopTapeBody2[x] = new Triangle(Tcoords);


            //update coords for next triangle
            x1 = x2;
            z1 = z2;
            angle += mAngle;     //increase the angle for each triangle
        }

    }

    public void draw(float [] mMVPmatrix){

        for(int i=0; i < numT; i++)             //draw each triangle
        {
            //small circles for taper
            CircleT1[i].draw(mMVPmatrix);       //bottom cicle
            CircleT2[i].draw(mMVPmatrix);       //top circle

            //body of cylinder
            Cbody1[i].draw(mMVPmatrix);
            Cbody2[i].draw(mMVPmatrix);

            //body of bottom taper
            BottomTapeBody1[i].draw(mMVPmatrix);
            BottomTapeBody2[i].draw(mMVPmatrix);

            //body of top taper
            TopTapeBody1[i].draw(mMVPmatrix);
            TopTapeBody2[i].draw(mMVPmatrix);


        }
    }
}
