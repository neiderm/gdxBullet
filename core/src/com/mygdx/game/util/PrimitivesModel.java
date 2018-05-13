package com.mygdx.game.util;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by mango on 12/18/17.
 */

public class PrimitivesModel extends SizeableEntityBuilder {

//    public static final PrimitivesModel instance = new PrimitivesModel();

    public static /*final */Model primitivesModel;
//    public static final Model boxTemplateModel;
//    public static final Model sphereTemplateModel;

    private PrimitivesModel() {
        model = primitivesModel;
    }

    static {
        final ModelBuilder mb = new ModelBuilder();

/*
        Texture cubeTex = new Texture(Gdx.files.internal("data/crate.png"), false);
        boxTemplateModel = mb.createBox(1f, 1f, 1f,
                new Material(TextureAttribute.createDiffuse(cubeTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);

        Texture sphereTex = new Texture(Gdx.files.internal("data/day.png"), false);
        sphereTemplateModel = mb.createSphere(1f, 1f, 1f, 16, 16,
                new Material(TextureAttribute.createDiffuse(sphereTex)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal | VertexAttributes.Usage.TextureCoordinates);
*/

        mb.begin();

        mb.node().id = "sphere";
        mb.part("sphere", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.GREEN))).sphere(1f, 1f, 1f, 10, 10);
//                new Material(ColorAttribute.createDiffuse(Color.GREEN), IntAttribute.createCullFace(GL_BACK))).sphere(1f, 1f, 1f, 10, 10);
        mb.node().id = "box";
        mb.part("box", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.BLUE))).box(1f, 1f, 1f);
        mb.node().id = "cone";
        mb.part("cone", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.YELLOW))).cone(1f, 1f, 1f, 10);
        mb.node().id = "capsule";
        mb.part("capsule", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.CYAN))).capsule(1f * SizeableEntityBuilder.primHE, SizeableEntityBuilder.primCapsuleHt, 10); // note radius and height vs. bullet
        mb.node().id = "cylinder";
        mb.part("cylinder", GL20.GL_TRIANGLES, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal,
                new Material(ColorAttribute.createDiffuse(Color.MAGENTA))).cylinder(1f, 1f, 1f, 10);

        primitivesModel = mb.end();
    }

    public static Entity loadSphere(float r, Vector3 pos){
        return BaseEntityBuilder.load(
                primitivesModel, "sphere", new Vector3(r, r, r), pos);
    }

    public static Entity loadCone(float mass, Vector3 trans, Vector3 size) {
        return coneTemplate.create(primitivesModel, "cone", mass, trans, size);
    }
    public static Entity loadCapsule(float mass, Vector3 trans, Vector3 size) {
        return capsuleTemplate.create(primitivesModel, "capsule", mass, trans, size);
    }
    public static Entity loadCylinder(float mass, Vector3 trans, Vector3 size) {
        return cylinderTemplate.create(primitivesModel, "cylinder", mass, trans, size);
    }
    public static Entity loadBox(float mass, Vector3 trans, Vector3 size) {
        return boxTemplate.create(primitivesModel, "box", mass, trans, size);
    }

/*    @Override
    public void dispose() {
        trash();
    }*/

    public static void trash(){
        // The Model owns the meshes and textures, to dispose of these, the Model has to be disposed. Therefor, the Model must outlive all its ModelInstances
//  Disposing the primitivesModel will automatically make all instances invalid!
/*
        sphereTemplateModel.dispose();
        boxTemplateModel.dispose();
*/
        primitivesModel.dispose();
        primitivesModel = null;
    }
}
