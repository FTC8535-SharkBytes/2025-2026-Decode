package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.control.DrivingController;
import org.firstinspires.ftc.teamcode.control.MechanismController;

@TeleOp(name="Main TeleOp (Drive + Shooter + Servos)")
public class Main_Teleop extends LinearOpMode {

    // --- Drive system ---
    private final DrivingController driveController = new DrivingController();
    private final MechanismController mechanismController = MechanismController.getInstance();

    private LookupTable lookupTable = new LookupTable(
            new double[]{1302, 1308.595, 1316.08, 1324.455, 1333.72, 1343.875, 1354.92, 1366.855, 1379.68, 1393.395, 1408, 1423.495, 1439.88, 1457.155, 1475.32, 1494.375, 1514.32, 1535.155, 1556.88, 1579.495,
                    1603, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2302.195,
                    2345.28},
            20,
            100
    );

    private final ElapsedTime runtime = new ElapsedTime();

    private double desiredVelocity = 0.9 * 100 * 28; // 90% of base target speed
    private boolean bIsPressed = false;
    private boolean xIsPressed = false;

    @Override
    public void runOpMode() {

        // --- Initialize drive ---
        driveController.init(hardwareMap, telemetry, false);
        mechanismController.init(hardwareMap, telemetry, false);


        waitForStart();
        runtime.reset();

        while (opModeIsActive()) {
            // --- Driving control (gamepad1 joysticks) ---
            double axial   = -gamepad1.left_stick_y;  // forward/back
            double lateral =  gamepad1.left_stick_x;  // strafe
            double yaw     =  gamepad1.right_stick_x; // turn

            boolean isFastMode = (gamepad1.right_trigger != 1);
            driveController.updateDriveCommands(axial, lateral, yaw, isFastMode);

            // --- Servo controls (gamepad1 buttons) ---
            // Intake control
            if (gamepad2.dpad_right) {
                mechanismController.setIntakeDown();
            } else if (gamepad2.dpad_left) {
                mechanismController.setIntakeUp();
            }

            // Feeder control
            if (gamepad1.b) {
                mechanismController.setFeederUp();
            } else {
                mechanismController.setFeederDown();
            }

            // Shooter hood control
            if (gamepad2.dpad_up) {
                mechanismController.setHoodUp();
            } else if (gamepad2.dpad_down) {
                mechanismController.setHoodDown();
            }

            // --- Shooter motor belly motor and intake motor control (gamepad2) ---
            if (gamepad2.x) {

                mechanismController.setShooterVelocity(desiredVelocity);
            }
            if (gamepad2.a) {
                mechanismController.stopShooter();
            }

            // Increase velocity
            if (gamepad2.right_trigger > 0.1 && !xIsPressed) {
                xIsPressed = true;
                desiredVelocity += 100;
                mechanismController.setShooterVelocity(desiredVelocity);
            }
            if (gamepad2.right_trigger < 0.1 && xIsPressed) {
                xIsPressed = false;
            }
            // Decrease velocity
            if (gamepad2.left_trigger > 0.1 && !bIsPressed) {
                bIsPressed = true;
                desiredVelocity -= 100;
                mechanismController.setShooterVelocity(desiredVelocity);
            }
            if (gamepad2.left_trigger < 0.1 && bIsPressed) {
                bIsPressed = false;
            }

            // Belly motor controls
            if (gamepad2.y) {
                mechanismController.startBelly();
            } else {
                mechanismController.stopBelly();
            }
            //intake motor
            if (gamepad2.b) {
                mechanismController.startIntake();
            }
            else {
                mechanismController.stopIntake();
            }

            // --- Telemetry ---
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            telemetry.addData("Desired Velocity", desiredVelocity);
            mechanismController.updateTelemetry();
            telemetry.update();
        }
    }
}
