package com.mygdx.game.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by mango on 12/21/17.
 */

public class ModelComponent implements Component {

    public ModelInstance modelInst;
    public Vector3 scale;

    public ModelComponent(ModelInstance modelInst) {

        this.modelInst = modelInst;
    }

    public ModelComponent(ModelInstance modelInst, Vector3 scale) {

        this.modelInst = modelInst;
        this.scale = new Vector3(scale);

    }
}
