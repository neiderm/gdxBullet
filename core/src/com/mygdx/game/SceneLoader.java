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

package com.mygdx.game;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.mygdx.game.components.BulletComponent;
import com.mygdx.game.components.ModelComponent;
import com.mygdx.game.components.PickRayComponent;
import com.mygdx.game.screens.GameObject;
import com.mygdx.game.screens.InstanceData;
import com.mygdx.game.screens.ModelGroup;
import com.mygdx.game.screens.ModelInfo;
import com.mygdx.game.screens.SceneData;
import com.mygdx.game.util.MeshHelper;
import com.mygdx.game.util.ModelInstanceEx;
import com.mygdx.game.util.PrimitivesBuilder;

import java.util.Random;


/**
 * Created by neiderm on 12/18/17.
 */

public class SceneLoader implements Disposable {

    private SceneData gameData;
    private static boolean useTestObjects = true;
    private AssetManager assets;

//    private static final float DEFAULT_TANK_MASS = 5.1f; // idkf


    public SceneLoader(String path) {

        gameData = new SceneData();
/*
        ModelGroup tanksGroup = new ModelGroup("tanks");
        tanksGroup.gameObjects.add(new GameData.GameObject("ship", "mesh Shape"));
        tanksGroup.gameObjects.add(new GameData.GameObject("tank", "mesh Shape"));
        gameData.modelGroups.put("tanks", tanksGroup);

        ModelGroup sceneGroup = new ModelGroup("scene", "scene");
        sceneGroup.gameObjects.add(new GameData.GameObject("Cube", "none"));
        sceneGroup.gameObjects.add(new GameData.GameObject("Platform001", "convexHullShape"));
        sceneGroup.gameObjects.add(new GameData.GameObject("Plane", "triangleMeshShape"));
        sceneGroup.gameObjects.add(new GameData.GameObject("space", "none"));
        gameData.modelGroups.put("scene", sceneGroup);

        ModelGroup objectsGroup = new ModelGroup("objects", "objects");
        objectsGroup.gameObjects.add(new GameData.GameObject("Crate*", "btBoxShape")); // could be convexHull? (gaps?)
        gameData.modelGroups.put("objects", objectsGroup);

        ModelGroup primitivesGroup = new ModelGroup("primitives", "primitivesModel");
        GameData.GameObject object = new GameData.GameObject("boxTex", "btBoxShape"); // could be convexHull? (gaps?)
        object.instanceData.add( new GameData.GameObject.InstanceData(new Vector3(0, 4, -15), new Vector3(0, 0, 0)));
        object.instanceData.add( new GameData.GameObject.InstanceData(new Vector3(-2, 4, -15), new Vector3(0, 0, 0)));
        object.instanceData.add( new GameData.GameObject.InstanceData(new Vector3(-4, 4, -15), new Vector3(0, 0, 0)));
        object.instanceData.add( new GameData.GameObject.InstanceData(new Vector3(0, 6, -15), new Vector3(0, 0, 0)));
        object.instanceData.add( new GameData.GameObject.InstanceData(new Vector3(-2, 6, -15), new Vector3(0, 0, 0)));
        object.instanceData.add( new GameData.GameObject.InstanceData(new Vector3(-4, 6, -15), new Vector3(0, 0, 0)));
        primitivesGroup.gameObjects.add(object);
        gameData.modelGroups.put("primitives", primitivesGroup);

        gameData.modelInfo.put("scene", new ModelInfo("scene", "data/scene.g3dj"));
        gameData.modelInfo.put("landscape", new ModelInfo("landscape", "data/landscape.g3db"));
        gameData.modelInfo.put("ship", new ModelInfo("ship", "tanks/ship.g3db"));
        gameData.modelInfo.put("tank", new ModelInfo("tank", "tanks/panzerwagen.g3db"));
        gameData.modelInfo.put("objects", new ModelInfo("objects", "data/cubetest.g3dj"));
        gameData.modelInfo.put("primitives", new ModelInfo("primitivesModel", null));
*/
//        saveData(); // tmp: saving to temp file, don't overwrite what we have

//        initializeGameData();

        gameData = SceneData.loadData(path);

        assets = new AssetManager();
/*
        assets.load("data/cubetest.g3dj", Model.class);
        assets.load("data/landscape.g3db", Model.class);
        assets. load("tanks/ship.g3db", Model.class);
        assets.load("tanks/panzerwagen.g3db", Model.class);
        assets.load("data/scene.g3dj", Model.class);
*/
//        int i = gameData.modelInfo.values().size();
        for (String key : gameData.modelInfo.keySet()) {
            if (null != gameData.modelInfo.get(key).fileName) {
                assets.load(gameData.modelInfo.get(key).fileName, Model.class);
            }
        }

        SceneData.saveData(gameData);
    }

