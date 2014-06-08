/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package button;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO.EnhancedIOException;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Mentors Carl Burger - Team 708
 */
public abstract class Button {

    // The button may be a:
    //    joystick button
    //    driver station digital input
    //    driver station analog input
    //
    //    robot digital input (to take advantage of button behaviors)
    //
    // Channels 1-16 are digital inputs
    // Channels 17-24 are analog inputs on the driver station enhanced I/O.
    // For analog inputs, subtract 16 to get the actual analog channel 1-8.
    // For analog input, >= 4 volts is considered NOT pressed
    //                   <= 2 volts is considered pressed

    protected final static int    kMaxDigitalInput           = 16;
    protected final static double kPressedAnalogThreshold    = 2; // <= 2 volts
    protected final static double kNotPressedAnalogThreshold = 4; // >= 4 volts

    protected int       buttonType;
    public static final int TYPE_JOYSTICK = 0;
    public static final int TYPE_DRIVERSTATION = 1;
    public static final int TYPE_ROBOT = 2;
    public static final int TYPE_JOYSTICK_AXIS_POSITIVE = 3;
    public static final int TYPE_JOYSTICK_AXIS_NEGATIVE = 4;
    
    protected boolean       modifierStateWhenPressed;
    protected boolean       dualMode;
    protected Joystick      stick;
    protected DriverStationEnhancedIO station;
    protected DigitalInput dInput;
    protected int           inputToRead; // joystick button or driver station digital input
    protected int           modifierInput; //driver station input that switches in between two sets of button meanings
    protected String        name;

    protected boolean lastState = false; //last state of the raw button
    protected boolean currState;
    protected boolean lastReturn = false; //last state returned with getButtonState
    protected boolean thisReturn;

    protected Button(Joystick stick, int button, String name) {

        buttonType = TYPE_JOYSTICK;
        dualMode = false;
        this.stick = stick;
        inputToRead = button;
        this.name = name;

    }

    protected Button(Joystick stick, int buttonType, int button, String name){
        this.buttonType = buttonType;
        dualMode = false;
        this.stick = stick;
        inputToRead = button;
        this.name = name;
    }

    protected Button(DriverStationEnhancedIO station, int channel, String name) {

        buttonType = TYPE_DRIVERSTATION;
        dualMode = false;
        this.station = station;
        inputToRead = channel;
        this.name = name;

    }

    protected Button(DriverStationEnhancedIO station, int modifierChannel, boolean modStateWhenPressed, int signalChannel, String name) {

        buttonType = TYPE_DRIVERSTATION;
        dualMode = true;
        modifierStateWhenPressed = modStateWhenPressed;
        this.station = station;
        modifierInput = modifierChannel;
        inputToRead = signalChannel;
        this.name = name;

    }
    
    protected Button(DigitalInput dInput){
        this.dInput= dInput;
        buttonType = TYPE_ROBOT;
    }
    

    boolean isButtonPressed() {

        boolean isPressed = false;

        try {
            if (buttonType == TYPE_JOYSTICK)
            {
                isPressed = stick.getRawButton(inputToRead);
            }
            else if(buttonType == TYPE_DRIVERSTATION)
            {
                if (dualMode == true)
                {
                    if (station.getDigital(modifierInput) == modifierStateWhenPressed)
                        isPressed = !station.getDigital(inputToRead);
                    else
                        isPressed = false;

                } else {
                    
                    if (inputToRead > kMaxDigitalInput) {
                        double voltage = station.getAnalogIn(inputToRead-kMaxDigitalInput);
                        if (voltage >= kNotPressedAnalogThreshold) {
                            isPressed = false;

                        } else if (voltage <= kPressedAnalogThreshold) {
                            isPressed = true;
                        }
                    } else {
                        isPressed = !station.getDigital(inputToRead);
                    }
                }
            }else if(buttonType == TYPE_ROBOT){
                isPressed = dInput.get();
            } else if(buttonType == TYPE_JOYSTICK_AXIS_POSITIVE){
                isPressed = stick.getRawAxis(inputToRead) > 0;
            }else if(buttonType == TYPE_JOYSTICK_AXIS_NEGATIVE){
                isPressed = stick.getRawAxis(inputToRead) < 0;
            }else{
                //should never get here
                isPressed = false;
            }
        }

        catch (EnhancedIOException e)
        {
            //System.out.println("DriverStationEnhancedIO " + e.getMessage());
        }

        //if(isPressed) System.out.println(name + " pressed");  //*** FOR DEBUGGING ***

        return isPressed;
    }

    abstract boolean getButtonState();

}