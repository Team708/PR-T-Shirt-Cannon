/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.wpi.first.wpilibj.templates;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.SpeedController;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 * @author 708
 */
public class AirCannon {
    
    //objects
    private Solenoid firingSolenoid;        //release to allow cannon to fire
    private Solenoid lockingSolenoid;       //must be extended when barrel is aligned
    private DigitalInput lockingSwitch;     //true if barrel is aligned
    private SpeedController rotationMotor;  //rotates the barrel assembly
    private Timer timer;
    
    //constants
    private final double rotationSpeed = .25;
    private final double valveRelease = .1; //time (sec) that the firingSolenoid takes
                                            //to release pressure
    private final boolean kValveOpen = true;
    private final boolean kValveClose = false;
    
    private final boolean kPistonExtend = true;
    private final boolean kPistonRetract = false;
    
    private final int STATE_IDLE = 0;           //neither rotating nor firing
    private final int STATE_ROTATING = 1;        //rotating the barrels
    private final int STATE_FIRING = 2;         //firing - cannot rotate
    
    //vars
    private int state;
    private boolean manualOverride = false;
    private boolean betweenBarrels = false;
    
    public AirCannon(int firingSolenoidChan,int lockingSolenoidChan,int lockingSwitchChan,
            int motorChan)
    {
        firingSolenoid = new Solenoid(firingSolenoidChan);
        //begin in a closed position
        firingSolenoid.set(kValveClose);
        
        lockingSolenoid = new Solenoid(lockingSolenoidChan);
        
        //begin in locked configuration
        lockingSolenoid.set(kPistonExtend);
        
        lockingSwitch = new DigitalInput(lockingSwitchChan);
        rotationMotor = new Victor(motorChan);
        timer = new Timer();
        
        state = STATE_IDLE;
    }
    
    /**
     * This method should be called periodically to operate the air cannnon.
     */
    public void update()
    {
        switch(state)
        {
                //enters STATE_IDLE when ready to rotate or fire
            case STATE_IDLE:
                break;
             
                //may enter STATE_ROTATING when a barrel is aligned
                //or when between barrels
            case STATE_ROTATING:
                
                if(lockingSwitch.get())
                {
                    //either aligned with current barrel, or
                    //aligned with next barrel (which is the goal)
                    if(betweenBarrels)
                    {
                        //has passed in between barrels - goal is reached
                        
                        //stop rotating
                        rotationMotor.set(0.0);
                        
                        //NOTE: possible location for extending lock piston
//                        //extend lock piston (will fall into notch on next barrel)
//                        lockingSolenoid.set(kPistonExtend);
                    
                        //reset flag
                        betweenBarrels = false;
                        
                        //ready to fire
                        state = STATE_IDLE;
                    }else
                    {
                        //has not passed between barrels yet - still on current barrel
                        
                        //retract locking piston
                        lockingSolenoid.set(kPistonRetract);
                        
                        //begin rotating to next barrel
                        rotationMotor.set(rotationSpeed);
                    }
                }else{
                    //continue rotating
                    rotationMotor.set(rotationSpeed);
                    
                    //extend lock piston (will fall into notch on next barrel)
                    lockingSolenoid.set(kPistonExtend);
                    
                    //between barrels
                    betweenBarrels = true;
                }
                
                break;
                
                //enters STATE_FIRING when barrel is aligned
                //and about to fire
            case STATE_FIRING:
               //leave the firing valve open for a certain interval 
               timer.start();
               
               if(timer.get() <= valveRelease)
               {
                   //allow air to flow through valve
                   firingSolenoid.set(kValveOpen);
               }else{
                   //close the valve
                   firingSolenoid.set(kValveClose);
                   
                   //reset timer
                   timer.reset();
                   
                   //rotate to next barrel
                   state = STATE_ROTATING;
               }
                break;
                
            default:
                break;
        }
    }
    
    public void fire()
    {
        //make sure that barrels are not rotating and barrel is aligned
        //and we are NOT in manual override mode
        if(state == STATE_IDLE && lockingSwitch.get() && !manualOverride) state = STATE_FIRING;
    }
    
    public void rotateToNextBarrel()
    {
        if(state == STATE_IDLE) state = STATE_ROTATING;
    }
    
    public void engageManualOverride()
    {
        manualOverride = true;
        
        //retract locking piston
        lockingSolenoid.set(false);
    }
    
    public void disgageManualOverride()
    {
        manualOverride = false;
        
        //rotate to next barrel
        state = STATE_ROTATING;
    }
    
    public void setManualSpeed(double speed)
    {
        if(manualOverride)
        {
            rotationMotor.set(rotationSpeed * speed);
        }
    }
    
    public void sendToDashboard()
    {
        SmartDashboard.putBoolean("Cannon Override", manualOverride);
        SmartDashboard.putBoolean("Between Barrels", betweenBarrels);
        SmartDashboard.putDouble("Cannon Rot Speed",rotationMotor.get());
        SmartDashboard.putString("Cannon State",(state == STATE_IDLE)?"Idle":
                (state == STATE_ROTATING)?"Rotating":(state == STATE_FIRING)?"Firing":"Unknown");
        SmartDashboard.putBoolean("Lock Piston",lockingSolenoid.get());
        SmartDashboard.putBoolean("Valve", firingSolenoid.get());
        SmartDashboard.putBoolean("Cannon Switch",lockingSwitch.get());
    }
}
    