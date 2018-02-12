package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.bullet.dynamics.btRigidBody;
import com.mygdx.game.Components.BulletComponent;
import com.mygdx.game.Components.PlayerComponent;
import com.mygdx.game.InputReceiverSystem;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.SliderForceControl;
import com.mygdx.game.screens.MainMenuScreen;

import java.util.Random;

import static com.badlogic.gdx.math.MathUtils.cos;
import static com.badlogic.gdx.math.MathUtils.sin;

/**
 * Created by mango on 1/23/18.
 */


/* idea for player camera placment (chaseer
"We have a main character, who has a main node (the actor), a sight node (the point the character is
supposed to be looking at), and a chase camera node (where we think the best chasing camera should
be placed). "
 */


public class PlayerSystem extends EntitySystem implements EntityListener, InputReceiverSystem {

    // magnitude of force applied (property of "vehicle" type?)
    private static /* final */ float forceMag = 12.0f;

    /* kinetic friction? ... ground/landscape is not dynamic and doesn't provide friction!
     * ultimately, somehow MU needs to be a property of the "surface" player is contact with and
     * passed as parameter to the friction computation .
      * Somehow, this seems to work well - the vehicle accelerates only to a point at which the
      * velocity seems to be limited and constant ... go look up the math eventually */
    private static final float MU = 0.5f;


    //    private Engine engine;
    private PlayerComponent playerComp = null;
    private btRigidBody plyrPhysBody = null;
    private MyGdxGame game;

    // working variables
    private static Matrix4 tmpM = new Matrix4();
    private static Vector3 tmpV = new Vector3();
    private static Random rnd = new Random();
    public /* private */ static Vector3 forceVect = new Vector3(); // allowed this to be seen for debug info


    public PlayerSystem(MyGdxGame game) {
        this.game = game;
    }

    @Override
    public void addedToEngine(Engine engine) {

//        this.engine = engine;

        engine.addEntityListener(Family.all(PlayerComponent.class).get(), this);
    }

    public void updateV(float x, float y) {
        playerComp.inpVect.x = x;
        playerComp.inpVect.y = y;
    }

    public void onTouchDown(Vector2 xy) {
        updateV(xy.x, xy.y);
    }

    public void onTouchUp(Vector2 xy) {
        updateV(xy.x, xy.y);
    }

    public void onTouchDragged(Vector2 xy) {
        updateV(xy.x, xy.y);
    }

    public void onButton() {
        onJumpButton();
    }


    public void onJumpButton() {

        // random flip left or right
        if (rnd.nextFloat() > 0.5f)
            tmpV.set(0.1f, 0, 0);
        else
            tmpV.set(-0.1f, 0, 0);

        plyrPhysBody.applyImpulse(forceVect.set(0, rnd.nextFloat() * 10.f + 40.0f, 0), tmpV);
    }


    @Override
    public void update(float delta) {

        final float DZ = 0.25f; // actual number is irrelevant if < deadzoneRadius of TouchPad

        // rotate by a constant rate according to stick left or stick right.
        // note: rotation in model space - rotate around the Z (need to fix model export-orientation!)
        float degrees = 0;
        if (playerComp.inpVect.x < -DZ) {
            degrees = 1;
        } else if (playerComp.inpVect.x > DZ) {
            degrees = -1;
        }

        // use sin/cos to develop unit vector of force apply based on the ships heading
        Quaternion r = plyrPhysBody.getOrientation();
        float yaw = r.getYawRad();
        //            tmpV.rotate(0, 1, 0, yaw);
        forceVect.x = -sin(yaw);
        forceVect.y = 0;     // note Y always 0 here, force always parallel to XZ plane ... for some reason  ;)
        forceVect.z = -cos(yaw);

        if (playerComp.inpVect.y > DZ) {
            // reverse thrust & "steer" opposite direction !
            forceVect.scl(-1);
            degrees *= -1;
        } else if (!(playerComp.inpVect.y < -DZ)) {
            forceVect.set(0, 0, 0);
        }


        // TODO: check for contact w/ surface, only apply force if in contact, not falling
        SliderForceControl.comp(delta, // eventually we should take time into account not assume 16mS?
                plyrPhysBody, forceVect, forceMag, MU, playerComp.mass);


// for dynamic object you should get world trans directly from rigid body!
        plyrPhysBody.getWorldTransform(tmpM);
        tmpM.getTranslation(tmpV);

        if (tmpV.y < -20) {
            game.setScreen(new MainMenuScreen(game)); // TODO: status.alive = false ...
        }


// we should maybe be using torque for this to be consistent in dealing with our rigid body player!
        tmpM.rotate(0, 1, 0, degrees); // does not touch translation ;)

        plyrPhysBody.setWorldTransform(tmpM);


        updateChaseNode(playerComp.chaseNode, tmpM, 2.0f, 3.0f);
    }


    static Quaternion quat = new Quaternion();

    /*
    * this should be eventually put in its own class, player isn't the only thing could use a chase node!
     */
    public static void updateChaseNode(
            Vector3 targetPosition, Matrix4 actorTransform, float height, float dist) {

        targetPosition.set(actorTransform.getTranslation(tmpV));

        // offset to maintain position above subject ...
        targetPosition.y += height;

        // ... and then determine a point slightly "behind"
        // take negative of unit vector of players orientation
        actorTransform.getRotation(quat);

        // hackme ! this is not truly in 3D!
        float yaw = quat.getYawRad();

        float dX = sin(yaw);
        float dZ = cos(yaw);
        targetPosition.x += dX * dist;
        targetPosition.z += dZ * dist;
    }


    @Override
    public void entityAdded(Entity entity) {

        // TODO: only allow one player ... assertion that these
        // state variables are not initialized (null)

//        if (null != entity.getComponent(PlayerComponent.class))
        {
            playerComp = entity.getComponent(PlayerComponent.class);
            plyrPhysBody = entity.getComponent(BulletComponent.class).body;
        }
    }

    @Override
    public void entityRemoved(Entity entity) {
    }
}
