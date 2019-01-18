package frc.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.GenericHID;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.AnalogInput;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * @author Cam Clarke
 * 
 * <p> This code is for the pneumatic system for our robot's pistons </p>
 */

public class Pneumatics {

    DoubleSolenoid kickPiston; //We will need two more DS for unknown task
    Compressor mCompressor;
    XboxController xBox;
    AnalogInput voltageReading;
    DigitalInput slideReversecheck;
    WPI_TalonSRX barMotor, pulleyMotor, launchMotor;
    Timer kickoutTimer, pullInTimer;

    boolean isKickoutActivated, isRoutineRunning, isPullInActivated;

    double speedL, speedR, sensorVoltage, psi;
    int kickOutState;

    //definition of variables
    public Pneumatics() {
        kickPiston = new DoubleSolenoid(1, 2, 3); //subject to change
        mCompressor = new Compressor(Consts.compressorPort);
        xBox = new XboxController(Consts.xBoxPort);
        voltageReading = new AnalogInput(Consts.pressureLevelAnalogPin);

        kickoutTimer = new Timer();
        pullInTimer = new Timer();

        slideReversecheck = new DigitalInput(Consts.digitalInputPort);

        isKickoutActivated = false;
        barMotor = new WPI_TalonSRX(0); //subject to change
        pulleyMotor = new WPI_TalonSRX(1); //subject to change
        launchMotor = new WPI_TalonSRX(2); //subject to change

        configTalon(barMotor);
        configTalon(pulleyMotor);
        configTalon(launchMotor);
        barMotor.setInverted(false);
        pulleyMotor.setInverted(false);
        launchMotor.setInverted(true);

        isKickoutActivated = false;
        isPullInActivated = false;
    }

    public void configTalon(WPI_TalonSRX talon) {
        talon.configNominalOutputForward(0, Consts.timeOutMs);
		talon.configNominalOutputReverse(0, Consts.timeOutMs);
		talon.configPeakOutputForward(1, Consts.timeOutMs);
		talon.configPeakOutputReverse(-1, Consts.timeOutMs);
		talon.configAllowableClosedloopError(0, 0, Consts.timeOutMs);
		talon.configNeutralDeadband(0.05, Consts.timeOutMs); 
		talon.setNeutralMode(com.ctre.phoenix.motorcontrol.NeutralMode.Brake);

		// Peak current and duration must be exceeded before corrent limit is activated.
		// When activated, current will be limited to continuous current.
		// Set peak current params to 0 if desired behavior is to immediately
		// current-limit.
		talon.enableCurrentLimit(true);
		talon.configContinuousCurrentLimit(30, Consts.timeOutMs); // Must be 5 amps or more
		talon.configPeakCurrentLimit(30, Consts.timeOutMs); // 100 A
		talon.configPeakCurrentDuration(200, Consts.timeOutMs); // 200 ms
    }

    public void analogSetup() {
        voltageReading.setOversampleBits(8);
		voltageReading.setAverageBits(13);
    }

    public double compressorPSI() {
        sensorVoltage = voltageReading.getVoltage();
        psi = 250 * (sensorVoltage / 5) - 25;
        return Math.round(psi);
    }

    public void robotDiagnostics() {
        SmartDashboard.putNumber("Compressor PSI", psi);
    }

    public void kickoutInit() {
        if (!isKickoutActivated) {
            kickoutTimer.reset();
            kickOutState = 1;
            isKickoutActivated = true;
            kickoutTimer.start();
        }
    }

    public void stop() {
        kickPiston.set(DoubleSolenoid.Value.kOff);
    }

    public void hatchKickOut() {
        kickPiston.set(DoubleSolenoid.Value.kForward);
    }

    public void hatchTakeIn() {
        kickPiston.set(DoubleSolenoid.Value.kReverse);
    }

    public HatchState xBox() {
        
        if (xBox.getAButton()) {
            return HatchState.KICKOUT;
        }

        if (xBox.getBButton()) {
            return HatchState.KICKRETURN;
        }

        else {
            return HatchState.NOTHING;
        }
    }

    public void hatchLaunchPeriodic() {
        isRoutineRunning = isKickoutActivated || isPullInActivated;

        hatchKickOut();

        if (!isRoutineRunning || xBox.getPOV() != -1 || xBox.getBumper(GenericHID.Hand.kLeft) || xBox.getBumper(GenericHID.Hand.kRight) || xBox.getBackButton() || xBox.getStartButton()) {
            switch (xBox()) {
                case KICKOUT:
                    isKickoutActivated = true;
                    break;
                case KICKRETURN:
                    isPullInActivated = true;
                    break;
                case NOTHING:
                    stop();
                    isKickoutActivated = false;
                    isPullInActivated = false;
                    break;
            }
        }
    }

    public enum HatchState {
        KICKOUT, KICKRETURN, NOTHING
    }
}