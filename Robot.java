// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.Faults;

import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PneumaticsModuleType;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
// import edu.wpi.cscore.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the
 * name of this class or
 * the package after creating this project, you must also update the
 * build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  boolean breakOut = false;
  private float speed = 1;
  private float balanceSpeed = .1f;
  private float turnSpeed = 1;
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private boolean toggleMode = false;
  private boolean clawAB = true;
  private boolean balanceModeRN = false;
  private double capture = 0;
  Faults _faults_L = new Faults();
  Faults _faults_R = new Faults();

  DoubleSolenoid clawA = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 6, 7);
  DoubleSolenoid clawB = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 4, 5);
  //DoubleSolenoid br = new DoubleSolenoid(PneumaticsModuleType.CTREPCM, 2, 3);

  private Timer autoTimer = new Timer();

  // setup joystick
  private final Joystick joystick = new Joystick(0);

  // create drivetrain motor controllers

  WPI_VictorSPX leftVictorOne = new WPI_VictorSPX(1);
  WPI_VictorSPX leftVictorTwo = new WPI_VictorSPX(3);

  WPI_VictorSPX rightVictorTwo = new WPI_VictorSPX(2);
  WPI_VictorSPX rightVictorOne = new WPI_VictorSPX(0);

  // create arm motor controllers
  Spark armTelescopic = new Spark(0);

  Spark armRotationalLeft = new Spark(1);
  Spark armRotationalRight = new Spark(2);

  // group motor controllers

  // MotorControllerGroup leftDrivetrain = new MotorControllerGroup(leftVictorOne,
  // leftVictorTwo);
  // MotorControllerGroup armGroup = new MotorControllerGroup(armRotationalLeft,
  // armRotationalRight);
  MotorControllerGroup leftVictorSet = new MotorControllerGroup(leftVictorOne, leftVictorTwo);
  MotorControllerGroup rightVictorSet = new MotorControllerGroup(rightVictorOne, rightVictorTwo);

  // create differential drive class
  private final DifferentialDrive differentialDrive = new DifferentialDrive(leftVictorSet, rightVictorSet);

  /**
   * This function is run when the robot is first started up and should be used
   * for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    rightVictorSet.setInverted(true);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    m_chooser.addOption("Drive Back & Balance", "Drive Back Balance");
    SmartDashboard.putData("Auto choices", m_chooser);
    CameraServer.startAutomaticCapture();

    //br.set(Value.kReverse);

  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for
   * items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and
   * test.
   *
   * <p>
   * This runs after the mode specific periodic functions, but before LiveWindow
   * and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
  }

  /**
   * This autonomous (along with the chooser code above) shows how to select
   * between different
   * autonomous modes using the dashboard. The sendable chooser code works with
   * the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the
   * chooser code and
   * uncomment the getString line to get the auto name from the text box below the
   * Gyro
   *
   * <p>
   * You can add additional auto modes by adding additional comparisons to the
   * switch structure
   * below with additional strings. If using the SendableChooser make sure to add
   * them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    autoTimer.stop();
    autoTimer.reset();
    autoTimer.start();
    m_autoSelected = m_chooser.getSelected();
    // m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      case "Drive Back Balance":
        if (!autoTimer.hasElapsed(5.0)) {
          differentialDrive.arcadeDrive(.6, .3);
        } else {
          differentialDrive.arcadeDrive(0, 0);
          //br.set(Value.kForward);
        }
        break;
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    //br.set(Value.kReverse);

    autoTimer.stop();
    autoTimer.reset();
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    double forw = -1 * joystick.getRawAxis(1); /* positive is forward */
    double turn = -1 * joystick.getRawAxis(2); /* positive is right */
    boolean armIn = joystick.getRawButton(2); /* is button is down, print joystick values */
    boolean armOut = joystick.getRawButton(4);

    boolean switchMode = joystick.getRawButtonPressed(10);
    if (switchMode) {
      if (toggleMode) {
        toggleMode = false;
      } else {
        toggleMode = true;
      }
    }
    boolean switchMode2 = joystick.getRawButtonPressed(14);
    if (switchMode2) {
      if (clawAB) {
        clawAB = false;
      } else {
        clawAB = true;
      }
    }

    if (joystick.getPOV() == 180) {
      leftVictorSet.set(-1);
      rightVictorSet.set(1);
    }
    boolean balanceMode = joystick.getRawButtonPressed(13);
    if (balanceMode) {
      if (balanceModeRN) {
        balanceModeRN = false;
        toggleMode = true;
      } else {
        toggleMode = true;
        balanceModeRN = true;
      }
    }
    if (joystick.getRawButton(8) && !toggleMode) {
      capture = forw;
    }
    if (joystick.getRawButton(7)) {
      capture = 0;
    }
    if (toggleMode && capture != 0) {
      armRotationalLeft.set(-forw);
      armRotationalRight.set(forw);
    }
    if (joystick.getPOV() == 270) {
      System.out.println("yippee");
      if (clawAB) {
        clawA.set(Value.kReverse);
      } else {
        clawB.set(Value.kReverse);
      }
    }
    if (joystick.getPOV() == 90) {
      System.out.println("yonder");
      if (clawAB) {
        clawA.set(Value.kForward);
      } else {
        clawB.set(Value.kForward);
      }
    }
    if (joystick.getRawButtonPressed(9)) {
      if (breakOut) {
        //br.set(Value.kReverse);
        breakOut=false;
  
      } else {
        //br.set(Value.kForward);
        breakOut=true;
      }
    }
    // differentialDrive.arcadeDrive(-joystick.getY(), joystick.getX());
    // System.out.println("yo");
    // leftVictorOne.set(ControlMode.PercentOutput, .2);
    // leftVictorTwo.set(ControlMode.PercentOutput, .2);
    // rightVictorOne.set(ControlMode.PercentOutput, -.2);
    // rightVictorTwo.set(ControlMode.PercentOutput, -.2);

    if (armIn) {
      armTelescopic.set(.7);
    } else if (armOut) {
      armTelescopic.set(-.7);
    } else {
      armTelescopic.set(0);
    }

    if (Math.abs(forw) < 0.10) {
      forw = 0;
    }
    if (Math.abs(turn) < 0.10) {
      turn = 0;
    }

    if (toggleMode) {
      if (!balanceMode) {
        differentialDrive.arcadeDrive(forw * speed, turn * turnSpeed, false);
      } else {
        differentialDrive.arcadeDrive(forw * balanceSpeed, turn * balanceSpeed, false);
      }
    } else {
      differentialDrive.arcadeDriveIK(0, 0, false);
      armRotationalLeft.set(-forw);
      armRotationalRight.set(forw);
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {
  }

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {
  }

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {

  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {

    // armRotationalLeft.set(-1);
    // armRotationalRight.set(1);

  }
}
