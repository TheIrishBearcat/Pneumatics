package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;

public class Pneumatics {

    DoubleSolenoid kickOut, takeIn;
    Compressor mCompressor;
    XboxController xBox;
    AnalogInput voltageReading; //this is for voltage

    double speedL, speedR, sensorVoltage, psi;

    //definition of variables
    public Pneumatics() {
        kickOut = new DoubleSolenoid(1, 2, 3);
        takeIn = new DoubleSolenoid(0, 0, 1);
        mCompressor = new Compressor(Consts.compressorPort);
        xBox = new XboxController(Consts.xBoxPort);
        voltageReading = new AnalogInput(Consts.pressureLevelAnalogPin);
    }

    public double compressorPSI() {
        sensorVoltage = voltageReading.getVoltage();
        psi = 250 * (sensorVoltage / 5) - 25;
        return Math.round(psi);
    }

    public void psiError() {
        if (compressorPSI() < 60.0) {
            System.out.println("The robot called about our pressure level chief. He said this ain't it.");
        }
    }
    
    public void analogSetup() {
        voltageReading.setOversampleBits(8);
		voltageReading.setAverageBits(13);
    }

    public void ballKickOut() {
        kickOut.set(DoubleSolenoid.Value.kForward);
    }

    public void ballKickOutSpeedSet() {
        speedR = xBox.getTriggerAxis(GenericHID.Hand.kRight);
        //add talon code for this later
    }

    public void ballTakeIn() {
        takeIn.set(DoubleSolenoid.Value.kReverse);
    }

    public void ballTakeInSetSpeed() {
        speedL = xBox.getTriggerAxis(GenericHID.Hand.kLeft) * -1;
        //add talon code for this later
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