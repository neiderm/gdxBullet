package com.mygdx.game.components;

import com.badlogic.ashley.core.Component;
import com.mygdx.game.util.IStatusUpdater;

/**
 * Created by neiderm on 7/5/2018.
 */

public class StatusComponent implements Component {

    public String name;

    public final static int  FPS = 60;
    public final static int LIFECLOCKDEFAULT = 7 * FPS;
    public final static int DIECLOCKDEFAULT = 10 * FPS;

    public int lifeClock = LIFECLOCKDEFAULT;
    public int dieClock = DIECLOCKDEFAULT;

    public int hitCount;

    // hackme: all should be removeable EXCEPT player
    public boolean isEntityRemoveable = true;

    public IStatusUpdater statusUpdater;


    private StatusComponent() {

        this("no-name");
        isEntityRemoveable = false; // tmp workaround for player
    }

    public StatusComponent(int lifeClockSecs, int dieClockSecs) {
        this();
        this.lifeClock = lifeClockSecs * FPS;
        this.dieClock = dieClockSecs * FPS;
    }

    public StatusComponent(String name) {

        this.name = name;
    }
}
