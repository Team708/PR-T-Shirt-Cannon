/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.AnalogChannel;

/**
 * Implementation of a potentiometer that is being used
 * to read a value in degrees or radians.
 * @author Connor
 */
public class HeadingPotentiometer extends AnalogChannel{
    
    private double lowVlts,rangeVlts,lowDegs,rangeDegs;

    public HeadingPotentiometer(double lowVlts, double highVlts,
            double lowDegs, double highDegs, int channel) {
        super(channel);
        this.lowVlts = lowVlts;
        rangeVlts = highVlts - lowVlts;
        this.lowDegs = lowDegs;
        rangeDegs = highDegs - lowDegs;
    }
    
    /**
     * Returns the current angle of the potentiometer,
     * measured in degrees.
     * @return 
     */
    public double getAngleDeg()
    {
        return (getVoltage() - lowVlts)/rangeVlts * rangeDegs + lowDegs;
    }
    
    /**
     * Returns the current angle of the potentiometer,
     * measured in radians.
     * @return 
     */
    public double getAngleRads()
    {
        return Math.toRadians(getAngleDeg());
    }
}
