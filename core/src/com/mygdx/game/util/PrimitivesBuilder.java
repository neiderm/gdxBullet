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
package com.mygdx.game.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.Bullet;
import com.badlogic.gdx.physics.bullet.collision.Collision;
import com.badlogic.gdx.physics.bullet.collision.btBoxShape;
import com.badlogic.gdx.physics.bullet.collision.btCapsuleShape;
import com.badlogic.gdx.physics.bullet.collision.btCollisionObject;
import com.badlogic.gdx.physics.bullet.collision.btCollisionShape;
import com.badlogic.gdx.physics.bullet.collision.btConeShape;
import com.badlogic.gdx.physics.bullet.collision.btCylinderShape;
import com.badlogic.gdx.physics.bullet.collision.btSphereShape;
import com.mygdx.game.components.BulletComponent;
import com.mygdx.game.components.ModelComponent;

/**
 * Created by neiderm on 12/18/17.
 */

public class PrimitivesBuilder extends BaseEntityBuilder /* implements Disposable */ {

    // use unit (i.e. 1.0f) for all dimensions - primitive objects will have scale applied by load()
    protected static final float DIM_UNIT = 1.0f;
    protected static final float DIM_HE = 1f / 2f; // primitives half extent constant
    protected static final float DIM_CAPS_HT = 1.0f + 0.5f + 0.5f; // define capsule height ala bullet (HeightTotal = H + 1/2R + 1/2R)


    public static Model model;

    /* instances only access the protected reference to the model */
//    private PrimitivesBuilder() { }

    public static Model getModel(){
        return model;
    }

    /* one instance of the primitives model is allowed to persist for the entire app lifetime */
    public static void init() {

        final ModelBuilder mb = new ModelBuilder();
        long attributes =
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal;
        Texture tex;

        mb.begin();

        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, attributes,
                new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, attributes,
                new Material(ColorAttribute.createDiffuse(Color.BLUE))).box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, attributes,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW))).cone(1f, 1f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, attributes,
                new Material(ColorAttribute.createDiffuse(Color.CYAN))).capsule(1f * DIM_HE, DIM_CAPS_HT, 10); // note radius and height vs. bullet
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, attributes,
                new Material(ColorAttribute.createDiffuse(Color.MAGENTA))).cylinder(1f, 1f, 1f, 10);

        attributes |= VertexAttributes.Usage.TextureCoordinates;

        tex = new Texture(Gdx.files.internal("data/crate.png"), true);
        mb.node().id = "boxTex";
        mb.part("box", GL20.GL_TRIANGLES, attributes,
                new Material(TextureAttribute.createDiffuse(tex))).box(1f, 1f, 1f);

        tex = new Texture(Gdx.files.internal("data/day.png"), true);
        mb.node().id = "sphereTex";
        mb.part("sphere", GL20.GL_TRIANGLES, attributes,
                new Material(TextureAttribute.createDiffuse(tex))).sphere(1f, 1f, 1f, 10, 10);

        tex = new Texture(Gdx.files.internal("data/badlogic.jpg"), true);
        mb.node().id = "sphereCharacter";
        mb.part("sphere", GL20.GL_TRIANGLES, attributes,
                new Material(TextureAttribute.createDiffuse(tex))).sphere(1f, 1f, 1f, 10, 10);

        /* example of createCullFace */ /*
        tex = new Texture(Gdx.files.internal("data/sky.jpg"), true);
        mb.node().id = "skySphere";
        mb.part("sphere", GL20.GL_TRIANGLES, attributes,
        new Material(TextureAttribute.createDiffuse(tex), IntAttribute.createCullFace(GL_FRONT))).sphere(1f, 1f, 1f, 10, 10);
*/
        model = mb.end();
    }

    /*
    Generate bullet shapes by applying the same scale/size as shall be applied to the vertices of the instance mesh.
    primitive meshes should use unit value (1.0) for the extent dimensions, thus those base dimensions don't have to be multiplied in explicitly in the shape sizing calculation below.
    In some cases we have to take special care as bullet shapes don't all parameterize same way as gdx model primitives.
    Constant "DIM_HE" (primitives-half-extent) is used interchangeably to compute radius from size.x, as well as half extents where needed.
    */
