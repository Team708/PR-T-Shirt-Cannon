/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import com.sun.squawk.util.MathUtils;

/**
 * Miscellaneous math functions.
 * @author 708
 */
public class Math708 {
    
    /**
     * Used to round a floating point value to
     * a given decimal place.
     * @param n The floating point value.
     * @param dig The decimal place to round to (use negative
     * numbers for places > 0.
     * @return 
     */
    public static double round(double n,int dig)
    {
        double tens = MathUtils.pow(10,dig);
        return ((int)((n * tens) + .5)) / tens;
    }
    
    /**
     * Returns the length of a vector with the specified
     * components.
     * @param x
     * @param y
     * @return 
     */
    public static double length(double x,double y)
    {
        return Math.sqrt(lengthSquared(x,y));
    }
    
    public static double lengthSquared(double x,double y)
    {
        return x * x + y * y;
    }
}
