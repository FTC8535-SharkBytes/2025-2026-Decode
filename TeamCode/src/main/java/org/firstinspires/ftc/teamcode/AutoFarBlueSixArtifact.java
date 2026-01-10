package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
import org.firstinspires.ftc.teamcode.control.ArtifactSorter;
import org.firstinspires.ftc.teamcode.control.DrivingController;
import org.firstinspires.ftc.teamcode.control.LimelightController;
import org.firstinspires.ftc.teamcode.control.MechanismController;
import org.firstinspires.ftc.teamcode.navigation.DriveToPoint;

/** @noinspection unused*/
@Autonomous(name="AutoFarBlueSixArtifact", group="Autonomous", preselectTeleOp = "BlueTeleop")
public class AutoFarBlueSixArtifact extends LinearOpMode {
    private final DrivingController driveController = new DrivingController();
    private final MechanismController mechanismController = MechanismController.getInstance();
    private final ElapsedTime runtime = new ElapsedTime();
    private final LimelightController limelightController = new LimelightController();
    DriveToPoint nav = new DriveToPoint(this);

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
        DRIVE_TO_ARTIFACTS_1,
        INTAKE_1,
        INTAKE_2,
        INTAKE_UP,
        MOVE_TO_SHOOT,
        AIM_TOWARD_GOAL_2,
        WAIT_FOR_SORT_2,
        START_SHOOTER_2,
        SHOOT_4,
        SHOOT_5,
        SHOOT_6,
        WAIT_FOR_SHOTS_2,
        MOVE_TO_PARK,
        AT_TARGET
    }

    static final Pose2D TARGET_ARTIFACT_1 = new Pose2D(DistanceUnit.MM,625,475, AngleUnit.DEGREES,-90);
    static final Pose2D TARGET_FAR_ZONE = new Pose2D(DistanceUnit.MM,50,0, AngleUnit.DEGREES,20);


    @Override
    public void runOpMode() {
        driveController.init(hardwareMap, telemetry, true);
        mechanismController.init(hardwareMap, telemetry, true);
        limelightController.init(hardwareMap, telemetry);
        nav.setDriveType(DriveToPoint.DriveType.MECANUM);


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
                        mechanismController.setIntakeDown();
                        stateMachine = StateMachine.DRIVE_TO_ARTIFACTS_1;
                    }
                    break;
                case DRIVE_TO_ARTIFACTS_1:
                    // wait a half second before moving to let shooting finish
                    if (nav.driveTo(driveController.getOdometryPosition(), TARGET_ARTIFACT_1, 0.45, 0)){
                        stateMachine = StateMachine.INTAKE_1;
                        runtime.reset();
                    }
                    break;
                case INTAKE_1:
                    if (runtime.seconds() < 1.25) {
                        driveController.updateDriveCommands(-0.8, 0, 0, false);
                        mechanismController.startIntake();
                    } else {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.INTAKE_2;
                        runtime.reset();
                    }
                    break;
                case INTAKE_2:
                    if (runtime.seconds() < 1.0) {
                        driveController.updateDriveCommands(-0.8, 0, 0, false);
                        mechanismController.startIntake();
                    } else {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.INTAKE_UP;
                        runtime.reset();
                    }
                    break;
                case INTAKE_UP:
                    if (runtime.seconds() < 0.5) {
                        driveController.updateDriveCommands(-0.5, 0, 0, false);
                        mechanismController.startIntake();
                    } else {
                        mechanismController.setIntakeUp();
                        mechanismController.stopIntake();
                        driveController.updateDriveCommands(0, 0, 0, false);
                        stateMachine = StateMachine.MOVE_TO_SHOOT;
                        runtime.reset();
                    }
                    break;
                case MOVE_TO_SHOOT://numbers might need changed and is team dependant
                    if (nav.driveTo(driveController.getOdometryPosition(), TARGET_FAR_ZONE, 0.45, 0)){
                        stateMachine = StateMachine.AIM_TOWARD_GOAL_2;
                        runtime.reset();
                    }
                    break;
                case AIM_TOWARD_GOAL_2:
                    if (runtime.seconds() > 1.0) {
                        driveController.updateDriveCommands(0, 0, 0, true);
                        stateMachine = StateMachine.WAIT_FOR_SORT_2;
                        runtime.reset();
                    } else {
                        LimelightController.LimelightResult limelightResult
                                = limelightController.getLimelightTrackingResult();
                        if (limelightResult.isValid) {
                            double tx = limelightResult.tx;
                            driveController.updateDriveWithLimelight(0, 0, tx, true);
                            if (tx < 0.1 && tx > -0.1) {//numbers might need changed
                                driveController.updateDriveCommands(0, 0, 0, true);

                                stateMachine = StateMachine.WAIT_FOR_SORT_2;
                                runtime.reset();
                            }
                        }
                    }
                    break;
                case WAIT_FOR_SORT_2:
                    if (mechanismController.isBellyAtTarget()) {
                        stateMachine = StateMachine.START_SHOOTER_2;
                        runtime.reset();
                    }
                    break;
                case START_SHOOTER_2:
                    //the first step in the autonomous
                    stateMachine = StateMachine.SHOOT_4;
                    mechanismController.setShooterVelocity(1900);
                    mechanismController.setHoodUp();
                    runtime.reset();
                    break;
                case SHOOT_4:
                    if (mechanismController.isShooterAtSpeed()) {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.SHOOT_5;
                        runtime.reset();
                    }
                    break;
                case SHOOT_5:
                    if (runtime.seconds() >= 0.5) {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.SHOOT_6;
                        runtime.reset();
                    }
                    break;
                case SHOOT_6:
                    if (runtime.seconds() >= 0.5) {
                        mechanismController.rotateBelly();
                        stateMachine = StateMachine.WAIT_FOR_SHOTS_2;
                        runtime.reset();
                    }
                    break;
                case WAIT_FOR_SHOTS_2:
                    if (mechanismController.isBellyAtTarget()) {
                        stateMachine = StateMachine.MOVE_TO_PARK;
                        runtime.reset();
                    }
                    break;
                case MOVE_TO_PARK:
                    driveController.updateDriveCommands(0.5, 0.0, 0.0, true);
                    if (runtime.seconds() >= 1.0) {
                        driveController.updateDriveCommands(0.0, 0.0, 0.0, true);
                        stateMachine = StateMachine.AT_TARGET;
                    }
                    break;

            }


            if (stateMachine == StateMachine.AT_TARGET){
                driveController.updateDriveCommands(0.0, 0.0, 0.0, true);
            } else if (stateMachine == StateMachine.DRIVE_TO_ARTIFACTS_1 ||
                    stateMachine == StateMachine.MOVE_TO_SHOOT) {
                DrivingController.WheelPower wheelPower = new DrivingController.WheelPower(nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_FRONT),
                        nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_FRONT),
                        nav.getMotorPower(DriveToPoint.DriveMotor.LEFT_BACK),
                        nav.getMotorPower(DriveToPoint.DriveMotor.RIGHT_BACK));
                driveController.assignWheelPowers(wheelPower);
            }

            telemetry.addData("current state:",stateMachine);
            mechanismController.updateTelemetry();

            telemetry.update();

        }
    }
}