    public AssetManager getAssets() {
        return assets;
    }



    /*
        http://niklasnson.com/programming/network/tips%20and%20tricks/2017/09/15/libgdx-save-and-load-game-data.html
    */

/*    public void initializeGameData() {
        if (!fileHandle.exists()) {
            gameData = new GameData();

            saveData();
        } else {
            loadData();
        }
    }*/



    public void doneLoading() {

        for (String key : gameData.modelInfo.keySet()) {
            if (null != gameData.modelInfo.get(key).fileName) {
                gameData.modelInfo.get(key).model = assets.get(gameData.modelInfo.get(key).fileName, Model.class);
            }
        }
//        gameData.modelInfo.get("primitives").model = PrimitivesBuilder.primitivesModel; // maybe we don't need it
    }

    private static void createTestObjects(Engine engine) {

        Random rnd = new Random();

        int N_ENTITIES = 10;
        final int N_BOXES = 4;
        if (!useTestObjects) N_ENTITIES = 0;
        Vector3 size = new Vector3();

        PrimitivesBuilder boxBuilder = PrimitivesBuilder.getBoxBuilder("boxTex");
        PrimitivesBuilder sphereBuilder = PrimitivesBuilder.getSphereBuilder("sphereTex");

        for (int i = 0; i < N_ENTITIES; i++) {

            size.set(rnd.nextFloat() + .1f, rnd.nextFloat() + .1f, rnd.nextFloat() + .1f);
            size.scl(2.0f); // this keeps object "same" size relative to previous primitivesModel size was 2x

            Vector3 translation =
                    new Vector3(rnd.nextFloat() * 10.0f - 5f, rnd.nextFloat() + 25f, rnd.nextFloat() * 10.0f - 5f);

            Entity e;
            if (i < N_BOXES) {
//                e = load(PrimitivesBuilder.model, "boxTex", boxBuilder.getShape(size), size, size.x, translation);
//                engine.addEntity(e);
                engine.addEntity(boxBuilder.create(size.x, translation, size));
            } else {
//                e = load(PrimitivesBuilder.model, "sphereTex", sphereBuilder.getShape(size), size, size.x, translation);
//                engine.addEntity(e);
                engine.addEntity(sphereBuilder.create(size.x, translation, new Vector3(size.x, size.x, size.x)));
            }
        }
    }


    /*
     *  re "obtainStaticNodeShape()"
     *   https://github.com/libgdx/libgdx/wiki/Bullet-Wrapper---Using-models
     *  "collision shape will share the same data (vertices) as the model"
     *
     *  need to look at this comment again ? ...
     *   "in some situations having issues (works only if single node in model, and it has no local translation - see code in Bullet.java)"
     */
    private static btCollisionShape getShape(String shapeName, Vector3 dimensions, Node node)
    {
        btCollisionShape shape;

        if (shapeName.equals("convexHullShape")) {

            shape = MeshHelper.createConvexHullShape(node);
//            int n = ((btConvexHullShape) shape).getNumPoints(); // GN: optimizes to 8 points for platform cube

        } else if (shapeName.equals("triangleMeshShape")) {

            shape = Bullet.obtainStaticNodeShape(node, false);

        } else if (shapeName.equals("btBoxShape")) {

            shape = new btBoxShape(dimensions.scl(0.5f));
        }
        else{ // default?

            shape = new btSphereShape(dimensions.scl(0.5f).x);
        }
        return shape;
    }


