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
public class SwitchUntilReleasedButton extends Button {

    public SwitchUntilReleasedButton(DigitalInput dInput) {
        super(dInput);
    }

    // Changes the control value when the button is pressed and retains the new
    // value until the button is released. When released, the control reverts to
    // its default value, similar to the operation of a door buzzer.
    // The frequency with which the button is read does not affect this behavior.

    public SwitchUntilReleasedButton(Joystick stick, int button, String name) {
        super(stick, button, name);
    }

    public SwitchUntilReleasedButton(Joystick stick, int buttonType, int button, String name) {
            super(stick,buttonType,button,name);
    }
    
    public SwitchUntilReleasedButton(DriverStationEnhancedIO station, int channel, String name) {
        super(station, channel, name);
    }

    public boolean getButtonState() {
        return isButtonPressed();
    }
}