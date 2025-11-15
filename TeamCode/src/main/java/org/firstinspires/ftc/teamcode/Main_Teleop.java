package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;



import org.firstinspires.ftc.teamcode.nisaandvalerie.mechanumstuffthings.DrivingControllerThingyYay;

@TeleOp(name="Main TeleOp (Drive + Shooter + Servos)")
public class Main_Teleop extends LinearOpMode {

    // --- Drive system ---
    private final DrivingControllerThingyYay driveControllerThingy = new DrivingControllerThingyYay();

    // --- Shooter system ---
    private ElapsedTime runtime = new ElapsedTime();
    private DcMotorEx shooterMotor = null;
    private DcMotor intakeMotor = null;
    private DcMotor bellyMotor = null;
    private double desiredVelocity = 0.9 * 100 * 28; // 90% of base target speed
    private boolean bIsPressed = false;
    private boolean xIsPressed = false;

    // --- Servo system ---
    private Servo leftIntake;
    private Servo rightIntake;
    private Servo shooterHood;
    private Servo feeder;

    // Servo positions
    private final double LEFTINTAKE_DOWN = 0.25;
    private final double LEFTINTAKE_UP = 0.0;
    private final double RIGHTINTAKE_UP = 0.0;
    private final double RIGHTINTAKE_DOWN = 0.25;
    private final double SHOOTER_HOOD_UP = 0.31;
    private final double SHOOTER_HOOD_DOWN = 0.07;
    private final double FEEDER_DOWN = 0.3;
    private final double FEEDER_UP = 0.05;

    @Override
    public void runOpMode() {

        // --- Initialize drive ---
        driveControllerThingy.init(hardwareMap, telemetry, false);

        // --- Initialize shooter hardware ---
        shooterMotor = hardwareMap.get(DcMotorEx.class, "shooter_motor");
        bellyMotor = hardwareMap.get(DcMotor.class, "belly_motor");
        intakeMotor = hardwareMap.get(DcMotor.class,"intake_motor");

        shooterMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        shooterMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        bellyMotor.setDirection(DcMotorSimple.Direction.FORWARD);
        intakeMotor.setDirection(DcMotorSimple.Direction.REVERSE);

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

        telemetry.addLine("Initialized: Drive + Shooter + Servos");
        telemetry.update();

        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // --- Driving control (gamepad1 joysticks) ---
            double axial   = -gamepad1.left_stick_y;  // forward/back
            double lateral =  gamepad1.left_stick_x;  // strafe
            double yaw     =  gamepad1.right_stick_x; // turn

            boolean isFastMode = (gamepad1.right_trigger != 1);
            driveControllerThingy.updateDriveCommands(axial, lateral, yaw, isFastMode);

            // --- Servo controls (gamepad1 buttons) ---
            // Intake control
            if (gamepad1.x) { // raise intake
                leftIntake.setPosition(LEFTINTAKE_DOWN);
                rightIntake.setPosition(RIGHTINTAKE_DOWN);
            } else if (gamepad1.y) { // lower intake
                leftIntake.setPosition(LEFTINTAKE_UP);
                rightIntake.setPosition(RIGHTINTAKE_UP);
            }

            // Feeder control
            if (gamepad1.a) {
                feeder.setPosition(FEEDER_DOWN);
            } else if (gamepad1.b) {
                feeder.setPosition(FEEDER_UP);
            }

            // Shooter hood control
            if (gamepad1.dpad_up) {
                shooterHood.setPosition(SHOOTER_HOOD_UP);
            } else if (gamepad1.dpad_down) {
                shooterHood.setPosition(SHOOTER_HOOD_DOWN);
            }

            // --- Shooter motor belly motor and intake motor control (gamepad2) ---
            if (gamepad2.y) {
                shooterMotor.setVelocity(desiredVelocity);
            }
            if (gamepad2.a) {
                shooterMotor.setVelocity(0);
            }

            // Increase velocity
            if (gamepad2.x && !xIsPressed) {
                xIsPressed = true;
                desiredVelocity += 100;
            }
            if (!gamepad2.x && xIsPressed) {
                xIsPressed = false;
            }
            // Decrease velocity
            if (gamepad2.b && !bIsPressed) {
                bIsPressed = true;
                desiredVelocity -= 100;
            }
            if (!gamepad2.b && bIsPressed) {
                bIsPressed = false;
            }

            // Belly motor controls
            if (gamepad2.dpad_up) {
                bellyMotor.setPower(1);
            } else if (gamepad2.dpad_down) {
                bellyMotor.setPower(0);
            }
            //intake motor
            if (gamepad2.dpad_right) {
                intakeMotor.setPower(1);
            }
            else if (!gamepad2.dpad_right) {
                intakeMotor.setPower(0);
            }

            // --- Telemetry ---
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Shooter Velocity", shooterMotor.getVelocity());
            telemetry.addData("Desired Velocity", desiredVelocity);
            telemetry.addData("Left Intake", leftIntake.getPosition());
            telemetry.addData("Right Intake", rightIntake.getPosition());
            telemetry.addData("Shooter Hood", shooterHood.getPosition());
            telemetry.addData("Feeder", feeder.getPosition());
            telemetry.update();
        }
    }
}
