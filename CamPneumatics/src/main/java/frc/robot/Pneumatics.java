package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Timer;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * @author Cam Clarke
 * 
 * <p> This code is for the pneumatic system for our robot's pistons </p>
 */

public class Pneumatics {

    DoubleSolenoid kickOut, takeIn;
    Compressor mCompressor;
    XboxController xBox;
    AnalogInput voltageReading;
    DigitalInput slideReversecheck;
    WPI_TalonSRX lTalonSRX, rTalonSRX;
    Timer kickoutTimer, pullTimer;

    boolean isKickoutActivated;

    double speedL, speedR, sensorVoltage, psi;
    int kickOutState;

    //definition of variables
    public Pneumatics() {
        kickOut = new DoubleSolenoid(1, 2, 3);
        takeIn = new DoubleSolenoid(0, 0, 1);
        mCompressor = new Compressor(Consts.compressorPort);
        xBox = new XboxController(Consts.xBoxPort);
        voltageReading = new AnalogInput(Consts.pressureLevelAnalogPin);
        lTalonSRX = new WPI_TalonSRX(8);
        rTalonSRX = new WPI_TalonSRX(7);

        kickoutTimer = new Timer();
        pullTimer = new Timer();

        slideReversecheck = new DigitalInput(Consts.digitalInputPort);

        isKickoutActivated = false;

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

    public void kickoutInit() {
        if (!isKickoutActivated) {
            kickoutTimer.reset();
            kickOutState = 1;
            isKickoutActivated = true;
            kickoutTimer.start();
            lTalonSRX.set(1);
        }
    }

    public void kickoutPeriodid() {
        if (isKickoutActivated) {
            switch(kickOutState) {
                case 1:
                    lTalonSRX.set(1);
                    if (kickoutTimer.hasPeriodPassed(0.05)) {
                        kickOutState = 2;
                    }

                    break;
                case 2:
                    //some method here for doing what we want
                    lTalonSRX.set(1);

                    if (kickoutTimer.hasPeriodPassed(1)) {
                        kickOutState = 3;
                    }

                    break;
                case 3:
                    lTalonSRX.set(0);

                    if (kickoutTimer.hasPeriodPassed(2)) {
                        kickOutState = -1;
                        isKickoutActivated = false;
                        kickoutTimer.stop();
                        System.out.print("isKickoutactivated boolean in case five");
					    System.out.print(isKickoutActivated);
                    }
                    break;
                default:
                    System.out.print("WARNING kickout method caught exception");
                    isKickoutActivated = false;
                    stop();
                    break;
            }
        }
    }

    public void stop() {
        kickOut.set(DoubleSolenoid.Value.kOff);
        takeIn.set(DoubleSolenoid.Value.kOff);
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

    public enum HatchState {
        KICKOUT, KICKRETURN, PUSHOUT, PULLIN, NOTHING
    }
}