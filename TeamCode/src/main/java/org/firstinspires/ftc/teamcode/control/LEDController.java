package org.firstinspires.ftc.teamcode.control;

import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Prism.Color;
import org.firstinspires.ftc.teamcode.Prism.GoBildaPrismDriver;
import org.firstinspires.ftc.teamcode.Prism.PrismAnimations;

public class LEDController {
    private Telemetry telemetry;
    PrismAnimations.Solid[] solids = {
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
            new PrismAnimations.Solid(),
    };

    GoBildaPrismDriver prism;

    public void init(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        prism = hardwareMap.get(GoBildaPrismDriver.class, "prism");
        prism.clearAllAnimations();

        for (int i = 0; i < 10; i++) {
            int j = i;
            if (i >= 5)
                j++;

            solids[i].setStartIndex(j);
            solids[i].setStopIndex(j);
            solids[i].setBrightness(50);
            solids[i].setPrimaryColor(Color.BLUE);
            prism.insertAndUpdateAnimation(GoBildaPrismDriver.LayerHeight.values()[i], solids[i]);


        }


    }

    public void setLedColor(int i, Color color) {
        if (i < 0 || i > 4) return;
        solids[i].setPrimaryColor(color);
        solids[i + 5].setPrimaryColor(color);
        prism.updateAnimationFromIndex(GoBildaPrismDriver.LayerHeight.values()[i]);
        prism.updateAnimationFromIndex(GoBildaPrismDriver.LayerHeight.values()[i + 5]);

    }
}
