/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package button;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStationEnhancedIO;
import edu.wpi.first.wpilibj.Joystick;

/**
 *
 * @author Mentors Carl Burger - Team 708
 */
public class SwitchWhenPressedButton extends Button {

    public SwitchWhenPressedButton(DigitalInput dInput) {
        super(dInput);
    }

    // Changes the control value each time the button is pressed
    // The frequency with which the button is read does not affect this behavior

    public SwitchWhenPressedButton(Joystick stick, int button, String name) {
        super(stick, button, name);
    }

    public SwitchWhenPressedButton(Joystick stick, int buttonType, int button, String name) {
        super(stick,buttonType,button,name);
    }



    public SwitchWhenPressedButton(DriverStationEnhancedIO station, int channel, String name) {
        super(station, channel, name);
    }

    public SwitchWhenPressedButton(Joystick stick, int button, String name, boolean initialState) {
        super(stick, button, name);
        lastReturn = initialState;
    }

    public SwitchWhenPressedButton(DriverStationEnhancedIO station, int channel, String name, boolean initialState) {
        super(station, channel, name);
        lastReturn = initialState;
    }

    public boolean getButtonState() {

        /* last  curr  last    this
           state state return  return  condition
           ----- ----- ------  ------  ---------
           F     F     F       F       initial condition
           F     T     F       T       button pressed
           T     T     T       T       button held
           T     F     T       T       button released
           F     F     T       T       button still released
           F     T     T       F       button pressed
           T     T     F       F       button still held
           T     F     F       F       button released
         *
         */

        currState = isButtonPressed();

        if ( (currState != lastState) && (currState == true) )
        {
            thisReturn = !lastReturn;
        }
        else
        {
            thisReturn = lastReturn;
        }

        lastReturn = thisReturn;
        lastState = currState;

        return thisReturn;
    }
}