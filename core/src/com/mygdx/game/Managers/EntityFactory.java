package com.mygdx.game.Managers;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btBvhTriangleMeshShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.game.Components.BulletComponent;
import com.mygdx.game.Components.ModelComponent;

import java.util.Random;


/**
 * Created by neiderm on 12/21/2017.
 */

public class EntityFactory {

    private static final AssetManager assets;
    private static final Model landscapeModel;

    private enum pType {
        SPHERE, BOX
    }

    private static Model boxTemplateModel;
    private static Model ballTemplateModel;

    static {
        final ModelBuilder modelBuilder = new ModelBuilder();

        Texture cubeTex = new Texture(Gdx.files.internal("data/crate.png"), false);
        boxTemplateModel  = modelBuilder.createBox(2f, 2f, 2f,
                new Material(TextureAttribute.createDiffuse(cubeTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        Texture sphereTex = new Texture(Gdx.files.internal("data/day.png"), false);
        ballTemplateModel = modelBuilder.createSphere(2f, 2f, 2f, 16, 16,
                new Material(TextureAttribute.createDiffuse(sphereTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        assets = new AssetManager();
        assets.load("data/landscape.g3db", Model.class);
        assets.finishLoading();
        landscapeModel = assets.get("data/landscape.g3db", Model.class);
    }


    private static final int N_ENTITIES = 21;
    private static final int N_BOXES = 10;


    private static void CreateObject(BulletComponent bc, ModelComponent mc,
                                     ModelInstance modelInst, Vector3 sz, float mass, Matrix4 transform) {

//        mc.modelInst  = modelInst;

        bc.scale = new Vector3(sz);

        Vector3 tmp = new Vector3();

        if (mass == 0) {
            modelInst.transform.scl(sz);
            tmp = Vector3.Zero.cpy(); // GN: beware of modifying Zero!
            bc.motionstate = null;
        } else {
            bc.shape.calculateLocalInertia(mass, tmp);
            bc.motionstate = new BulletComponent.MotionState(modelInst.transform);
        }

        btRigidBody.btRigidBodyConstructionInfo bodyInfo =
                new btRigidBody.btRigidBodyConstructionInfo(mass, bc.motionstate, bc.shape, tmp);
        bc.body = new btRigidBody(bodyInfo);
        bc.body.setFriction(0.8f);

        bodyInfo.dispose();

        if (mass == 0) {
//            bc.body.translate(tmp.set(modelInst.transform.val[12], modelInst.transform.val[13], modelInst.transform.val[14]));
            bc.body.translate(modelInst.transform.getTranslation(tmp));
        }
    }

    public static Entity CreateEntity(
            Engine engine, pType tp, Vector3 sz, float mass, Matrix4 transform) {

        btCollisionShape shape = null;
        ModelInstance modelInst = null;

        if (tp == pType.BOX) {
            shape = new btBoxShape(sz);
            modelInst = new ModelInstance(boxTemplateModel);
        }

        if (tp == pType.SPHERE) {
            sz.y = sz.x;
            sz.z = sz.x; // sphere must be symetrical!
            shape = new btSphereShape(sz.x);
            modelInst = new ModelInstance(ballTemplateModel);
        }

        modelInst.transform = new Matrix4(transform);


        Entity e = new Entity();
        engine.addEntity(e);

        ModelComponent mc = new ModelComponent();
        e.add(mc);

        BulletComponent bc = new BulletComponent(shape, modelInst);

        CreateObject(bc, mc, modelInst, sz, mass, transform);

        e.add(bc); // now the BC can be added (bullet system needs valid body on entity added event)

        return e;
    }

    public static void CreateEntities(Engine engine /*, AssetManager assets */) {

        Vector3 tmpV = new Vector3(); // size
        Matrix4 tmpM = new Matrix4(); // transform
        Random rnd = new Random();

        for (int i = 0; i < N_ENTITIES; i++) {
            tmpV.set(rnd.nextFloat() + .1f, rnd.nextFloat() + .1f, rnd.nextFloat() + .1f);
            tmpM.idt().trn(rnd.nextFloat() * 10.0f - 5f, rnd.nextFloat() + 25f, rnd.nextFloat() * 10.0f - 5f);

            pType tp = pType.BOX;

            if (i >= N_BOXES) {
                tp = pType.SPHERE;
            }

            CreateEntity(engine, tp, tmpV, rnd.nextFloat() + 0.5f, tmpM);
        }

        // uncomment for a terrain alternative;
        //tmpM.idt().trn(0, -4, 0);
        //new physObj(physObj.pType.BOX, tmpV.set(20f, 1f, 20f), 0, tmpM);	// zero mass = static
        tmpM.idt().trn(10, -5, 0);
        EntityFactory.CreateEntity(engine, EntityFactory.pType.SPHERE, tmpV.set(8f, 8f, 8f), 0, tmpM);

        createLandscape(engine);
    }

    private static void createLandscape(Engine engine){

        Entity e = new Entity();

        // put the landscape at an angle so stuff falls of it...
        ModelInstance inst = new ModelInstance(landscapeModel);
        inst.transform = new Matrix4().idt().rotate(new Vector3(1, 0, 0), 20f);

        BulletComponent bc = new BulletComponent(
                new btBvhTriangleMeshShape(landscapeModel.meshParts), inst);

        e.add(bc); // now the BC can be added (bullet system needs valid body on entity added event)
        engine.addEntity(e);

        ModelComponent mc = new ModelComponent();
        e.add(mc);
    }

    public static void dispose(){

        boxTemplateModel.dispose();
        ballTemplateModel.dispose();

        assets.dispose();
    }
}
