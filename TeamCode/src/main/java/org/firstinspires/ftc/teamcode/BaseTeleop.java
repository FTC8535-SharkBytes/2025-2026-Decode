package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.control.DrivingController;
import org.firstinspires.ftc.teamcode.control.LimelightController;
import org.firstinspires.ftc.teamcode.control.MechanismController;

/** @noinspection unused*/
public abstract class BaseTeleop extends LinearOpMode {

    protected abstract int getGoalPipeline();

    // --- Drive system ---
    private final DrivingController driveController = new DrivingController();
    private final MechanismController mechanismController = MechanismController.getInstance();
    private final LimelightController limelightController = new LimelightController();

    private final ElapsedTime runtime = new ElapsedTime();

    private LimelightController.LimelightResult limelightResult;
    private boolean bIsPressed = false;
    private boolean xIsPressed = false;
    private boolean yPressedLast = false;

    @Override
    public void runOpMode() {

        // --- Initialize drive ---
        driveController.init(hardwareMap, telemetry, true);
        mechanismController.init(hardwareMap, telemetry, false);
        limelightController.init(hardwareMap, telemetry);

        waitForStart();
        runtime.reset();
        limelightController.switchPipeline(getGoalPipeline());

        while (opModeIsActive()) {
            driveController.updateOdometry();
            mechanismController.update();
            limelightResult = limelightController.getLimelightTrackingResult();

            // --- Driving control (gamepad1 joysticks) ---
            double axial   = -gamepad1.left_stick_y;  // forward/back
            double lateral =  gamepad1.left_stick_x;  // strafe
            double yaw     =  gamepad1.right_stick_x; // turn

            boolean isFastMode = (gamepad1.right_trigger != 1);
            boolean isAimMode = (gamepad1.left_trigger > 0);
            if (isAimMode) {
                double tx = 0;
                if (limelightResult.isValid) {
                    tx = limelightResult.tx;
                }
                driveController.updateDriveWithLimelight(axial, lateral, tx, isFastMode);
            } else {
                driveController.updateDriveCommands(axial, lateral, yaw, isFastMode);
            }

            // --- Servo controls (gamepad1 buttons) ---
            // Intake control
            if (gamepad2.dpad_right) {
                mechanismController.setIntakeDown();
            } else if (gamepad2.dpad_left) {
                mechanismController.setIntakeUp();
            }

            // Feeder control
            if (gamepad1.b) {
                mechanismController.setKickstandUp();
            } else if (gamepad1.a) {
                mechanismController.setKickstandDown();
            }

            // Shooter hood control
            if (gamepad2.dpad_up) {
                mechanismController.setHoodUp();
            } else if (gamepad2.dpad_down) {
                mechanismController.setHoodDown();
            }

            // --- Shooter motor belly motor and intake motor control (gamepad2) ---
            if (gamepad2.x) {
                if (limelightResult.isValid) {
                    mechanismController.setShooterVelocityUsingLimelight(limelightResult.ta);
                }
            }
            if (gamepad2.a) {
                mechanismController.stopShooter();
            }

            // Increase velocity
            if (gamepad2.right_trigger > 0.1 && !xIsPressed) {
                xIsPressed = true;
                mechanismController.increaseShooter();
            }
            if (gamepad2.right_trigger < 0.1 && xIsPressed) {
                xIsPressed = false;
            }
            // Decrease velocity
            if (gamepad2.left_trigger > 0.1 && !bIsPressed) {
                bIsPressed = true;
                mechanismController.decreaseShooter();
            }
            if (gamepad2.left_trigger < 0.1 && bIsPressed) {
                bIsPressed = false;
            }

            // Belly motor controls
            if (gamepad2.y && !yPressedLast) {
                if (gamepad2.back) {
                    mechanismController.reverseBelly();
                } else {
                    mechanismController.rotateBelly();
                }
                yPressedLast = true;
            } else if (!gamepad2.y && yPressedLast) {
                yPressedLast = false;
//                mechanismController.stopBelly();
            }
            //intake motor
            if (gamepad2.b) {
                if (gamepad2.back) {
                    mechanismController.reverseIntake();
                } else {
                    mechanismController.startIntake();
                    }
            }
            else {
                mechanismController.stopIntake();
            }

            // --- Telemetry ---
            telemetry.addData("Status", "Run Time: " + runtime.toString());
            mechanismController.updateTelemetry();
            telemetry.update();
        }
    }
}