    public void buildCharacters(Array<Entity> characters, Engine engine, String groupName) {

        String tmpName;

        if (null != groupName)
            tmpName = groupName;
        else
            tmpName = "tanks";

        ModelGroup mg = gameData.modelGroups.get(tmpName);

        if (null != mg) {
            for (GameObject gameObject : gameData.modelGroups.get(tmpName).gameObjects) {

                Model model = gameData.modelInfo.get(gameObject.objectName).model;
                Entity e;

                for (InstanceData id : gameObject.instanceData) {

                    e = new Entity();

                    // special sauce to hand off the model node
                    ModelInstance inst = ModelInstanceEx.getModelInstance(model, model.nodes.get(0).id);
                    inst.transform.trn(id.translation);
                    e.add(new ModelComponent(inst));

                    if (null != gameObject.meshShape) {
                        btCollisionShape shape = MeshHelper.createConvexHullShape(model.meshes.get(0));
                        e.add(new BulletComponent(shape, inst.transform, gameObject.mass));
                    }

                    if (null != characters) {
                        characters.add(e);
                    }

                    if (gameObject.isPickable) {
                        addPickObject(e, gameObject.objectName);
                    }

                    engine.addEntity(e);
                }
            }
        }
    }


    // tmp special sauce for selectScreen
    private Array<Entity> charactersArray;

