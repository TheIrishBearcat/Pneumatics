package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.Compressor;

public class Pneumatics {

    DoubleSolenoid kickOut, takeIn;
    Compressor mCompressor;
    XboxController xBox;

    public Pneumatics() {
        kickOut = new DoubleSolenoid(1, 2, 3);
        mCompressor = new Compressor(Consts.compressorPort);
        xBox = new XboxController(Consts.xBoxPort);
    }

    public void ballKickOut() {
        kickOut.set(DoubleSolenoid.Value.kForward);
    }

    public void ballTakeIn() {
        takeIn.set(DoubleSolenoid.Value.kReverse);
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