package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.GenericHID;

public class Pneumatics_2018 {

    WPI_TalonSRX leftMasterIntakeTalon, rightSlaveIntakeTalon;
    XboxController xBox;
    DoubleSolenoid clamp;
    Compressor mainC;

    boolean isRoutineRunning;

    public Pneumatics_2018() {
        xBox = new XboxController(Consts.xBoxPort);
        leftMasterIntakeTalon = new WPI_TalonSRX(7);
        rightSlaveIntakeTalon = new WPI_TalonSRX(8);

        clamp = new DoubleSolenoid(0, 2, 3);
        mainC = new Compressor(0);
        
        configTalon(leftMasterIntakeTalon);
		configTalon(rightSlaveIntakeTalon);
		rightSlaveIntakeTalon.set(com.ctre.phoenix.motorcontrol.ControlMode.Follower, 7);
		leftMasterIntakeTalon.setInverted(false);
		rightSlaveIntakeTalon.setInverted(true);
    }

    public void openClamp() {
        clamp.set(DoubleSolenoid.Value.kForward);
    }
    
    public void closeClamp() {
        clamp.set(DoubleSolenoid.Value.kReverse);
    }

    public void stopClamps() {
        clamp.set(DoubleSolenoid.Value.kOff);
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

    public void camClampTest() {
        isRoutineRunning = false;

        if (!isRoutineRunning || xBox.getPOV() != -1 || xBox.getBumper(GenericHID.Hand.kLeft)
        || xBox.getBumper(GenericHID.Hand.kRight) || xBox.getBackButton() || xBox.getStartButton()) {
            switch(xBox()) {
                case CLAMPOPEN:
                    openClamp();
                    break;
                case CLAMPCLOSE:
                    closeClamp();
                    break;
                case OFF:
                    stopClamps();
                    break;
            }
        }
    }

    public PneumaticStates xBox() {
        if (xBox.getXButton()) {
            return PneumaticStates.CLAMPOPEN;
        }

        else if (xBox.getAButton()) {
            return PneumaticStates.CLAMPCLOSE;
        }

        else {
            return PneumaticStates.OFF;
        }
    }

    public enum PneumaticStates {
        CLAMPOPEN, CLAMPCLOSE, OFF
    }
}