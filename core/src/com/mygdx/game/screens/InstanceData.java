package com.mygdx.game.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

public class InstanceData {

    public InstanceData() {
    }

    public InstanceData(Vector3 translation, Quaternion rotation) {
        this.translation = new Vector3(translation);
        this.rotation = new Quaternion(rotation);
        this.color = Color.CORAL;
    }

    public Quaternion rotation;
    public Vector3 translation;
    public Color color;
}
