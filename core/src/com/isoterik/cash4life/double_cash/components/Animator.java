package com.isoterik.cash4life.double_cash.components;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.isoterik.mgdx.Component;
import com.isoterik.mgdx.Transform;
import com.isoterik.mgdx.utils.WorldUnits;

public class Animator extends Component {
    private Actor actor;

    public Animator() {
        // Create a new actor
        actor = new Actor();
    }

    public Actor getActor() {
        return actor;
    }

    @Override
    public void start() {
        // Attach the actor to a stage
        scene.getCanvas().addActor(actor);

        // Make sure the actor's features match the host game object
        Transform transform = gameObject.transform;
        WorldUnits worldUnits = scene.getMainCamera().getWorldUnits();

        actor.setScale(transform.getScaleX(), transform.getScaleY());
        actor.setSize(worldUnits.toPixels(transform.getWidth()), worldUnits.toPixels(transform.getHeight()));
        actor.setRotation(transform.getRotation());
        actor.setOrigin(worldUnits.toPixels(transform.getOriginX()), worldUnits.toPixels(transform.getOriginY()));
        actor.setPosition(worldUnits.toPixels(transform.getX()), worldUnits.toPixels(transform.getY()));
    }

    @Override
    public void update(float deltaTime) {
        // Update the game object
        Transform transform = gameObject.transform;
        WorldUnits worldUnits = scene.getMainCamera().getWorldUnits();

        transform.setScale(actor.getScaleX(), actor.getScaleY());
        transform.setSize(worldUnits.toWorldUnit(actor.getWidth()), worldUnits.toWorldUnit(actor.getHeight()));
        transform.setRotation(actor.getRotation());
        transform.setOrigin(worldUnits.toWorldUnit(actor.getOriginX()), worldUnits.toWorldUnit(actor.getOriginY()));
        transform.setPosition(worldUnits.toWorldUnit(actor.getX()), worldUnits.toWorldUnit(actor.getY()));
    }
}


























