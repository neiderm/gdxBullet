package com.mygdx.game.screens;

import com.badlogic.gdx.utils.Array;

public class ModelGroup {

    public ModelGroup() {
    }

    public ModelGroup(String groupName) {
    }

    public ModelGroup(String groupName, String modelName) {
        this(groupName);
        this.modelName = modelName;
    }

    public String modelName;
    public Array<GameObject> gameObjects = new Array<GameObject>();
}
