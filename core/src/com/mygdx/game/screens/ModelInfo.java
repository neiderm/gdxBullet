package com.mygdx.game.screens;

import com.badlogic.gdx.graphics.g3d.Model;
import com.mygdx.game.util.PrimitivesBuilder;

public class ModelInfo {

    public ModelInfo() {
    }

    public ModelInfo(String fileName) {

        this.fileName = fileName;
        this.model = PrimitivesBuilder.getModel();  // allow it to be default
    }

    public String fileName;
    public Model model;
}
