package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public final class MechanismController {

    // Servo positions
    private static final double LEFTINTAKE_DOWN = 0.25;
    private static final double LEFTINTAKE_UP = 0.0;
    private static final double RIGHTINTAKE_UP = 0.0;
    private static final double RIGHTINTAKE_DOWN = 0.25;
    private static final double SHOOTER_HOOD_UP = 0.39;
    private static final double SHOOTER_HOOD_DOWN = 0.07;
    private static final double FEEDER_DOWN = 0.3;
    private static final double FEEDER_UP = 0.05;
    private static final double BELLY_VELOCITY = 300;
    // each press =+ 96 ticks/120 degrees
    private static final int BELLY_INCREMENT = 96;

    private Telemetry telemetry;

    // --- Shooter system ---
    private DcMotorEx shooterMotor = null;
    private DcMotor intakeMotor = null;
    private DcMotorEx bellyMotor = null;
    // --- Servo system ---
    private Servo leftIntake;
    private Servo rightIntake;
    private Servo shooterHood;
    private Servo feeder;

    private int bellyTargetPosition = 0;
    private double desiredShooterVelocity = 0.9 * 100 * 28; // 90% of base target speed

    private ArtifactSorter artifactSorter = new ArtifactSorter();

    // The field must be declared volatile to ensure that changes to the
    // instance variable are immediately visible to all threads.
    private static volatile MechanismController instance;

    // Private constructor to prevent direct instantiation
    private MechanismController() {}

    // Public static method to get the single instance of the class
    public static MechanismController getInstance() {
        // Double-checked locking to prevent race conditions in multi-threaded environments
        if (instance == null) {
            synchronized (MechanismController.class) {
                if (instance == null) {
                    instance = new MechanismController();
                }
            }
        }
        return instance;
    }

    public void init(HardwareMap hardwareMap, Telemetry telemetry, boolean zeroEncoders) {
        this.telemetry = telemetry;

        artifactSorter.init(hardwareMap, telemetry);

        // --- Initialize shooter hardware ---
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter_motor");
        bellyMotor = hardwareMap.get(DcMotorEx.class, "belly_motor");
        intakeMotor = hardwareMap.get(DcMotor.class,"intake_motor");

        shooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bellyMotor.setTargetPosition(bellyTargetPosition);
        bellyMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bellyMotor.setPower(1.0);
        bellyMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        if (zeroEncoders) {
            zeroBellyEncoders();
        }

        // --- Initialize servos ---
        leftIntake = hardwareMap.get(Servo.class, "left_intake_servo");
        rightIntake = hardwareMap.get(Servo.class, "right_intake_servo");
        shooterHood = hardwareMap.get(Servo.class, "shooter_hood_servo");
        feeder = hardwareMap.get(Servo.class, "feeder_servo");

        // set initial positions
        leftIntake.setPosition(LEFTINTAKE_UP);
        rightIntake.setPosition(RIGHTINTAKE_UP);
        shooterHood.setPosition(SHOOTER_HOOD_DOWN);
        feeder.setPosition(FEEDER_DOWN);

     }

     private void zeroBellyEncoders() {
        bellyMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bellyMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bellyMotor.setTargetPosition(0);
        bellyTargetPosition = 0;
     }

     public void setIntakeUp() {
        leftIntake.setPosition(LEFTINTAKE_UP);
        rightIntake.setPosition(RIGHTINTAKE_UP);
     }

     public void setIntakeDown() {
         leftIntake.setPosition(LEFTINTAKE_DOWN);
         rightIntake.setPosition(RIGHTINTAKE_DOWN);
         setHoodDown();
     }

     public void setHoodUp() {
         shooterHood.setPosition(SHOOTER_HOOD_UP);
     }

     public void setHoodDown() {
         shooterHood.setPosition(SHOOTER_HOOD_DOWN);
     }

     public void setFeederUp() {
         feeder.setPosition(FEEDER_UP);
     }

     public void setFeederDown() {
         feeder.setPosition(FEEDER_DOWN);
     }

     public void setShooterVelocityUsingLimelight(double ta) {
         double sqrt_ta = Math.sqrt(ta);
         desiredShooterVelocity = 4182.0 - 4293.0 * sqrt_ta + 1600.0 * sqrt_ta * sqrt_ta;
         startShooter();
     }

     public void increaseShooter() {
        desiredShooterVelocity += 100;
        startShooter();
     }

     public void decreaseShooter() {
        desiredShooterVelocity -= 100;
        startShooter();
     }

     public boolean isShooterAtSpeed() {
        return Math.abs(shooterMotor.getVelocity() - desiredShooterVelocity) < 50;
     }

     public void startShooter() {
        shooterMotor.setVelocity(desiredShooterVelocity);
     }

     public void stopShooter() {
        shooterMotor.setVelocity(0);
     }

     public void startBelly() {
        bellyMotor.setVelocity(BELLY_VELOCITY);
     }

     public void stopBelly() {
        bellyMotor.setVelocity(0);
     }

     public void rotateBelly() {
         bellyTargetPosition += BELLY_INCREMENT;

        bellyMotor.setTargetPosition(bellyTargetPosition);
     }

     public boolean isBellyAtTarget() {
         return Math.abs(bellyMotor.getCurrentPosition() - bellyTargetPosition) < 10;
     }

     public void startIntake() {
        intakeMotor.setPower(1);
     }

     public void stopIntake() {
        intakeMotor.setPower(0);
     }

     public void update() {
        if (bellyMotor.getCurrentPosition() > (bellyTargetPosition - BELLY_INCREMENT / 3)) {
            artifactSorter.updateColors();
        } else {
            artifactSorter.clear();
        }
     }

     public void updateTelemetry() {
         telemetry.addData("Desired Shooter Velocity", desiredShooterVelocity);
         telemetry.addData("Actual Shooter Velocity", shooterMotor.getVelocity());
         telemetry.addData("Left Intake", leftIntake.getPosition());
         telemetry.addData("Right Intake", rightIntake.getPosition());
         telemetry.addData("Shooter Hood", shooterHood.getPosition());
         telemetry.addData("Feeder", feeder.getPosition());
         telemetry.addData("Belly Desired Pos", bellyTargetPosition);
         telemetry.addData("Belly Current Pos", bellyMotor.getCurrentPosition());
         artifactSorter.updateTelemetry();
     }
}
