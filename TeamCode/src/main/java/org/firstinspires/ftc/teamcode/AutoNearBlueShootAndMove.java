package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.control.DrivingController;
import org.firstinspires.ftc.teamcode.control.MechanismController;

/** @noinspection unused*/
@Autonomous(name="AutoNearBlueShootAndMove", group="Autonomous", preselectTeleOp = "BlueTeleop")
public class AutoNearBlueShootAndMove extends LinearOpMode {
    private final DrivingController driveController = new DrivingController();
    private final MechanismController mechanismController = MechanismController.getInstance();
    private final ElapsedTime runtime = new ElapsedTime();

    enum StateMachine {
        WAITING_FOR_START,
        MOVE_BACK_1,
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

        StateMachine stateMachine;
        stateMachine = StateMachine.WAITING_FOR_START;

        // Wait for the game to start (driver presses START)
        waitForStart();
        resetRuntime();

        while (opModeIsActive()) {
            driveController.updateOdometry();
            mechanismController.update();

            switch (stateMachine) {
                case WAITING_FOR_START:
                    //the first step in the autonomous
                    stateMachine = StateMachine.MOVE_BACK_1;
                    mechanismController.setShooterVelocity(1400);
                    mechanismController.setHoodUp();
                    runtime.reset();
                    break;
                case MOVE_BACK_1:
                    driveController.updateDriveCommands(-0.5, 0.0, 0.0, true);
                    if (runtime.seconds() >= 2.5) {
                        driveController.updateDriveCommands(0.0, 0.0, 0.0, true);
                        stateMachine = StateMachine.SHOOT_1;
                        runtime.reset();
                    }
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
                    driveController.updateDriveCommands(0.0, -0.5, 0.0, true);
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