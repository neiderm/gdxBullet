package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btConvexHullShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btShapeHull;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.Components.BulletComponent;
import com.mygdx.game.Components.CharacterComponent;
import com.mygdx.game.Components.ModelComponent;
import com.mygdx.game.Components.PlayerComponent;
import com.mygdx.game.Managers.EntityFactory.BoxObject;
import com.mygdx.game.Managers.EntityFactory.GameObject;
import com.mygdx.game.Managers.EntityFactory.LandscapeObject;
import com.mygdx.game.Managers.EntityFactory.SphereObject;
import com.mygdx.game.Managers.EntityFactory.StaticEntiteeFactory;
import com.mygdx.game.systems.BulletSystem;

import java.util.Random;

/**
 * Created by mango on 12/18/17.
 */

public class SceneLoader implements Disposable {

    public static final SceneLoader instance = new SceneLoader();

    private static boolean useTestObjects = true;
    private static Model model;
    private static final AssetManager assets;
    private static final Model landscapeModel;
    private static final Model shipModel;
    private static final Model sceneModel;
    private static Model boxTemplateModel;
    private static Model sphereTemplateModel;
    private static Model ballTemplateModel;
    private static Model tankTemplateModel;

    private SceneLoader() {
        //super();
    }

    public void init() { // ?????????

        //super.init();
    }

    static Vector3 tankSize = new Vector3(1, 1, 2);

