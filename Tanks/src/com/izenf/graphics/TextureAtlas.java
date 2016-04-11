package com.izenf.graphics;

import com.izenf.utils.ResourceLoader;

import java.awt.image.BufferedImage;

/**
 * Created by izenf on 28.03.2016.
 */
public class TextureAtlas {
    BufferedImage image;

    public TextureAtlas(String imageName){
        image = ResourceLoader.loadImage(imageName);
    }

    public BufferedImage cut(int x, int y, int w, int h){
        return image.getSubimage(x,y,w,h);
    }

}
