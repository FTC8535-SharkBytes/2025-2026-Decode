package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.control.ArtifactSorter;
import org.firstinspires.ftc.teamcode.control.DrivingController;
import org.firstinspires.ftc.teamcode.control.LimelightController;
import org.firstinspires.ftc.teamcode.control.MechanismController;

/** @noinspection unused*/
@Autonomous(name="AutoFarBlueSort", group="Autonomous", preselectTeleOp = "BlueTeleop")
public class AutoFarBlueSort extends LinearOpMode {
    private final DrivingController driveController = new DrivingController();
    private final MechanismController mechanismController = MechanismController.getInstance();
    private final ElapsedTime runtime = new ElapsedTime();
    private final LimelightController limelightController = new LimelightController();

    enum StateMachine {
        READ_LIMELIGHT,
        TURN_TOWARD_GOAL,
        AIM_TOWARD_GOAL,
        WAIT_FOR_SORT,
        START_SHOOTER,
        SHOOT_1,
        SHOOT_2,
        SHOOT_3,
        WAIT_FOR_SHOTS_1,
        WAIT_TO_DRIVE_1,
        DRIVE_TO_PARK,
        AT_TARGET
    }

    @Override
    public void runOpMode() {
        driveController.init(hardwareMap, telemetry, true);
        mechanismController.init(hardwareMap, telemetry, true);
        limelightController.init(hardwareMap, telemetry);

        StateMachine stateMachine;
        stateMachine = StateMachine.READ_LIMELIGHT;

        // Wait for the game to start (driver presses START)
        limelightController.switchPipeline(0);
        waitForStart();
        resetRuntime();

        while (opModeIsActive()) {
            driveController.updateOdometry();
            mechanismController.update();

            switch (stateMachine) {
                case READ_LIMELIGHT:
                    if (runtime.seconds() > 1.0) {
                        ArtifactSorter.Pattern pattern = limelightController.detectPattern();
                        switch (pattern) {
                            case PPG:
                                mechanismController.rotateBelly();
                                break;
                            case PGP:
                                mechanismController.rotateBelly();
                                mechanismController.rotateBelly();
                                break;
                        }
                        limelightController.switchPipeline(1);
                        stateMachine = StateMachine.TURN_TOWARD_GOAL;
                        runtime.reset();
                    }
                    break;
                case TURN_TOWARD_GOAL://numbers might need changed and is team dependant
                    if (runtime.seconds() < 0.2) {
                        driveController.updateDriveCommands(0, 0, -0.5, true);
                    } else {
                        driveController.updateDriveCommands(0, 0, 0, true);
                        stateMachine = StateMachine.AIM_TOWARD_GOAL;
                        runtime.reset();
                    }
                    break;
                case AIM_TOWARD_GOAL:
                    if (runtime.seconds() > 1.0) {
                        driveController.updateDriveCommands(0, 0, 0, true);
                        stateMachine = StateMachine.WAIT_FOR_SORT;
                        runtime.reset();
                    } else {
                        LimelightController.LimelightResult limelightResult
                                = limelightController.getLimelightTrackingResult();
                        if (limelightResult.isValid) {
                            double tx = limelightResult.tx;
                            driveController.updateDriveWithLimelight(0, 0, tx, true);
                            if (tx < 0.1 && tx > -0.1) {//numbers might need changed
                                driveController.updateDriveCommands(0, 0, 0, true);

                                stateMachine = StateMachine.WAIT_FOR_SORT;
                                runtime.reset();
                            }
                        }
                    }

                    break;
                case WAIT_FOR_SORT:
                    if (mechanismController.isBellyAtTarget()) {
                        stateMachine = StateMachine.START_SHOOTER;
                        runtime.reset();
                    }
                    break;
                case START_SHOOTER:
                    //the first step in the autonomous
                    stateMachine = StateMachine.SHOOT_1;
                    mechanismController.setShooterVelocity(1900);
                    mechanismController.setHoodUp();
                    runtime.reset();
                    break;
                case SHOOT_1:
                    if (mechanismController.isShooterAtSpeed()) {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.SHOOT_2;
                        runtime.reset();
                    }
                    break;
                case SHOOT_2:
                    if (runtime.seconds() >= 0.5) {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.SHOOT_3;
                        runtime.reset();
                    }
                    break;
                case SHOOT_3:
                    if (runtime.seconds() >= 0.5) {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.WAIT_FOR_SHOTS_1;
                        runtime.reset();
                    }
                    break;
                case WAIT_FOR_SHOTS_1:
                    if (mechanismController.isBellyAtTarget()) {
                        stateMachine = StateMachine.WAIT_TO_DRIVE_1;
                        runtime.reset();
                    }
                    break;
                case WAIT_TO_DRIVE_1:
                    if (runtime.seconds() >= 2.0) {
                        mechanismController.stopShooter();
                        runtime.reset();
                        stateMachine = StateMachine.DRIVE_TO_PARK;
                    }
                    break;
                case DRIVE_TO_PARK:
                    // wait a half second before moving to let shooting finish
                    driveController.updateDriveCommands(0.5, 0.0, 0.0, true);
                    if (runtime.seconds() >= 1.0) {
                        driveController.updateDriveCommands(0.0, 0.0, 0.0, true);
                        stateMachine = StateMachine.AT_TARGET;
                    }
                    break;

            }

            DrivingController.WheelPower wheelPower;
            if (stateMachine == StateMachine.AT_TARGET){
                driveController.updateDriveCommands(0.0, 0.0, 0.0, true);
            }

            telemetry.addData("current state:",stateMachine);
            mechanismController.updateTelemetry();

            telemetry.update();

        }
    }
}