    static {
        final ModelBuilder mb = new ModelBuilder();

        Texture cubeTex = new Texture(Gdx.files.internal("data/crate.png"), false);
        boxTemplateModel = mb.createBox(1f, 1f, 1f,
                new Material(TextureAttribute.createDiffuse(cubeTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        Texture sphereTex = new Texture(Gdx.files.internal("data/day.png"), false);
        sphereTemplateModel = mb.createSphere(1f, 1f, 1f, 16, 16,
                new Material(TextureAttribute.createDiffuse(sphereTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        Texture ballTex = new Texture(Gdx.files.internal("data/Ball8.jpg"), false);
        ballTemplateModel = mb.createSphere(1f, 1f, 1f, 16, 16,
                new Material(TextureAttribute.createDiffuse(ballTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        tankTemplateModel = mb.createBox(tankSize.x, tankSize.y, tankSize.z,
                new Material(TextureAttribute.createDiffuse(ballTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        assets = new AssetManager();
        assets.load("data/landscape.g3db", Model.class);
        assets.load("data/panzerwagen.g3db", Model.class);
        assets.load("data/panzerwagen_3x3.g3dj", Model.class);
        assets.load("data/ship.g3dj", Model.class);
        assets.load("data/scene.g3dj", Model.class);
        assets.finishLoading();
        landscapeModel = assets.get("data/landscape.g3db", Model.class);
//        shipModel = assets.get("data/panzerwagen_3x3.g3dj", Model.class);
//        shipModel = assets.get("data/panzerwagen.g3db", Model.class);
        shipModel = assets.get("data/ship.g3dj", Model.class);
        sceneModel = assets.get("data/scene.g3dj", Model.class);

        mb.begin();

        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.BLUE))).box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW))).cone(1f, 2f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.CYAN))).capsule(0.5f, 2f, 10);
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.MAGENTA))).cylinder(1f, 2f, 1f, 10);

        model = mb.end();
    }


    public static void createEntities(Engine engine) {

        final int N_ENTITIES = 5;
        final int N_BOXES = 2;

        Vector3 tmpV = new Vector3(); // size
        Random rnd = new Random();

        for (int i = 0; i < N_ENTITIES; i++) {

            tmpV.set(rnd.nextFloat() + .1f, rnd.nextFloat() + .1f, rnd.nextFloat() + .1f);
            tmpV.scl(2.0f); // this keeps object "same" size relative to previous model size was 2x

            Vector3 translation =
                    new Vector3(rnd.nextFloat() * 10.0f - 5f, rnd.nextFloat() + 25f, rnd.nextFloat() * 10.0f - 5f);

            if (i < N_BOXES) {
                engine.addEntity(
                        new BoxObject(tmpV, boxTemplateModel).create(tmpV.x, translation));
            } else {
                engine.addEntity(
                        new SphereObject(tmpV.x, sphereTemplateModel).create(tmpV.x, translation));
            }
        }


        Vector3 t = new Vector3(0, 0 + 25f, 0 - 5f);
        Vector3 s = new Vector3(1, 1, 1);
        if (useTestObjects) {
            engine.addEntity(
                    new GameObject(s, model, "cone").create(rnd.nextFloat() + 0.5f, t,
                            new btConeShape(0.5f, 2.0f)));

            engine.addEntity(
                    new GameObject(s, model, "capsule").create(rnd.nextFloat() + 0.5f, t,
                            new btCapsuleShape(0.5f, 0.5f * 2.0f)));

            engine.addEntity(
                    new GameObject(s, model, "cylinder").create(rnd.nextFloat() + 0.5f, t,
                            new btCylinderShape(new Vector3(0.5f * 1.0f, 0.5f * 2.0f, 0.5f * 1.0f))));
/*
            engine.addEntity(
                    new GameObject(s, model, "sphere").create(rnd.nextFloat() + 0.5f, t,
                            new btSphereShape(0.5f)));
                            */
        } //


        if (useTestObjects) {
            BoxObject bo = new BoxObject(new Vector3(2, 2, 2), boxTemplateModel);

            engine.addEntity(bo.create(0.1f, new Vector3(0, 0 + 4, 0 - 5f)));
            engine.addEntity(bo.create(0.1f, new Vector3(-2, 0 + 4, 0 - 5f)));
            engine.addEntity(bo.create(0.1f, new Vector3(-4, 0 + 4, 0 - 5f)));
            engine.addEntity(bo.create(0.1f, new Vector3(0, 0 + 6, 0 - 5f)));
            engine.addEntity(bo.create(0.1f, new Vector3(-2, 0 + 6, 0 - 5f)));
            engine.addEntity(bo.create(0.1f, new Vector3(-4, 0 + 6, 0 - 5f)));
        } //
        int wallW = 0;
        int wallH = 0;
        for (wallH = 0; wallH < 2; wallH++) {
            for (wallW = 0; wallW < 3; wallW++) {

            }
        }


        StaticEntiteeFactory<GameObject> staticFactory =
                new StaticEntiteeFactory<GameObject>();

        float yTrans = -10.0f;
        Vector3 tran = new Vector3(0, -4 + yTrans, 0);
///*
        engine.addEntity(staticFactory.create(
                new BoxObject(new Vector3(40f, 2f, 40f), boxTemplateModel), tran));

        engine.addEntity(staticFactory.create(
                new SphereObject(16, sphereTemplateModel), new Vector3(10, 5 + yTrans, 0)));

        engine.addEntity(staticFactory.create(
                new BoxObject(new Vector3(40f, 2f, 40f), model, "box"), new Vector3(-15, 1, -20)));
//*/
        if (false) { // this slows down bullet debug drawer considerably!
            // put the landscape at an angle so stuff falls of it...
            Matrix4 transform = new Matrix4().idt().rotate(new Vector3(1, 0, 0), 20f);
//            Matrix4 transform = new Matrix4().idt();
            transform.trn(0, 0 + yTrans, 0);
            engine.addEntity(new LandscapeObject().create(landscapeModel, transform));
        }


// TODO: intatiate object as dynamic, let it fall, then let it rest as static (take out of dynamics world)
    }


    /*
    a character object that tracks the given "node" ...
     */
    public static Entity createChaser1(Engine engine, Matrix4 tgtTransform) {
        /*
          Right now we're moving (translating) it directly, according to a PI loop idea.
          ("Plant" output is simply an offset displacement added to present position).
          This is fine IF the "camera body" is not colliding into another phsics body!

          Regarding kinematic bodyies ...

          "Such an object that does move, but does not respond to collisions, is called a kinematic
          body. In practice a kinematic body is very much like a static object, except that you can
          change its location and rotation through code."

          BUT I don't think kinematc is exactly right thing for my camera, because I don't want the
          camera to affect other phys objects (like the way the ground that does not respond to
          collisions but nonetheless influences objects with forces that can stop them falling
          or them bounce roll etc.)
        */

        Entity e = new GameObject(new Vector3(0.5f, 0.5f, 0.5f),
                model, "sphere").create(new Vector3(0, 15f, -5f));

        // static entity not use motion state so just set the scale on it once and for all
        ModelComponent mc = e.getComponent(ModelComponent.class);
        mc.modelInst.transform.scl(mc.scale);
        mc.modelInst.userData = 0xaa55;
        e.add(new CharacterComponent(
                new PIDcontrol(tgtTransform,
                        mc.modelInst.transform,
                        new Vector3(0, 2, 3),
                        0.1f, 0, 0)));

        engine.addEntity(e);
        return e;
    }

/*
    a character object that tracks the given "node" ...
 */
    public static Entity createChaser2(Engine engine, Vector3 node) {

        /*
          (WE're using/calculating PID error term in 3D, so we could normalize this value
          into a unit vector that would provde appropriate direction vector for applied force!
          (NOTE: the camera "body" should NOT exert foces on other phys objects, so I THINK that
          this is achievable simply by using mass of 0?)

I make it a full phsics object controlled by forces. As a full fledged dynamics object,
have to deal with getting "caught" in other phys structures ... possibly build some "flotation" into
camera and have it dragged along by player as a sort of tether. Or we could use raycast between
player and camera, and if obstacle between, we "break" the tether, allow only force on camera to
be it's "buoyancy", and let if "float up" until free of interposing obstacles .
        */

        final float r = 4.0f;
        Entity e = new GameObject(new Vector3(r, r, r), model, "sphere").create(
                0.01f, new Vector3(0, 15f, -5f), new btSphereShape(r / 2f));

        btRigidBody body = e.getComponent(BulletComponent.class).body;
//        e.add(new CharacterComponent(                new PhysicsPIDcontrol(body, node, 0.1f, 0, 0)));

        engine.addEntity(e);
        return e;
    }

    public static Entity createPlayer(Engine engine) {

        Vector3 s = new Vector3(1, 1, 1);

        float mass = 5.1f; // can't go much more mass, ball way too fast!

        Entity plyr;


        Model model; // shipModel
 String node = null;
if (true){
    model = shipModel; //
    node = null;//null;
    Matrix4 transform = new Matrix4().idt().trn(new Vector3(0, 15f, -5f));
    plyr = new GameObject(s, model, node).create(transform);
    btConvexHullShape shape = createConvexHullShape(model, node, true);
    plyr.add(new BulletComponent(shape, transform, mass));
//*/

}else{
    model = sceneModel; //
    node = "ship";

    Matrix4 transform_ = new Matrix4().idt().trn(new Vector3(0, 15f, -5f));
    plyr = new GameObject(s, model, node).create(transform_);
    ModelInstance instance = plyr.getComponent(ModelComponent.class).modelInst;
    Matrix4 transform = instance.transform;
    Node shipNode = instance.getNode(node);

    instance.transform.set(shipNode.globalTransform);
    shipNode.translation.set(0, 0, 0);
    shipNode.scale.set(1,1,1);
    shipNode.rotation.idt();
    instance.calculateTransforms();

//        Mesh mesh = shipNode.parts.items[0].meshPart.mesh;
//    btConvexHullShape shape = createConvexHullShape(mesh, true);
btBoxShape shape = new btBoxShape(new Vector3(0.5f, 0.35f, 0.75f));
    plyr.add(new BulletComponent(shape, transform, mass));


//    btRigidBody body = plyr.getComponent(BulletComponent.class).body;
//    body.setWorldTransform(transform);
}

//        plyr = new GameObject(s, model, node).create(mass, new Vector3(0, 15f, -5f),createConvexHullShape(model, node, true));

        //                new btBoxShape(new Vector3(0.5f, 0.35f, 0.75f))); // tmp test


        PlayerComponent comp = new PlayerComponent(mass);
        plyr.add(comp);
        engine.addEntity(plyr);

//        Matrix4 tmpM = new Matrix4();
//        btRigidBody body = plyr.getComponent(BulletComponent.class).body;
//        body.getWorldTransform(tmpM);

        // these rotations are equivalent!!!
//        tmpM.rotate(1, 0, 0, -90);
//        tmpM.rotate(-1, 0, 0, 90);

        //        tmpM.getTranslation(tmpV);
//                tmpM.setFromEulerAngles(0, -90, 0);  // but this one clears translation!
//        tmpM.setTranslation(tmpV.x, tmpV.y, tmpV.z);

//        body.setWorldTransform(tmpM); // setCenterOfMassTransform

        return plyr;
    }

/*
  https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/bullet/ConvexHullTest.java

  ship model:
    Blender "view front" ... looking into back of ship
    Export to FBX with Y forward and Z up (and scale 0.01)
    Export to OBJ with -Z^H^H +Z forward and Y up
    http://www.badlogicgames.com/forum/viewtopic.php?f=11&t=18948
    fbx-conv-win32.exe -o G3DJ  -f ship.obj  data/ship.g3dj

  panzer-wagen model:
     https://opengameart.org/content/tankcar
     Blender "view front" ... looking into front of vehicle
    export to FBX with -Y forward and Z up (and scale 0.01)
 */
    public static btConvexHullShape createConvexHullShape (final Model model, boolean optimize) {

        final Mesh mesh = model.meshes.get(0);
        final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
        if (!optimize) return shape;
        // now optimize the shape
        final btShapeHull hull = new btShapeHull(shape);
        hull.buildHull(shape.getMargin());
        final btConvexHullShape result = new btConvexHullShape(hull);
        // delete the temporary shape
        shape.dispose();
        hull.dispose();
        return result;
    }

    public static btConvexHullShape createConvexHullShape (Mesh mesh, boolean optimize) {
        {
            // how the F do you get the mesh from a node
            //           final Mesh mesh = model.meshes.get(0);


            final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
            if (!optimize) return shape;
            // now optimize the shape
            final btShapeHull hull = new btShapeHull(shape);
            hull.buildHull(shape.getMargin());
            final btConvexHullShape result = new btConvexHullShape(hull);
            // delete the temporary shape
            shape.dispose();
            hull.dispose();
            return result;
        }
    }

    public static btConvexHullShape createConvexHullShape (
            final Model model, final String node, boolean optimize) {

        if (node == null ){
            return createConvexHullShape(model, optimize);
        }else {

            int i;

            if (null != node) {

                String id;
                for (i = 0; i < model.nodes.size; i++) {
                    id = model.nodes.get(i).id;
                    if (id.equals("ship")) {
                        break;
                    }
                }

                //        assert( id == "ship")
                id = model.nodes.get(i).id;

            } else {
                i = 0;
            }

            // how the F do you get the mesh from a node
            final Mesh mesh = model.meshes.get(0);

            final btConvexHullShape shape = new btConvexHullShape(mesh.getVerticesBuffer(), mesh.getNumVertices(), mesh.getVertexSize());
            if (!optimize) return shape;
            // now optimize the shape
            final btShapeHull hull = new btShapeHull(shape);
            hull.buildHull(shape.getMargin());
            final btConvexHullShape result = new btConvexHullShape(hull);
            // delete the temporary shape
            shape.dispose();
            hull.dispose();
            return result;
        }
    }

    @Override
    public void dispose() {

        // The Model owns the meshes and textures, to dispose of these, the Model has to be disposed. Therefor, the Model must outlive all its ModelInstances
//  Disposing the model will automatically make all instances invalid!
        sceneModel.dispose();
        tankTemplateModel.dispose();
        sphereTemplateModel.dispose();
        boxTemplateModel.dispose();
        ballTemplateModel.dispose();
        model.dispose();
        assets.dispose();
    }
}
