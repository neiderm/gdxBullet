/*
 * Copyright (c) 2019 Glenn Neidermeier
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mygdx.game.features;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.game.components.CompCommon;
import com.mygdx.game.util.ModelInstanceEx;
import com.mygdx.game.util.PrimitivesBuilder;

/**
 * Created by neiderm on 7/5/2019.
 * <p>
 * This can be a "generic" handler for a sensor. assigned a single target Entity to be sensing for.
 *
 * https://free3d.com/3d-model/t-55-12298.html
 * https://free3d.com/3d-model/veteran-tiger-tank-38672.html
 */
public class KillSensor {

    public enum ImpactType {
        FATAL,
        DAMAGING,
        ACQUIRE
    }


    private Node gunNode;
    private int gunIndex = -1;
    private Node turretNode;
    private int turretIndex = -1;
    private final Vector3 yAxisN = new Vector3(0, -1, 0);
    private final Vector3 yAxis = new Vector3(0, 1, 0);
    private final Vector3 xAxis = new Vector3(1, 0, 0);
    private final Quaternion tmpRotation = new Quaternion();

    private ModelInstance mi;
    private btCompoundShape btcs;
    private btRigidBody body;


    public KillSensor(ModelInstance mi, btCollisionShape bs, btRigidBody body) {

        this.mi = mi;

        if (bs.isCompound()) {

            this.btcs = (btCompoundShape) bs;
            this.body = body;
        }

//        String strMdlNode = "Tank_01.003";
//        String strTurretNode = "Main_Turre"; //"tank_cabine";
        String strTurretNode =  "tank_cabine";

        int index;
        // "unroll" the nodes list so that the index to the bullet child shape will be consistent
        index = PrimitivesBuilder.getNodeIndex(mi.nodes, strTurretNode);

        if (index >= 0) { // index != -1
            turretNode = mi.getNode(strTurretNode, true);  // recursive
            turretIndex = index;
        }

//        String strBarrelNode = "Main_Gun"; // https://free3d.com/3d-model/veteran-tiger-tank-38672.html
        String strBarrelNode =  "tenk_canhao";

        // "unroll" the nodes list so that the index to the bullet child shape will be consistent
        index = PrimitivesBuilder.getNodeIndex(mi.nodes, strBarrelNode);

        if (index >= 0) { // index != -1
            gunNode = mi.getNode(strBarrelNode, true);  // recursive
            gunIndex = index;
        }
    }

    public void updateControls(float[] analogs, boolean[] switches) {

        /// hackage, turret control enable needs a key
        if ( switches[2] /*Gdx.input.isKeyPressed(Input.Keys.ALT_LEFT) */ )
        {
            if (null != turretNode) {
//                trans.set(featureNode.translation);
//            trans.y += .01f; // test
//            featureNode.translation.set(trans);
                tmpRotation.set(turretNode.rotation);

                float rfloat = tmpRotation.getAngleAround(yAxisN);

                rfloat += analogs[0];

                tmpRotation.set(yAxisN, rfloat);
                turretNode.rotation.set(tmpRotation);
            }


            if (null != gunNode) {

                tmpRotation.set(gunNode.rotation);

                float rfloat = tmpRotation.getAngleAround(xAxis);

                rfloat += analogs[1];

                tmpRotation.set(xAxis, rfloat);
                gunNode.rotation.set(tmpRotation);
            }

            mi.calculateTransforms(); // definately need this !


            if (null != btcs && null != body && null != mi.transform) {
// update child collision shape
                if (null != turretNode) {
                    btcs.updateChildTransform(turretIndex, turretNode.globalTransform);
                }
                if (null != gunNode) {
                    btcs.updateChildTransform(gunIndex, gunNode.globalTransform);
                }

                body.setWorldTransform(mi.transform);
            }
        }
    }


    private final Vector3 vFprj = new Vector3();
    private final Vector3 trans = new Vector3();
    private final Vector3 tmpV = new Vector3();
    private final Quaternion orientation = new Quaternion();


    // allowing this to be here so it can be basis of setting forwared vector for projectile/weaopon
    public void fireProjectile(Entity target, ModelInstance pMI) {

        if (null != pMI) {

            Matrix4 tmpM = pMI.transform;
            tmpM.getRotation(orientation);

// wip: handle orientation of gun barrel

//            tmpV.set(0, 0.5f, 0 + 0.5f);
            tmpV.set(0, 0.5f, 0 - 0.5f);
            ModelInstanceEx.rotateRad(tmpV, orientation); //  rotate the offset vector to orientation of vehicle

if (null != turretNode) {
    orientation.set(turretNode.rotation);
    float rTurret = orientation.getAngleAround(yAxisN);
//            ModelInstanceEx.rotateRad(tmpV, orientation); //  rotate the offset vector to orientation of vehicle
}
if (null != gunNode) {
    orientation.set(gunNode.rotation);
    float rBarrel = orientation.getAngleAround(xAxis);
// gun barrel is screwed up and to be rotated properlyn  must be translated back to origin
//            ModelInstanceEx.rotateRad(tmpV, orientation); //  rotate the offset vector to orientation of vehicle
}

            tmpM.getTranslation(trans).add(tmpV); // start coord of projectile = vehicle center + offset
            tmpM.getRotation(orientation);


            // set unit vector for direction of travel for theoretical projectile fired perfectly in forwared direction
            float mag = -0.15f; // scale the '-1' accordingly for magnitifdue of forward "velocity"
            vFprj.set(ModelInstanceEx.rotateRad(tmpV.set(0, 0, mag), orientation));


            CompCommon.spawnNewGameObject(
                    new Vector3(0.1f, 0.1f, 0.1f), trans,
                    new Projectile(target, vFprj),
                    "cone");
        }
    }


    /*
    */
    static void makeBurnOut(ModelInstance mi, KillSensor.ImpactType useFlags) {

        Material saveMat = mi.materials.get(0);

        TextureAttribute tmpTa = (TextureAttribute) saveMat.get(TextureAttribute.Diffuse);
        TextureAttribute fxTextureAttrib = null;

        if (null != tmpTa) {
            Texture tt = tmpTa.textureDescription.texture;
            fxTextureAttrib = TextureAttribute.createDiffuse(tt);

//            fxTextureAttrib = (TextureAttribute)tmpTa.copy(); // idfk maybe toodo
        }

        Vector3 translation = new Vector3(); // tmp for new vector instance .. only need to feed the GC relavitvely few of thsesei guess

        CompCommon.spawnNewGameObject(
                null, mi.transform.getTranslation(translation),
                new BurnOut(fxTextureAttrib, useFlags), "sphere");
    }
}
