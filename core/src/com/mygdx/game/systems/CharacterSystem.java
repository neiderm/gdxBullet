package com.mygdx.game.systems;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntityListener;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.signals.Signal;
import com.badlogic.ashley.systems.IteratingSystem;
import com.mygdx.game.components.CharacterComponent;
import com.mygdx.game.util.GameEvent;

import static com.mygdx.game.util.GameEvent.EventType.RAY_DETECT;

/**
 * Created by mango on 2/10/18.
 */

public class CharacterSystem extends IteratingSystem implements EntityListener {

    private int id;
    private Signal<GameEvent> gameEventSignal; // signal queue of pickRaySystem


    public CharacterSystem(Signal<GameEvent> gameEventSignal) {

        super(Family.all(CharacterComponent.class).get());

        this.gameEventSignal = gameEventSignal;
    }


    @Override
    public void addedToEngine(Engine engine) {

        super.addedToEngine(engine);

        // listener for these so that their bullet objects can be dispose'd
        engine.addEntityListener(getFamily(), this);
    }

    @Override
    public void removedFromEngine(Engine engine) {

        engine.removeEntityListener(this); // Ashley bug (doesn't remove listener when system removed?
    }


    @Override
    public void entityAdded(Entity entity) {

//        entity.getComponent(CharacterComponent.class).gameEvent = createPickEvent(entity);
    }

    @Override
    public void entityRemoved(Entity entity) {
        //empty
    }


/*
    private Vector3 position = new Vector3();
    private Quaternion rotation = new Quaternion();
    private Vector3 direction = new Vector3(0, 0, -1); // vehicle forward
*/

    @Override
    protected void processEntity(Entity entity, float deltaTime) {

        CharacterComponent comp = entity.getComponent(CharacterComponent.class);

        if (null != comp.controller)
            comp.controller.update(deltaTime);

        if (null != comp.character) {

            comp.character.update(entity, deltaTime, comp.lookRay);
            /*
            all characters to get the object in their line-of-sight view.
            caneraOpoerator (cameraMan) to also do this.
            camera LOS would be center of screen by default, but then on touch would defer to
            coordinate of cam.getPickRay()
             */
            if (null != comp.gameEvent) {
/*
                Matrix4 transform = entity.getComponent(ModelComponent.class).modelInst.transform;
                transform.getTranslation(position);
                transform.getRotation(rotation);
                comp.lookRay.set(position, ModelInstanceEx.rotateRad(direction.set(0, 0, -1), rotation));
*/
                comp.gameEvent.set(RAY_DETECT, comp.lookRay, id++);
                gameEventSignal.dispatch(comp.gameEvent);
            }
        }
    }
}
