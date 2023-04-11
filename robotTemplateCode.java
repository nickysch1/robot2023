//THIS IS A CODE TEMPLATE FOR THE MECHADOGS ROBOTICS TEAM
//YOU MUST EDIT THIS FOR USE 
//COMMENTS EXPLAIN THE MAJORITY
//CONTACT AT 914-325-7250 or nicholas.schnorbus@gmail.com WITH QUESTIONS




//these are imports, they allow extra software to be used, such as camera servers
package frc.robot;
import edu.wpi.first.cameraserver.CameraServer;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorControllerGroup;
import edu.wpi.first.wpilibj.motorcontrol.Spark;
import edu.wpi.first.wpilibj.motorcontrol.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

//all code will be within the following class, unless you create multiple files
public class Robot extends TimedRobot {
  //creates a set of "autos" to choose from, ignore if you only use one
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  //creates a speed variable one for turning and one for normal movement
  private float speed = 1;
  private float turnSpeed = 1;
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();

  //creates a timer for auto
  private Timer autoTimer = new Timer();

  // setup joystick
  private final Joystick joystick = new Joystick(0);

  // create drivetrain motor controllers, assuming two for both sides. Any kind of motor controller can be substituted for WPI_VictorSPX

  WPI_VictorSPX leftDriveTrainOne = new WPI_VictorSPX(1);
  WPI_VictorSPX lefDriveTrainTwo = new WPI_VictorSPX(3);

  WPI_VictorSPX rightDriveTrainTwo = new WPI_VictorSPX(2);
  WPI_VictorSPX rightDriveTrainOne = new WPI_VictorSPX(0);

  // create more motor controllers, Spark can be replaced with any kind of motor controller
  Spark exampleSpark = new Spark(0);

  // group motor controllers, used to control the drivetrain by each side rather than individually. Can treat each group as one motor

  MotorControllerGroup leftSet = new MotorControllerGroup(leftOne, leftTwo);
  MotorControllerGroup rightSet = new MotorControllerGroup(rightOne, rightTwo);

  // create differential drive class using the sets of motors. This allows the use of arcade drive, swerve drive, etc
  private final DifferentialDrive differentialDrive = new DifferentialDrive(leftSet, rightSet);

  
   //all code in this method runs once the robot is turned on in any mode. All setup code should be in here
  @Override
  public void robotInit() {
    //one side must be set inverted to ensure they drive together. Can be left or right
    rightSet.setInverted(true);
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    m_chooser.addOption("Drive Back & Balance", "Drive Back Balance");
    SmartDashboard.putData("Auto choices", m_chooser);
    
    //all the code needed to start a basic camera setup. Can check if its working by the chosen dashboard
    CameraServer.startAutomaticCapture();
  }

  // this method will run every tick of all robot modes, isn't super important unless you want constant diags running
  @Override
  public void robotPeriodic() {
    
  }

  // runs at the start of auto and no other time
  @Override
  public void autonomousInit() {
    //starts and resets the timer for auto that we set earlier
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
    //basically you can set multiple different auto modes and use one of them as selected, each one is under a switch case
    switch (m_autoSelected) {
      case kCustomAuto:
        // Put custom auto code here
        break;
      case kDefaultAuto:
      case "templateCustomAuto":
        //checks if 5 seconds have elapsed from the timer
        if (!autoTimer.hasElapsed(5.0)) {
          //drives for a certain speed and turn during auto while timer goes, constant speed
          differentialDrive.arcadeDrive(.6, .3);
        } else {
          //stops the robot if the timer has expired
          differentialDrive.arcadeDrive(0, 0);
         
        }
        break;
      default:
        // Put default auto code here
        break;
    }
  }
//runs at the start of teleop
  @Override
  public void teleopInit() {
    //stops the timer used during auto and resets it
    autoTimer.stop();
    autoTimer.reset();
  }

// runs every frame of teleop
  @Override
  public void teleopPeriodic() {
    //creates variables to store the movement of the robot every tick
    double forw = -1 * joystick.getRawAxis(1); /* positive is forward */
    double turn = -1 * joystick.getRawAxis(2); /* positive is right */

    //basically just checks if the given input is really small and reduces it to zero if so, helps stop drift
    if (Math.abs(forw) < 0.10) {
      forw = 0;
    }
    if (Math.abs(turn) < 0.10) {
      turn = 0;
    }

     //applies the motion of the robot based on the variables set earlier
    differentialDrive.arcadeDrive(forw * speed, turn * turnSpeed, false);
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

  }
}
