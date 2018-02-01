package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.game.Components.BulletComponent;
import com.mygdx.game.Components.ModelComponent;
import com.mygdx.game.Components.PlayerComponent;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.screens.MainMenuScreen;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

/**
 * Created by mango on 1/23/18.
 */

public class PlayerSystem extends EntitySystem implements EntityListener {

    private static final float forceScl = 0.2f * 60;

    // create a "braking" force ... ground/landscape is not dynamic and doesn't provide friction!
    private static final float vLossLin = -0.5f; // so this is kinda like coef of friction!

//    static private final float vLossAng = -5.0f;


//    private Engine engine;
    public Entity playerEntity;
    private PlayerComponent playerComp;

    private MyGdxGame game;


    public PlayerSystem(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void addedToEngine(Engine engine) {

//        this.engine = engine;

        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    private Matrix4 tmpM = new Matrix4();
    private Vector3 tmpV = new Vector3();

    @Override
    public void update(float delta) {

//        ModelInstance inst = playerEntity.getComponent(ModelComponent.class).modelInst;

        BulletComponent bc = playerEntity.getComponent(BulletComponent.class);
        btRigidBody body = bc.body;

// should only apply force if linear velocity less than some limit!
        float force = 0; // forceScl * playerComp.mass;

Quaternion r = body.getOrientation(); // ?
float yaw = r.getYaw();

        if (playerComp.vvv.z < -0.5) {
            force = forceScl * playerComp.mass;
            tmpV.set(0, 0, -1);
        } else if (playerComp.vvv.z > 0.5) {
            force = forceScl * playerComp.mass; // TODO: reverse!
            tmpV.set(0, 0, 1);
        }

        tmpV.rotate(0, 1, 0, yaw);
tmpV.x = sin(yaw);
tmpV.z = cos(yaw);

        tmpV.scl(force);

//        body.applyCentralForce(playerComp.vvv.cpy().scl(force));
        body.applyCentralForce(tmpV);


// my negative linear force is great for rolling, but should not apply while FALLING!
// need to simulate friction (function of velocity?) when collision detected
        // always apply loss of energy (torque negative of vA, linear negative of vL, fraction of mass)
        // (only works for sphere if mostly in surface contact, not if falling any significant distance)
//        body.applyTorque(body.getAngularVelocity().scl(vLossAng * mass)); // freaks out if angular scale factor > ~11 ???
        body.applyCentralForce(body.getLinearVelocity().scl(vLossLin * playerComp.mass));

// for dynamic object you should get world trans directly from rigid body!
        body.getWorldTransform(tmpM); // body.getWorldTransform(inst.transform);

        tmpM.getTranslation(tmpV); // inst.transform.getTranslation(trans);

        if (tmpV.y < -20) {
            game.setScreen(new MainMenuScreen(game)); // TODO: status.alive = false ...
        }
        
        // rotate by a constant rate according to stick left or stick right.
        // note: rotation in model space - rotate around the Z (need to fix model export-orientation!)
        float degrees = 0;
        if (playerComp.vvv.x < -0.5) {
            degrees = 1;
        } else if (playerComp.vvv.x > 0.5) {
            degrees = -1;
        }

        tmpM.rotate(0, 1, 0, degrees); // does not touch translation ;)
        body.setWorldTransform(tmpM);
    }


    @Override
    public void entityAdded(Entity entity) {

//        if (null != entity.getComponent(PlayerComponent.class))
        {
            playerEntity = entity;

            playerComp = entity.getComponent(PlayerComponent.class);


            // for getting transform (make it class instance)
            BulletComponent bc = playerEntity.getComponent(BulletComponent.class);
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