    public void buildScene(Engine engine) {

        charactersArray = new Array<Entity>();
        createTestObjects(engine); // creates test objects

        for (String key : gameData.modelGroups.keySet()) {

            ModelGroup mg = gameData.modelGroups.get(key);

            ModelInfo mi = gameData.modelInfo.get(mg.modelName);

            for (GameObject gameObject : mg.gameObjects) {

                Entity e;

                if (null != mi) {

                    Model model = mi.model;

                    /* load all nodes from model that match /objectName.*/
                    for (Node node : model.nodes) {

                        String unGlobbedObjectName = gameObject.objectName.replaceAll("\\*$", "");

                        if (node.id.contains(unGlobbedObjectName)) {

                            InstanceData id;
                            int n = 0;

                            do { // while (null != id && n < gameObject.instanceData.size)
                                e = new Entity();
                                engine.addEntity(e);

                                id = null;

                                if (gameObject.instanceData.size > 0) {
/*
instances should be same size/scale so that we can pass one collision shape to share between them
*/
                                    id = gameObject.instanceData.get(n++);
                                }

                                ModelInstance instance = ModelInstanceEx.getModelInstance(model, node.id);
/*
        scale is in parent object (not instances) because object should be able to share same bullet shape!
        HOWEVER ... seeing below that bullet comp is made with mesh, we still have duplicated meshes ;... :(
*/
                                if (null != gameObject.scale) {
// https://stackoverflow.com/questions/21827302/scaling-a-modelinstance-in-libgdx-3d-and-bullet-engine
                                    instance.nodes.get(0).scale.set(gameObject.scale);
                                    instance.calculateTransforms();
                                }

                                // leave translation null if using translation from the model layout
                                if (null != id) {
                                    if (null != id.rotation) {
                                        instance.transform.idt();
                                        instance.transform.rotate(id.rotation);
                                    }
                                    if (null != id.translation) {
                                        // nullify any translation from the model, apply instance translation
                                        instance.transform.setTranslation(0, 0, 0);
                                        instance.transform.trn(id.translation);
                                    }
                                }

                                btCollisionShape shape = null;

                                if (null != gameObject.meshShape) { // no mesh, no bullet
                                    BoundingBox boundingBox = new BoundingBox();
                                    Vector3 dimensions = new Vector3();
                                    instance.calculateBoundingBox(boundingBox);
                                    shape = getShape(gameObject.meshShape, boundingBox.getDimensions(dimensions), instance.getNode(node.id));
                                }

                                if (null != shape) { // no mesh, no bullet
                                    float mass = gameObject.mass;
                                    BulletComponent bc = new BulletComponent(shape, instance.transform, mass);
                                    e.add(bc);

                                    // special sauce here for static entity
                                    if (gameObject.isKinematic) {  // if (0 == mass) ??
                                        bc.body.setCollisionFlags(
                                                bc.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
                                        bc.body.setActivationState(Collision.DISABLE_DEACTIVATION);
                                    }
                                }

                                ModelComponent mc = new ModelComponent(instance);
                                mc.isShadowed = gameObject.isShadowed; // disable shadowing of skybox)
                                e.add(mc);

                            } while (null != id && n < gameObject.instanceData.size);
                        } // else  ... bail out if matched an un-globbed name ?
                    }
                } else {
                    // look for a model file  named as the object
                    ModelInfo mdlinfo = gameData.modelInfo.get(gameObject.objectName);

                    if (null == mdlinfo) {

                        Vector3 scale = gameObject.scale;
                        btCollisionShape shape = PrimitivesBuilder.getShape(gameObject.objectName, scale); // note: 1 shape re-used

                        for (InstanceData id : gameObject.instanceData) {

                            e = buildObjectInstance(PrimitivesBuilder.getModel(), gameObject, scale, shape, id.translation, id.rotation);

                            if (null != id.color)
                                ModelInstanceEx.setColorAttribute(e.getComponent(ModelComponent.class).modelInst, id.color, id.color.a); // kind of a hack ;)

                            engine.addEntity(e);
                        }
                    }
                }
            }
        }
    }

    /* could end up "gameObject.build()" ?? */
    private Entity buildObjectInstance (
            Model model, GameObject gameObject, Vector3 scale, btCollisionShape shape, Vector3 translation, Quaternion rotation) {

        Entity e = new Entity();

        /*
        scale is in parent object (not instances) because object should be able to share same bullet shape!
        HOWEVER ... seeing below that bullet comp is made with mesh, we still have duplicated meshes ;... :(
         */
        // we can roll the instance scale transform into the getModelInstance ;)
        ModelInstance instance = ModelInstanceEx.getModelInstance(model, gameObject.objectName);

        if (null != scale) {
// https://stackoverflow.com/questions/21827302/scaling-a-modelinstance-in-libgdx-3d-and-bullet-engine
            instance.nodes.get(0).scale.set(scale);
            instance.calculateTransforms();
        }

        if (null != rotation) {
            instance.transform.idt();
            instance.transform.rotate(rotation);
        }

        // leave translation null if using translation from the model layout
        if (null != translation) {
            instance.transform.trn(translation);
        }

        ModelComponent mc = new ModelComponent(instance);
        mc.isShadowed = gameObject.isShadowed; // disable shadowing of skybox)
        e.add(mc);

        if (null != shape) {

            BulletComponent bc = new BulletComponent(shape, instance.transform, gameObject.mass);

            e.add(bc);

            // special sauce here for static entity
            if (gameObject.isKinematic) {  //         if (0 == gameObject.mass) {
// set these flags in bullet comp?
                bc.body.setCollisionFlags(
                        bc.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
                bc.body.setActivationState(Collision.DISABLE_DEACTIVATION);
            }
        }

        if (gameObject.isPickable) {
            addPickObject(e);
        }
        return e;
    }

    @Override
    public void dispose() {

        // The Model owns the meshes and textures, to dispose of these, the Model has to be disposed. Therefor, the Model must outlive all its ModelInstances
        //  Disposing the file will automatically make all instances invalid!
        assets.dispose();

// new test file writer
        SceneData cpGameData = new SceneData();

        for (String key : gameData.modelGroups.keySet()) {

            ModelGroup mg = new ModelGroup(key /* gameData.modelGroups.get(key).groupName */);

            for (GameObject o : gameData.modelGroups.get(key).gameObjects) {

                GameObject cpObject = new GameObject(o.objectName, o.meshShape);

                for (InstanceData i : o.instanceData) {

                    cpObject.instanceData.add(i);
                }
                mg.gameObjects.add(cpObject);
            }

            cpGameData.modelGroups.put(key /* gameData.modelGroups.get(key).groupName */, mg);
        }
//        saveData(cpGameData);
    }

    private static void addPickObject(Entity e) {
        addPickObject(e, null);
    }

    private static void addPickObject(Entity e, String objectName) {

        e.add(new PickRayComponent(objectName)); // set the object name ... yeh pretty hacky
    }
}
