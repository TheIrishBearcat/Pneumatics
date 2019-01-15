package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Compressor;

public class Pneumatics {

    DoubleSolenoid kickOut, takeIn;
    Compressor mCompressor;
    XboxController xBox;

    public Pneumatics() {
        kickOut = new DoubleSolenoid(1, 2, 3);
        takeIn = new DoubleSolenoid(0, 0, 1);
        mCompressor = new Compressor(Consts.compressorPort);
        xBox = new XboxController(Consts.xBoxPort);
    }

    public void ballKickOut() {
        kickOut.set(DoubleSolenoid.Value.kForward);
    }

    public void ballKickOutSpeedSet() {
        double speedR = xBox.getTriggerAxis(GenericHID.Hand.kRight);
        //add talon code for this later
    }

    public void ballTakeIn() {
        takeIn.set(DoubleSolenoid.Value.kReverse);
    }

    public void ballTakeInSetSpeed() {
        double speedL = xBox.getTriggerAxis(GenericHID.Hand.kLeft) * -1;
        //add talon code for later
    }

    public void xBoxButtons() {
        if (xBox.getAButtonPressed()) {
            ballKickOut();
        }

        if (xBox.getBButtonPressed()) {
            ballTakeIn();
        }
    }
}