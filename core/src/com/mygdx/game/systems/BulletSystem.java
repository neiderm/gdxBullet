package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBroadphaseInterface;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.collision.btCollisionDispatcher;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btDbvtBroadphase;
import com.badlogic.gdx.physics.bullet.collision.btDefaultCollisionConfiguration;
import com.badlogic.gdx.physics.bullet.dynamics.btConstraintSolver;
import com.badlogic.gdx.physics.bullet.dynamics.btDiscreteDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btDynamicsWorld;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.physics.bullet.dynamics.btSequentialImpulseConstraintSolver;
import com.mygdx.game.Components.BulletComponent;
import com.mygdx.game.Managers.EntityFactory;
import com.mygdx.game.screens.physObj;

import java.util.Random;

/**
 * Created by mango on 12/18/17.
 * a bullet and libgdx test from
 *  "from http://bedroomcoders.co.uk/libgdx-bullet-redux-2/"
 */

public class BulletSystem extends EntitySystem implements EntityListener {

    public Vector3 tmpV = new Vector3();
    public Matrix4 tmpM = new Matrix4();

    btCollisionConfiguration collisionConfiguration;
    btCollisionDispatcher dispatcher;
    btBroadphaseInterface broadphase;
    btConstraintSolver solver;
    btDynamicsWorld collisionWorld;


    Vector3 gravity = new Vector3(0, -9.81f, 0);
    public Random rnd = new Random();

//    private Engine engine;
    private ImmutableArray<Entity> entities;


    public BulletSystem(Engine engine, Environment environment, PerspectiveCamera cam /* , Model landscapeModel *//* tmp */) {

//        Bullet.init();
        // Create the bullet world
        collisionConfiguration = new btDefaultCollisionConfiguration();
        dispatcher = new btCollisionDispatcher(collisionConfiguration);
        broadphase = new btDbvtBroadphase();
        solver = new btSequentialImpulseConstraintSolver();
        collisionWorld = new btDiscreteDynamicsWorld(dispatcher, broadphase, solver, collisionConfiguration);
        collisionWorld.setGravity(gravity);



        // little point putting static meshes in a convenience wrapper
        // as you only have a few and don't spawn them repeatedly
///*
if (true != EntityFactory.asdf) {
    Model landscapeModel = EntityFactory.landscapeModel;
    btCollisionShape triMesh = (btCollisionShape) new btBvhTriangleMeshShape(landscapeModel.meshParts);
    // put the landscape at an angle so stuff falls of it...
    physObj.MotionState motionstate = new physObj.MotionState(new Matrix4().idt().rotate(new Vector3(1, 0, 0), 20f));
    EntityFactory.landscape = new btRigidBody(0, motionstate, triMesh);
    EntityFactory.landscapeInstance = new ModelInstance(landscapeModel);
    EntityFactory.landscapeInstance.transform = motionstate.transform;
    collisionWorld.addRigidBody(EntityFactory.landscape);
}        //*/
}

    @Override
    public void update(float deltaTime) {

        collisionWorld.stepSimulation(deltaTime /* Gdx.graphics.getDeltaTime() */, 5);

        for (Entity e : entities) {

            BulletComponent bc = e.getComponent(BulletComponent.class);
            btRigidBody body = bc.body;

            if (null != bc) {
                if (body.isActive()) {  // gdx bullet used to leave scaling alone which was rather useful...

                    bc.modelInst.transform.mul(tmpM.setToScaling(bc.scale));

                    bc.motionstate.getWorldTransform(tmpM);
                    tmpM.getTranslation(tmpV);

                    if (tmpV.y < -10) {
                        tmpM.setToTranslation(rnd.nextFloat() * 10.0f - 5f, rnd.nextFloat() + 25f, rnd.nextFloat() * 10.0f - 5f);
                        // did idt, so need to scl
                        body.setWorldTransform(tmpM);
                        body.setAngularVelocity(Vector3.Zero.cpy());
                        body.setLinearVelocity(Vector3.Zero.cpy());
                    }
                }
            }
        }

    }

    @Override
    public void addedToEngine(Engine engine) {

//        this.engine = engine;

        // Grabs all entities with desired components
        entities = engine.getEntitiesFor(Family.all(BulletComponent.class).get());

        // listener for these so that their bullet objects can be dispose'd
        engine.addEntityListener(Family.all(BulletComponent.class).get(), this);
    }

    @Override
    public void removedFromEngine(Engine engine) {

        collisionWorld.dispose();
        solver.dispose();
        broadphase.dispose();
        dispatcher.dispose();
        collisionConfiguration.dispose();


        // tmp ... loop all Bullet entities to destroy resources
        for (Entity e : entities) {

            BulletComponent bc = e.getComponent(BulletComponent.class);

            bc.shape.dispose();
            bc.body.dispose();
        }
    }

    @Override
    public void entityAdded(Entity entity) {

        BulletComponent bc = entity.getComponent(BulletComponent.class);

        collisionWorld.addRigidBody(bc.body);
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
