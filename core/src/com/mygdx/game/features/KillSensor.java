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
package com.mygdx.game.features;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.TextureAttribute;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.components.CompCommon;

/**
 * Created by neiderm on 7/5/2019.
 * <p>
 * This can be a "generic" handler for a sensor. assigned a single target Entity to be sensing for.
 */
class KillSensor {

    public enum ImpactType {
        FATAL,
        DAMAGING,
        ACQUIRE
    }

    /*
    The thing that is going 'gaBoom' should be able to specify Material texture,  Color Attr. only)
    (or else if no Texture Attrib. then we assign a default (fire-y!!) one! ?

     IN: points : because floating signboarded  points
    */
    static void makeBurnOut(ModelInstance mi, KillSensor.ImpactType useFlags) {

        Material saveMat = mi.materials.get(0);

        TextureAttribute tmpTa = (TextureAttribute) saveMat.get(TextureAttribute.Diffuse);
        TextureAttribute fxTextureAttrib = null;

        if (null != tmpTa) {
            Texture tt = tmpTa.textureDescription.texture;
            fxTextureAttrib = TextureAttribute.createDiffuse(tt);

//            fxTextureAttrib = (TextureAttribute)tmpTa.copy(); // idfk maybe toodo
        }

        Vector3 translation = new Vector3(); // tmp for new vector instance .. only need to feed the GC relavitvely few of thsesei guess

        CompCommon.spawnNewGameObject(
                null, mi.transform.getTranslation(translation),
                new BurnOut(fxTextureAttrib, useFlags), "sphere");
    }
}
