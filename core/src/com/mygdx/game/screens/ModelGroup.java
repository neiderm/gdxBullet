package com.mygdx.game.screens;

import com.badlogic.gdx.utils.Array;

public class ModelGroup {

    ModelGroup() {
    }

    public ModelGroup(String groupName) {
    }

    ModelGroup(String groupName, String modelName) {

        this(groupName);
        this.modelName = modelName;
        this.isKinematic = true;
    }

    public String modelName;
    public Array<GameObject> gameObjects = new Array<GameObject>();

    public boolean isKinematic;
    public boolean isCharacter;
}
