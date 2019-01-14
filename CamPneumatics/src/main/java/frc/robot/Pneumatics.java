package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;

public class Pneumatics {

    DoubleSolenoid piston1;
    Compressor mCompressor;

    public Pneumatics() {
        piston1 = new DoubleSolenoid(0, 1, 0);
        mCompressor = new Compressor(0);
    }
}