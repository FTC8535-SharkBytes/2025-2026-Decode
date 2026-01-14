package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Prism.Color;

public final class MechanismController {

    // Servo positions
    private static final double LEFTINTAKE_DOWN = 0.25;
    private static final double LEFTINTAKE_UP = 0.0;
    //private static final double RIGHTINTAKE_UP = 0.0;
    //private static final double RIGHTINTAKE_DOWN = 0.25;
    private static final double SHOOTER_HOOD_UP = 0.39;
    private static final double SHOOTER_HOOD_DOWN = 0.07;
    private static final double LEFT_KICKSTAND_UP = 0.5;
    private static final double LEFT_KICKSTAND_DOWN = 0.22;
    private static final double RIGHT_KICKSTAND_UP = 0.585;
    private static final double RIGHT_KICKSTAND_DOWN = 0.305;

    private static final double BELLY_VELOCITY = 300;
    // each press =+ 96 ticks/120 degrees
    public static final int BELLY_INCREMENT = 96;

    public static final double NEW_BELLY_ENC_P = 40.0;
    public static final double NEW_BELLY_ENC_I = 0.0; // Orig 3.0
    public static final double NEW_BELLY_ENC_D = -0.01;
    public static final double NEW_BELLY_ENC_F = 0.0;

    public static final double NEW_BELLY_POS_P = 40.0;

    private LEDController ledController = new LEDController();

    private Telemetry telemetry;

    // --- Shooter system ---
    private DcMotorEx shooterMotor = null;
    private DcMotor intakeMotor = null;
    private DcMotorEx bellyMotor = null;
    // --- Servo system ---
    private Servo leftIntake;
    private Servo shooterHood;
    private Servo leftkickstand;
    private Servo rightkickstand;


    private int bellyTargetPosition = 0;
    private double desiredShooterVelocity = 1900; // 90% of base target speed

    private ArtifactSorter artifactSorter = new ArtifactSorter();

    // The field must be declared volatile to ensure that changes to the
    // instance variable are immediately visible to all threads.
    private static volatile MechanismController instance;
    private PIDFCoefficients pidfBellyEncOrig;
    private PIDFCoefficients pidfBellyPosOrig;
    private PIDFCoefficients pidfBellyEncModified;
    private PIDFCoefficients pidfBellyPosModified;


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

        ledController.init(hardwareMap, telemetry);
        artifactSorter.init(hardwareMap, telemetry);

        // --- Initialize shooter hardware ---
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter_motor");
        bellyMotor = hardwareMap.get(DcMotorEx.class, "belly_motor");
        intakeMotor = hardwareMap.get(DcMotor.class,"intake_motor");

        shooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bellyMotor.setTargetPosition(bellyTargetPosition);
        bellyMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        pidfBellyEncOrig = bellyMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        pidfBellyPosOrig = bellyMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);
        bellyMotor.setVelocityPIDFCoefficients(NEW_BELLY_ENC_P, NEW_BELLY_ENC_I, NEW_BELLY_ENC_D, NEW_BELLY_ENC_F);
        pidfBellyEncModified = bellyMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_USING_ENCODER);
        bellyMotor.setPositionPIDFCoefficients(NEW_BELLY_POS_P);
        pidfBellyPosModified = bellyMotor.getPIDFCoefficients(DcMotor.RunMode.RUN_TO_POSITION);

        bellyMotor.setPower(1.0);
        bellyMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

        if (zeroEncoders) {
            zeroBellyEncoders();
        }

        // --- Initialize servos ---
        leftIntake = hardwareMap.get(Servo.class, "intake_servo");
        shooterHood = hardwareMap.get(Servo.class, "shooter_hood_servo");
        leftkickstand = hardwareMap.get(Servo.class, "left_kickstand_servo");
        rightkickstand = hardwareMap.get(Servo.class, "right_kickstand_servo");

        // set initial positions
        leftIntake.setPosition(LEFTINTAKE_UP);
        shooterHood.setPosition(SHOOTER_HOOD_DOWN);
        leftkickstand.setPosition(LEFT_KICKSTAND_UP);
        rightkickstand.setPosition(RIGHT_KICKSTAND_UP);


     }

     private void zeroBellyEncoders() {
        bellyMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        bellyMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        bellyMotor.setTargetPosition(0);
        bellyTargetPosition = 0;
     }

     public void setIntakeUp() {
        leftIntake.setPosition(LEFTINTAKE_UP);

     }

     public void setIntakeDown() {
         leftIntake.setPosition(LEFTINTAKE_DOWN);

         setHoodDown();
     }

     public void setHoodUp() {
         shooterHood.setPosition(SHOOTER_HOOD_UP);
     }

     public void setHoodDown() {
         shooterHood.setPosition(SHOOTER_HOOD_DOWN);
     }

     public void setKickstandUp() {
         leftkickstand.setPosition(LEFT_KICKSTAND_UP);
         rightkickstand.setPosition(RIGHT_KICKSTAND_UP);
     }

     public void setKickstandDown() {
         leftkickstand.setPosition(LEFT_KICKSTAND_DOWN);
         rightkickstand.setPosition(RIGHT_KICKSTAND_DOWN);
     }


     public void setShooterVelocityUsingLimelight(double ta) {
         double sqrt_ta = Math.sqrt(ta);
//         desiredShooterVelocity = 4182.0 - 4293.0 * sqrt_ta + 1600.0 * sqrt_ta * sqrt_ta;
         desiredShooterVelocity = 2768.0 - 2052.0 * sqrt_ta + 705.0 * sqrt_ta * sqrt_ta;
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

     public void setShooterVelocity(double velocity) {
        desiredShooterVelocity = velocity;
        startShooter();
     }

     public void startShooter() {
        shooterMotor.setVelocity(desiredShooterVelocity);
     }

     public void stopShooter() {
        shooterMotor.setVelocity(0);
     }

     public void rotateBelly() {
         bellyTargetPosition += BELLY_INCREMENT;

         bellyMotor.setTargetPosition(bellyTargetPosition);
     }

     public void reverseBelly() {
         bellyTargetPosition -= BELLY_INCREMENT;

         bellyMotor.setTargetPosition(bellyTargetPosition);
     }

     public boolean isBellyAtTarget() {
         return Math.abs(bellyMotor.getCurrentPosition() - bellyTargetPosition) < 10;
     }

     public void startIntake() {
        intakeMotor.setPower(1);
     }

     public void reverseIntake() {
        intakeMotor.setPower(-1);
     }

     public void stopIntake() {
        intakeMotor.setPower(0);
     }

     public void update() {

        artifactSorter.updateColors(bellyMotor.getCurrentPosition());
        artifactSorter.updateLeds(ledController);

        if (isShooterAtSpeed() && shooterMotor.getVelocity() > 100) {
            ledController.setLedColor(0, Color.RED);
        } else {
            ledController.setLedColor(0, Color.BLUE);
        }

     }

     public void updateTelemetry() {
         telemetry.addData("Desired Shooter Velocity", desiredShooterVelocity);
         telemetry.addData("Actual Shooter Velocity", shooterMotor.getVelocity());
         telemetry.addData("Left Intake", leftIntake.getPosition());
         telemetry.addData("Shooter Hood", shooterHood.getPosition());
         telemetry.addData("P,I,D,F Enc (orig)", "%.04f, %.04f, %.04f, %.04f",
                 pidfBellyEncOrig.p, pidfBellyEncOrig.i, pidfBellyEncOrig.d, pidfBellyEncOrig.f);
         telemetry.addData("P,I,D,F Enc (mod)", "%.04f, %.04f, %.04f, %.04f",
                 pidfBellyEncModified.p, pidfBellyEncModified.i, pidfBellyEncModified.d, pidfBellyEncModified.f);
         telemetry.addData("P,I,D,F Pos (orig)", "%.04f, %.04f, %.04f, %.04f",
                 pidfBellyPosOrig.p, pidfBellyPosOrig.i, pidfBellyPosOrig.d, pidfBellyPosOrig.f);
         telemetry.addData("P,I,D,F Pos (mod)", "%.04f, %.04f, %.04f, %.04f",
                 pidfBellyPosModified.p, pidfBellyPosModified.i, pidfBellyPosModified.d, pidfBellyPosModified.f);
         telemetry.addData("Belly Desired Pos", bellyTargetPosition);
         telemetry.addData("Belly Current Pos", bellyMotor.getCurrentPosition());
         artifactSorter.updateTelemetry();
     }
}
