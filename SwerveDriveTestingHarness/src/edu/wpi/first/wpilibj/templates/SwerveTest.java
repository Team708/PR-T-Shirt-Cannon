/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import button.LatchWhenPressedButton;
import button.SwitchWhenPressedButton;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.networktables.NetworkTableKeyNotDefined;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class SwerveTest extends IterativeRobot {

    //objects
    private Gamepad gamepad;
    private SwerveWheel wheel1, wheel2, wheel3, wheel4;
    private RobotDriveSwerve robotDrive;
    private LatchWhenPressedButton switchModes;
    private SwitchWhenPressedButton toggleRelStr;
    private LatchWhenPressedButton resetGyro;
    private Gyro gyro;
    
    //constants
    
    
    /*
     * The testing harness has three modes:
     * 1) Manual Mode
     *  Allows manual control of swerve wheels with the gamepad's
     * left stick x axis controlling rotation of wheel, the right stick
     * x axis controlling speed of wheel, and the a,b,x,y buttons allowing
     * selection of which wheel(s) to control.
     * 
     * 2) Vector Mode
     *  Treats the input from the joystick as a vector. The x component
     * of the vector is the left stick x axis and the y component is the
     * left stick y axis.
     * 
     * 3) Swerve Mode
     *  Uses the final swerve drive algorithm to control all 4 wheels.
     * The left stick is used as the robot's translational vector while the
     * right stick x axis is used as the angular velocity.
     */
    private final int MANUAL_MODE = 0;
    private final int VECTOR_MODE = 1;
    private final int SWERVE_MODE = 2;
    
    //vars
    private double rsx = 0.0; //right stick x
    private double lsx = 0.0; //left stick x
    private double lsy = 0.0; //left stick y
    private String selected;
    private boolean buttonsPressed = false;
    private double stickFactor = .4;

    private int inputMode = MANUAL_MODE;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {
    
        wheel1 = new SwerveWheel(Constants.RelayChannels.kFrontRightSpike,
                Constants.PWMChannels.kFrontRightCIM,
                Constants.AnalogInputChannels.kFrontRightPot,
                Constants.DigitalIOChannels.kFrontRightEncoderA,
                Constants.DigitalIOChannels.kFrontRightEncoderB,
                false, Constants.Calibrations.kFrontRightPotLowVlts,
                Constants.Calibrations.kFrontRightPotHighVlts,
                Constants.Calibrations.kFrontRightHeadingLower,
                Constants.Calibrations.kFrontRightHeadingHigher,false);

        wheel2 = new SwerveWheel(Constants.RelayChannels.kFrontLeftSpike,
                Constants.PWMChannels.kFrontLeftCIM,
                Constants.AnalogInputChannels.kFrontLeftPot,
                Constants.DigitalIOChannels.kFrontLeftEncoderA,
                Constants.DigitalIOChannels.kFrontLeftEncoderB,
                false, Constants.Calibrations.kFrontLeftPotLowVlts,
                Constants.Calibrations.kFrontLeftPotHighVlts,
                Constants.Calibrations.kFrontLeftHeadingLower,
                Constants.Calibrations.kFrontLeftHeadingHigher,true);

        wheel3 = new SwerveWheel(Constants.RelayChannels.kRearLeftSpike,
                Constants.PWMChannels.kRearLeftCIM,
                Constants.AnalogInputChannels.kRearLeftPot,
                Constants.DigitalIOChannels.kRearLeftEncoderA,
                Constants.DigitalIOChannels.kRearLeftEncoderB,
                false, Constants.Calibrations.kRearLeftPotLowVlts,
                Constants.Calibrations.kRearLeftPotHighVlts,
                Constants.Calibrations.kRearLeftHeadingLower,
                Constants.Calibrations.kRearLeftHeadingHigher,true);

        wheel4 = new SwerveWheel(Constants.RelayChannels.kRearRightSpike,
                Constants.PWMChannels.kRearRightCIM,
                Constants.AnalogInputChannels.kRearRightPot,
                Constants.DigitalIOChannels.kRearRightEncoderA,
                Constants.DigitalIOChannels.kRearRightEncoderB,
                false, Constants.Calibrations.kRearRightPotLowVlts,
                Constants.Calibrations.kRearRightPotHighVlts,
                Constants.Calibrations.kRearRightHeadingLower,
                Constants.Calibrations.kRearRightHeadingHigher,false);
        
        gyro = new Gyro(Constants.AnalogInputChannels.kGyro);
        
        robotDrive = new RobotDriveSwerve(wheel1,wheel2,wheel3,wheel4,gyro);
        gamepad = new Gamepad(1);
        switchModes = new LatchWhenPressedButton(gamepad,Gamepad.button_Back,"SwitchModes");
        toggleRelStr = new SwitchWhenPressedButton(gamepad,Gamepad.button_Start,"RelStr");
        resetGyro = new LatchWhenPressedButton(gamepad,Gamepad.button_R_Shoulder,"Reset Gyro");
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
        //read input from the gamepad
        lsx = stickFactor * gamepad.getAxis(Gamepad.leftStick_X);
        rsx = stickFactor * -gamepad.getAxis(Gamepad.rightStick_X);
        lsy = stickFactor * gamepad.getAxis(Gamepad.leftStick_Y);
        selected = "";
        
        //cycle to next mode if button is pressed once
        if(switchModes.getButtonState())
        {
            inputMode++;
            inputMode %= 3;
            wheel1.reset();
            wheel2.reset();
            wheel3.reset();
            wheel4.reset();
        }
        
        //move in/out of relative steering mode
        robotDrive.setRelStrMode(toggleRelStr.getButtonState());
        
        //reset gyro 
        if(resetGyro.getButtonState()) robotDrive.resetGyro();
        
        switch(inputMode)
        {
            case MANUAL_MODE:
            case VECTOR_MODE:
                buttonsPressed = false;
                if (gamepad.getButton(Gamepad.button_A)) {
                    buttonsPressed = true;
                    controlWheel(wheel1,"FR ");
                }

                if (gamepad.getButton(Gamepad.button_X)) {
                    controlWheel(wheel2,"FL ");
                     buttonsPressed = true;
                }

                if (gamepad.getButton(Gamepad.button_Y)) {
                    controlWheel(wheel3,"RL ");
                     buttonsPressed = true;
                }

                if (gamepad.getButton(Gamepad.button_B)) {
                    controlWheel(wheel4,"RR ");
                     buttonsPressed = true;
                }
                SmartDashboard.putString("Selected Wheel(s)",selected);
            break; 
            
            case SWERVE_MODE:
                
                //robotDrive.swerveDrive(lsx/root2,lsy/root2,rsx);
                robotDrive.swerveDrive(lsx,lsy,rsx);
            break;
                
                
                
        }
        
        if(!buttonsPressed)
        {
            wheel1.reset();
            wheel2.reset();
            wheel3.reset();
            wheel4.reset();
        }
                
        //output data
        gamepad.sendAxesToDashboard();
        wheel1.sendToDashboard("FR");
        wheel2.sendToDashboard("FL");
        wheel3.sendToDashboard("RL");
        wheel4.sendToDashboard("RR");
        sendInputMode();
        SmartDashboard.putBoolean("Relative Steering",robotDrive.isRelStrMode());
        SmartDashboard.putDouble("Gyro angle",Math708.round(robotDrive.getGyroAngle(),2));
    }
    
    private void controlWheel(SwerveWheel w,String wheelName)
    {
        switch(inputMode)
        {
            case MANUAL_MODE:
                //use gamepad input to directly adjust speed/rotation

                //rotation
                if (lsx > 0.0) {
                    w.increaseHeading();
                } else if (lsx < 0.0) {
                    w.decreaseHeading();
                } else {
                    w.stopRotation();
                }

                //speed
                w.setPWM(rsx);
                break;
            case VECTOR_MODE:
                //treat gamepad input as a vector

                /*
                * Explanation of root 2:
                * Before the vector can be used, its length needs to be
                * brought between -1.0 and 1.0 since it will be used to
                * control the PWM value of the wheel. The maximum length 
                * of a vector read from the gamepad is the square root of 2
                * (a^2 + b^2 = c^2), so dividing by this number does
                * the trick.
                */
                w.setVector(lsx,lsy);

                //adjust speed/heading of wheel to match the vector
                w.update();
                break;
                
            default:
                //should not be called for swerve mode
                break;
        }
        
        //add name of wheel to "selected" label
        selected += wheelName;
    }
    
    private void sendInputMode()
    {
        String name;
        switch(inputMode)
        {
            case MANUAL_MODE:
                name = "Manual";
                break;
            case VECTOR_MODE:
                name = "Vector";
                break;
            case SWERVE_MODE:
                name = "Swerve";
                break;
            default:
                name = "";
                break;
        }
        
        SmartDashboard.putString("Input Mode",name);
    }
}
