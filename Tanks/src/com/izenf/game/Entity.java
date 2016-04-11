package com.izenf.game;

import com.izenf.IO.Input;

import java.awt.*;

/**
 * Created by izenf on 10.04.2016.
 */
public abstract class Entity {
    public final EntityType type;

    protected float x;
    protected float y;

    protected Entity(EntityType type, float x, float y){
        this.type = type;
        this.x = x;
        this.y = y;
    }

    public abstract void update(Input input);

    public abstract void render(Graphics2D g);

}
