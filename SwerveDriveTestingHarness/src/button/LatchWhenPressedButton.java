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
 * @author Carl Burger - Team 708
 */
public class LatchWhenPressedButton extends Button {

    // Change state on a button press and retain the new value until the button
    // is read.  If the button is pressed and released before it is read, then
    // the press is ignored.  Since the code reads the button at 50 msec
    // intervals, this approach should not miss any button presses.  Once read,
    // the next time the button is read, it will return false regardless of
    // whether the button is still pressed.  The button must be released and
    // pressed again for it to return true again.

    public LatchWhenPressedButton(Joystick stick, int button, String name) {
        super(stick, button, name);
    }

    public LatchWhenPressedButton(Joystick stick, int buttonType, int button, String name) {
        super(stick,buttonType,button,name);
    }



    public LatchWhenPressedButton(DriverStationEnhancedIO station, int channel, String name) {
        super(station, channel, name);
    }

    public LatchWhenPressedButton(DriverStationEnhancedIO station, int modifierChannel, boolean modStateWhenPressed,
                                  int channel, String name) {
        super(station, modifierChannel, modStateWhenPressed, channel, name);
    }

    public LatchWhenPressedButton(DigitalInput dInput) {
        super(dInput);
    }

    public boolean getButtonState() {

        /* last  curr  this
           state state return  condition
           ----- ----- ------  ---------
           F     F     F       initial condition / button released
           F     T     T       button pressed
           T     T     F       button held
           T     F     F       button released
         *
         */

        currState = isButtonPressed();

        if ( (currState != lastState) && (currState == true) )
        {
            thisReturn = true;
        }
        else
        {
            thisReturn = false;
        }

        lastState = currState;

        return thisReturn;
    }
}