/*----------------------------------------------------------------------------*/
/* Copyright (c) FIRST 2008. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/
package edu.wpi.first.wpilibj.templates;

import button.LatchWhenPressedButton;
import button.SwitchWhenPressedButton;
import drivetrain.RobotDriveSwerve;
import drivetrain.SwerveWheel;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class RobotTemplate extends IterativeRobot {

    //objects
    private Gamepad gamepad;
    private SwerveWheel wheel1, wheel2, wheel3, wheel4;
    private RobotDriveSwerve robotDrive;
    private SwitchWhenPressedButton toggleCannonMode;
    private SwitchWhenPressedButton toggleRelStr;
    private LatchWhenPressedButton fireCannon;
    private LatchWhenPressedButton nextBarrel;
    private LatchWhenPressedButton resetGyro;
    private Gyro gyro;
    private AirCannon cannon;
    
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
    
    //vars
    private double rsx = 0.0; //right stick x
    private double lsx = 0.0; //left stick x
    private double lsy = 0.0; //left stick y
    private double stickFactor = .4;

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
        
        cannon = new AirCannon(Constants.SolenoidChannels.kCannonFiringSolenoid,
                Constants.SolenoidChannels.kCannonLockingSolenoid,
                Constants.DigitalIOChannels.kCannonLockingSwitch,
                Constants.PWMChannels.kCannonMotor);
        
        robotDrive = new RobotDriveSwerve(wheel1,wheel2,wheel3,wheel4,gyro);
        gamepad = new Gamepad(1);
        toggleCannonMode = new SwitchWhenPressedButton(gamepad,Gamepad.button_Back,"Toggle Manual Cannon Mode");
        toggleRelStr = new SwitchWhenPressedButton(gamepad,Gamepad.button_Start,"RelStr");
        resetGyro = new LatchWhenPressedButton(gamepad,Gamepad.button_R_Shoulder,"Reset Gyro");
        fireCannon = new LatchWhenPressedButton(gamepad,Gamepad.button_L_Shoulder,"Fire");
        nextBarrel = new LatchWhenPressedButton(gamepad,Gamepad.button_A,"Rotate One Barrel Forward");
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
        
        //read input from the gamepad
        lsx = stickFactor * gamepad.getAxis(Gamepad.leftStick_X);
        rsx = stickFactor * -gamepad.getAxis(Gamepad.rightStick_X);
        lsy = stickFactor * gamepad.getAxis(Gamepad.leftStick_Y);
        
        
        /*
         * Begin drivetrain control
         */
        //move in/out of relative steering mode
        robotDrive.setRelStrMode(toggleRelStr.getButtonState());

        //reset gyro 
        if(resetGyro.getButtonState()) robotDrive.resetGyro();

        //robotDrive.swerveDrive(lsx/root2,lsy/root2,rsx);
        robotDrive.swerveDrive(lsx,lsy,rsx);
            
       /*
        * Begin air cannon control
        */
        //toggle cannon modes (manual or automatic)
        if(toggleCannonMode.getButtonState()) {
            cannon.engageManualOverride();
            
            //control speed of rotation
            cannon.setManualSpeed(gamepad.getAxis(Gamepad.shoulderAxis));
        }else cannon.disgageManualOverride();
        
        //manually rotate one barrel ahead
        if(nextBarrel.getButtonState()) cannon.rotateToNextBarrel();
        
        //fire the cannon
        if(fireCannon.getButtonState()) cannon.fire();
            
       /*
        * Begin data output
        */
        gamepad.sendAxesToDashboard();
        cannon.sendToDashboard();
        wheel1.sendToDashboard("FR");
        wheel2.sendToDashboard("FL");
        wheel3.sendToDashboard("RL");
        wheel4.sendToDashboard("RR");
        SmartDashboard.putBoolean("Relative Steering",robotDrive.isRelStrMode());
        SmartDashboard.putDouble("Gyro angle",Math708.round(robotDrive.getGyroAngle(),2));
    }
}