/*
    public static PrimitivesBuilder getPrimitiveBuilder(final Model model, final String objectName ) {

        PrimitivesBuilder pb = getPrimitiveBuilder(objectName);
        pb.model = model;
        return pb;
    }
*/
    public static PrimitivesBuilder getPrimitiveBuilder(final String objectName) {

        PrimitivesBuilder pb = null;

        if (objectName.contains("box")) {
// bulletshape given in file but get box builder is tied to it already
            pb = PrimitivesBuilder.getBoxBuilder(objectName); // this constructor could use a size param ?
        }
        else if (objectName.contains("sphere")) {
// bulletshape given in file but get Sphere builder is tied to it already
            pb = PrimitivesBuilder.getSphereBuilder(objectName); // this constructor could use a size param ?
        }
        else if (objectName.contains("cylinder")) {
            pb = PrimitivesBuilder.getCylinderBuilder(objectName); // currently I don't have a cylinder builder with name parameter for texturing
        }
        else if (objectName.contains("capsule")) {
            pb = PrimitivesBuilder.getCapsuleBuilder(objectName); // currently I don't have a cylinder builder with name parameter for texturing
        }
        else if (objectName.contains("cone")) {
            pb = PrimitivesBuilder.getConeBuilder(objectName); // currently I don't have a cylinder builder with name parameter for texturing
        }

        return pb;
    }

    /*
     *  re "obtainStaticNodeShape()"
     *   https://github.com/libgdx/libgdx/wiki/Bullet-Wrapper---Using-models
     *  "collision shape will share the same data (vertices) as the model"
     *
     *  need to look at this comment again ? ...
     *   "in some situations having issues (works only if single node in model, and it has no local translation - see code in Bullet.java)"
     */
    public static btCollisionShape getShape(String shapeName, Vector3 dimensions, Node node, Mesh mesh) {

        btCollisionShape shape = null;

        if (null == dimensions)
            dimensions = new Vector3(1, 1, 1);

        if (shapeName.equals("convexHullShape")) {

            if (null != mesh) {
                shape = MeshHelper.createConvexHullShape(mesh);
            }
            else if (null != node) {
                shape = MeshHelper.createConvexHullShape(node);
//            int n = ((btConvexHullShape) shape).getNumPoints(); // GN: optimizes to 8 points for platform cube
            }

        } else if (shapeName.equals("triangleMeshShape")) {

            shape = Bullet.obtainStaticNodeShape(node, false);

        } else if (shapeName.equals("btBoxShape")) {

            shape = new btBoxShape(dimensions.scl(0.5f));

        } else { // default?

            shape = new btSphereShape(dimensions.scl(0.5f).x);
        }
        return shape;
    }

    public static btCollisionShape getShape(final String objectName, Vector3 size) {

        PrimitivesBuilder pb = getPrimitiveBuilder(objectName);
        btCollisionShape shape = null;

        if (null != pb)
            shape = pb.getShape(size);

        return shape;
    }

    public static PrimitivesBuilder getSphereBuilder(final String objectName) {
        return new PrimitivesBuilder() {
            @Override
            public Entity create(float mass, Vector3 trans, Vector3 size) {
                return load(this.model, objectName, getShape(size), size, mass, trans);
            }
            @Override
            public btCollisionShape getShape(Vector3 size) {
                return new btSphereShape(size.x * DIM_HE);
            }
        };
    }

    public static PrimitivesBuilder getBoxBuilder(final String objectName) {
        return new PrimitivesBuilder() {
            @Override
            public Entity create(float mass, Vector3 trans, Vector3 size) {
                return load(this.model, objectName, getShape(size), size, mass, trans);
            }
            @Override
            public btCollisionShape getShape(Vector3 size) {
                return new btBoxShape(size.cpy().scl(DIM_HE));
            }
        };
    }

    private static PrimitivesBuilder getConeBuilder(final String objectName) {
        return new PrimitivesBuilder() {
            @Override
            public Entity create(float mass, Vector3 trans, Vector3 size) {
                return load(this.model, objectName, getShape(size), size, mass, trans);
            }
            @Override
            public btCollisionShape getShape(Vector3 size) {
                return new btConeShape(size.x * DIM_HE, size.y);
            }
        };
    }

    public static PrimitivesBuilder getCapsuleBuilder(final String objectName) {
        return new PrimitivesBuilder() {

            @Override
            public btCollisionShape getShape(Vector3 size) {
                // btcapsuleShape() takes actual radius parameter (unlike cone/cylinder which use width+depth)
                //  so we apply half extent factor to our size.x here.
                float radius = size.x * DIM_HE;
                // btcapsuleShape total height is height+2*radius (unlike gdx capsule mesh where height specifies TOTAL height!
                //  (http://bulletphysics.org/Bullet/BulletFull/classbtCapsuleShape.html#details)
                // determine the equivalent bullet-compatible height parameter by explicitly scaling
                // the base mesh height and then subtracting the (scaled) end radii
                float height = DIM_CAPS_HT * size.y - size.x * DIM_HE - size.x * DIM_HE;

                return new btCapsuleShape(radius, height);
            }
        };
    }

    private static PrimitivesBuilder getCylinderBuilder(final String objectName) {
        return new PrimitivesBuilder() {

            @Override
            public btCollisionShape getShape(Vector3 size) {

                return new btCylinderShape(size.cpy().scl(DIM_HE));
            }
        };
    }

    public static btCollisionShape load(
            ModelInstance instance, btCollisionShape shape, Vector3 size, Vector3 translation) {

        if (null != size) {
            instance.nodes.get(0).scale.set(size);
            instance.calculateTransforms();
        }
        // leave translation null if using translation from the model layout
        if (null != translation) {
            instance.transform.trn(translation);
        }

        return shape;
    }

    public static Entity load(
            Model model, String nodeID, btCollisionShape shape, Vector3 size, float mass, Vector3 translation) {

        // we can roll the instance scale transform into the getModelInstance ;)
        ModelInstance instance = ModelInstanceEx.getModelInstance(model, nodeID);

        load(instance, shape, size, translation);

        BulletComponent bc = new BulletComponent(shape, instance.transform, mass);

        if (0 == mass) {
            // special sauce here for static entity
// set these flags in bullet comp?
            bc.body.setCollisionFlags(
                    bc.body.getCollisionFlags() | btCollisionObject.CollisionFlags.CF_KINEMATIC_OBJECT);
            bc.body.setActivationState(Collision.DISABLE_DEACTIVATION);
        }

        Entity e  = new Entity();
        e.add(new ModelComponent(instance));
        e.add(bc);

        return e;
    }

    public static void dispose() {
        // The Model owns the meshes and textures, to dispose of these, the Model has to be disposed. Therefor, the Model must outlive all its ModelInstances
//  Disposing the primitivesModel will automatically make all instances invalid!
        model.dispose();
        model = null;
    }
}
