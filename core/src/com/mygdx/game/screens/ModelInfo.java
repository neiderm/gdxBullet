package com.mygdx.game.screens;

import com.badlogic.gdx.graphics.g3d.Model;
import com.mygdx.game.util.PrimitivesBuilder;

public class ModelInfo {

public     ModelInfo() {
    }

    public     ModelInfo(String fileName) {
        this.fileName = fileName;
    }

    public String fileName;
    public Model model = PrimitivesBuilder.primitivesModel;  // allow it to be default
}
