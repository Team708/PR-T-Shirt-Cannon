/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package drivetrain;

import com.sun.squawk.util.MathUtils;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.templates.Math708;

/**
 * This class represents a swerve drive wheel, which
 * is powered independently and can rotate 180 degrees.
 * The wheel's logical heading is as calculated by the
 * atan2() function and ranges from -180 - 180.
 * positive direction denotes counter clockwise rotation,
 * negative direction denotes clockwise rotation.
 * 
 * NOTE:
 * The wheel can only physically turn 180 degrees.
 * The remaining 180 degrees of movement is
 * accomplished through reversing the 
 * direction of the motor. This logic
 * must be implemented or else the robot
 * will tangle its power cables by
 * over-rotating the wheels. Another
 * failsafe involves waiting until the
 * wheel headings are on target because premature
 * movement could result in damage to
 * the drivetrain.
 * 
 * FUTURE ADDITIONS:
 * The Encoder can be used for measuring distance as
 * well as speed. Use it to allow for more accurate
 * swerve drive.
 * 
 * @author Connor
 */
public class SwerveWheel {
    
    //objects
    private Relay headingMotor;
    private SpeedController speedMotor;
    
    private HeadingPotentiometer pot;
    private Encoder encoder; //may replace with pid encoder to measure rpm
    
    //constants
    
    /*
     * These values must be verified:
     * headingLower - decreases the pot's value (degrees)
     * headingHigher - increases the pot's value (degrees)
     */
    private Relay.Value headingHigher;
    private Relay.Value headingLower;
    
    /*
     * This is the tolerance value for rotation - the wheel
     * will stop rotating when the heading is within "epsilon"
     * degrees of the goal heading.
     */
    private final double headingEpsilonDeg = 13.0; //must tune through testing
    
    //variables
    private double currentHeadingDeg = 0.0;
    private double error = 0.0;
    private double goalHeadingDeg = 0.0;
    private double wheelSpeedPWM = 0.0;
    private boolean reverseDirection = false;
    private boolean onTarget = false;
    private boolean invertedWheel = false;
    
    public SwerveWheel(int relayChannel,int speedChannel,int potChannel,
            int encoderAChannel,int encoderBChannel,boolean reverseEnc,
            double potLowVlts,double potHighVlts,Relay.Value headingLower,Relay.Value headingHigher,
            boolean invertedWheel)
    {
        headingMotor = new Relay(relayChannel);
        speedMotor = new Victor(speedChannel);
        pot = new HeadingPotentiometer(potLowVlts,potHighVlts,0,180,potChannel);
        encoder = new Encoder(encoderAChannel,encoderBChannel,reverseEnc);
        this.headingLower = headingLower;
        this.headingHigher = headingHigher;
        this.invertedWheel = invertedWheel;
    }
    
    /**
     * Call periodically to keep the swerve wheel's heading on
     * target. Return value indicates whether or not the wheel's heading
     * is on target.
     */
    public boolean update()
    {
        //decide whether a wheel reversal is necessary: -180 <= x < 0
        reverseDirection = goalHeadingDeg < 0;
        
        currentHeadingDeg = getAdjustedAngle();
        
        //calculate error
        error = currentHeadingDeg - goalHeadingDeg;
        
        //seek goal heading
        if(error > headingEpsilonDeg)
        {
            //redundant bounds check using raw pot value
            //so that robot doesn't destroy itself
            if(pot.getAngleDeg() >= -90)
            {
                //heading needs to decrease
                headingMotor.set(headingLower);
                onTarget = false;
            }else{
                headingMotor.set(Relay.Value.kOff);
            }
            
        }else if(error < -headingEpsilonDeg)
        {
            if(pot.getAngleDeg() <= 270)
            {
                //heading needs to increase
                headingMotor.set(headingHigher);
                onTarget = false;
            }else{
                headingMotor.set(Relay.Value.kOff);
            }
        }else
        {
            //heading is on target
            headingMotor.set(Relay.Value.kOff);
            onTarget = true;
        }
        
        if(onTarget)
        {
            //send power to wheel - heading is on target
            if(reverseDirection)
                setPWM(-wheelSpeedPWM);
            else 
                setPWM(wheelSpeedPWM);
        }else
        {
            //do not spin wheels until heading is on target
            setPWM(0.0);
        }
        
        return onTarget;
    }
    
    /**
     * Set the heading to which the wheel will rotate,
     * as well as the speed once it reaches the goal
     * heading.
     * @param headingDeg -180 - 180
     * @param speedPWM -1.0 - 1.0
     */
    public void set(double speedPWM,double headingDeg)
    {
        wheelSpeedPWM = speedPWM;
        goalHeadingDeg = headingDeg;
        
        //validate heading - find coterminal angle for OOB parameters
        while(goalHeadingDeg > 180) goalHeadingDeg -= 360;
        while(goalHeadingDeg < -180) goalHeadingDeg += 360;
    }
    
    /**
     * Sets the wheel's orientation in vector form.
     * @param xcomp - the x component of the wheel vector.
     * @param ycomp - the y component of the wheel vector.
     */
    public void setVector(double xcomp,double ycomp)
    {
        set(Math708.length(xcomp, ycomp),Math.toDegrees(MathUtils.atan2(ycomp,xcomp)));
    }
    
    /*
     * The following three methods are used to control
     * the rotation of the wheel manually (without the update
     * method).
     */
    
    public void decreaseHeading()
    {
        headingMotor.set(headingLower);
    }
    
    public void increaseHeading()
    {
        headingMotor.set(headingHigher);
    }
    
    public void stopRotation()
    {
        headingMotor.set(Relay.Value.kOff);
    }
    
    /**
     * Manually adjusts the drive motor speed.
     * @param speed 
     */
    public void setPWM(double speed)
    {
        speedMotor.set((invertedWheel)?-speed:speed);
    }
    
    /**
     * Returns the HeadingPotentiometer used to
     * read the heading of this wheel.
     * @return 
     */
    public HeadingPotentiometer getPot()
    {
        return pot;
    }

    /**
     * Returns the Encoder object used to read the
     * speed/distance traveled by this wheel.
     * @return 
     */
    
    public Encoder getEncoder()
    {
        return encoder;
    }
    
    /**
     * Returns the angle of this wheel (-180 to 180),
     * as used as input in the update method.
     * @return 
     */
    public double getAdjustedAngle()
    {
        if(reverseDirection)
        {
            /*
             * Use pot-180 for input angle since reversal of wheel
             * concerns negative side of unit circle.
             */
            return pot.getAngleDeg() - 180;
        }else
        {
            return pot.getAngleDeg();
        }
    }
    
    /**
     * Returns the underlying SpeedController object.
     * @return 
     */
    public SpeedController getMotor()
    {
        return speedMotor;
    }
    
    public void reset()
    {
        goalHeadingDeg = 0.0;
        reverseDirection = false;
        wheelSpeedPWM = 0.0;
    }
    
    public void sendToDashboard(String name)
    {
        SmartDashboard.putDouble(name + " pot angle",Math708.round(pot.getAngleDeg(),2));
        SmartDashboard.putDouble(name + " adjusted angle",Math708.round(getAdjustedAngle(),2));
        SmartDashboard.putDouble(name + " pot vlts",Math708.round(pot.getVoltage(),2));
        SmartDashboard.putDouble(name + " speed",Math708.round(wheelSpeedPWM,2));
        SmartDashboard.putDouble(name + " goal angle",Math708.round(goalHeadingDeg,2));
    }
}
