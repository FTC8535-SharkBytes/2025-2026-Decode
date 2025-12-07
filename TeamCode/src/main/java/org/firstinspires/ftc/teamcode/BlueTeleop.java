package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class BlueTeleop extends BaseTeleop {
    @Override
    protected int getGoalPipeline() {
        return 1;
    }
}
