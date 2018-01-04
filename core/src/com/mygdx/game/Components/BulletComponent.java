package com.mygdx.game.Components;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.linearmath.btMotionState;

/**
 * Created by utf1247 on 12/21/2017.
 */

public class BulletComponent implements Component {


    public static class MotionState extends btMotionState {

        public Matrix4 transform;

        public MotionState(final Matrix4 transform) {
            this.transform = transform;
        }

        @Override
        public void getWorldTransform(Matrix4 worldTrans) {
            worldTrans.set(transform);
        }

        @Override
        public void setWorldTransform(Matrix4 worldTrans) {
            transform.set(worldTrans);
        }
    }

    public MotionState motionstate;

    public btCollisionShape shape;
    public btRigidBody body;

    public Vector3 scale; // tmp?
    public ModelInstance modelInst; // tmp?

    public int id;

    public static int cnt = 0;


    public BulletComponent(){

    }


    public BulletComponent(btCollisionShape shape, ModelInstance modelInst, float mass) {

        this.id = cnt++;

//        this.motionstate = new MotionState(modelInst.transform);
//        this.body = new btRigidBody(0, this.motionstate, shape);
        this.modelInst = modelInst;
        this.shape = shape;


        Vector3 tmp = new Vector3();

        if (mass == 0) {
//            modelInst.transform.scl(sz);
            tmp = Vector3.Zero.cpy(); // GN: beware of modifying Zero!
            this.motionstate = null;
        } else {
            this.shape.calculateLocalInertia(mass, tmp);
            this.motionstate = new BulletComponent.MotionState(modelInst.transform);
        }

        btRigidBody.btRigidBodyConstructionInfo bodyInfo =
                new btRigidBody.btRigidBodyConstructionInfo(mass, this.motionstate, this.shape, tmp);
        this.body = new btRigidBody(bodyInfo);
        this.body.setFriction(0.8f);

        bodyInfo.dispose();
    }


    public BulletComponent(btCollisionShape shape, Matrix4 transform) {

        this.id = cnt++;

        this.motionstate = new MotionState(transform);
        this.body = new btRigidBody(0, this.motionstate, shape);
//        this.modelInst = modelInst;
        this.shape = shape;
    }

}
