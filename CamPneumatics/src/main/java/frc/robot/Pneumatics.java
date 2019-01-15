package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.Compressor;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Pneumatics {

    DoubleSolenoid kickOut, takeIn;
    Compressor mCompressor;
    XboxController xBox;
    AnalogInput voltageReading;
    WPI_TalonSRX lTalonSRX, rTalonSRX;

    double speedL, speedR, sensorVoltage, psi;

    //definition of variables
    public Pneumatics() {
        kickOut = new DoubleSolenoid(1, 2, 3);
        takeIn = new DoubleSolenoid(0, 0, 1);
        mCompressor = new Compressor(Consts.compressorPort);
        xBox = new XboxController(Consts.xBoxPort);
        voltageReading = new AnalogInput(Consts.pressureLevelAnalogPin);
        lTalonSRX = new WPI_TalonSRX(7);
        rTalonSRX = new WPI_TalonSRX(8);

        configTalon(lTalonSRX);
        configTalon(rTalonSRX);

        rTalonSRX.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, 7);
		lTalonSRX.setInverted(false);
		rTalonSRX.setInverted(true);
    }

    public void configTalon(WPI_TalonSRX talon) {
        talon.configNominalOutputForward(0, Consts.timeOutMs);
		talon.configNominalOutputReverse(0, Consts.timeOutMs);
		talon.configPeakOutputForward(1, Consts.timeOutMs);
		talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);
		talon.configNeutralDeadband(0.05, Consts.timeOutMs); 
		talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);
		talon.setInverted(false);

		// Peak current and duration must be exceeded before corrent limit is activated.
		// When activated, current will be limited to continuous current.
		// Set peak current params to 0 if desired behavior is to immediately
		// current-limit.
		talon.enableCurrentLimit(true);
		talon.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		talon.configPeakCurrentLimit(30, Consts.timeOutMs); // 100 A
		talon.configPeakCurrentDuration(200, Consts.timeOutMs); // 200 ms
    }

    public double compressorPSI() {
        sensorVoltage = voltageReading.getVoltage();
        psi = 250 * (sensorVoltage / 5) - 25;
        return Math.round(psi);
    }

    public void psiError() {
        if (compressorPSI() < 60.0) {
            System.out.println("The robot called about our pressure level. He said this ain't it chief.");
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
        rTalonSRX.set(speedR);
    }

    public void ballTakeIn() {
        takeIn.set(DoubleSolenoid.Value.kReverse);
    }

    public void ballTakeInSetSpeed() {
        speedL = xBox.getTriggerAxis(GenericHID.Hand.kLeft) * -1;
        lTalonSRX.set(speedL);
    }

    public void xBoxButtons() {
        if (xBox.getAButtonPressed()) {
            ballKickOut();
        }

        if (xBox.getBButtonPressed()) {
            ballTakeIn();
        }
    }

    public enum RobotState {
        INTAKE, KICKOUT
    }
}