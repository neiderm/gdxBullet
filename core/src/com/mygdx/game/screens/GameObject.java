package com.mygdx.game.screens;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

public class GameObject {

    GameObject() {
    }

    public GameObject(String objectName, String meshShape) {
        this.objectName = objectName;
        this.meshShape = meshShape;
        this.isShadowed = true;
//        this.isKinematic = true;
        this.isPickable = false;
        this.scale = new Vector3(1, 1, 1); // placeholder
    }

    public Array<InstanceData> instanceData = new Array<InstanceData>();
    public String objectName;
    //            Vector3 translation; // needs to be only per-instance
    public Vector3 scale; // NOT per-instance, all instances should be same scale (share same collision Shape)
    public float mass;
    public String meshShape; // triangleMeshShape, convexHullShape
    public boolean isKinematic;  //  "isStatic" ?
    public boolean isPickable;
    public boolean isShadowed;
    public boolean isSteerable;
    public boolean isCharacter;
}

