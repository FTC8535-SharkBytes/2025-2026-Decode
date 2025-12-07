package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp
public class RedTeleop extends BaseTeleop {
    @Override
    protected int getGoalPipeline() {
        return 2;
    }
}
