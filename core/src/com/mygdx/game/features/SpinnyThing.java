/*
 * Copyright (c) 2020 Glenn Neidermeier
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
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCompoundShape;
import com.mygdx.game.components.BulletComponent;
import com.mygdx.game.components.ModelComponent;

/*
 * some fancy animation controller here
 * to make this work:
 *  ) entire model rotated: r x -90 ctrl+a r (apply rotation) r x 90
 *  ) adjustment to turret origin for "better" rotation center?
 */
public class SpinnyThing extends FeatureAdaptor {

private    Quaternion turretRotation = new Quaternion();
    private    Vector3 down = new Vector3(0, 1, 0);
    private Vector3 trans = new Vector3();
    private Node featureNode;
    private int turretIndex;
    private String strAnimationNode;// = new String("Tank_01.003");
    private ModelComponent mc;
    private BulletComponent bc;


    public SpinnyThing() {
        // empty
    }

    public SpinnyThing(String strAnimationNode) {
        this.strAnimationNode = strAnimationNode;
    }

    @Override
    public void init(Object target) {

        super.init(target);
    }


    @Override
    public void update(Entity sensor) {
        /*
         should be one-time init
         */
        if (null == featureNode) {

            mc = sensor.getComponent(ModelComponent.class);

            if (null != mc) {
                for (int index = 0; index < mc.modelInst.nodes.size; index++) {

                    Node nnnn = mc.modelInst.nodes.get(index);
                    if (nnnn.id.equals( strAnimationNode)                    ) {
                        featureNode = nnnn;
                        turretIndex = index;
                        break;
                    }
                }
            }

            bc = sensor.getComponent(BulletComponent.class);
        }

        /*
         * update
         */
        if (null != featureNode && null != mc) {

            trans.set(featureNode.translation);
//        trans.y += .01f; // test
            featureNode.translation.set(trans);

            turretRotation.set(featureNode.rotation);

            float rfloat = turretRotation.getAngleAround(down);
            rfloat += 1; // test
            turretRotation.set(down, rfloat);
            featureNode.rotation.set(turretRotation);


            mc.modelInst.calculateTransforms(); // definately need this !


// update child collision shape
            if (null != bc &&null != bc.body &&  bc.shape.isCompound()) {

                btCompoundShape btcs = (btCompoundShape) bc.shape;
                if (null != btcs) {
                    btcs.updateChildTransform(turretIndex, featureNode.globalTransform);
                }

                if (null != bc && null != bc.body) {
                    bc.body.setWorldTransform(mc.modelInst.transform);
                }
            }
        }
    }
}
