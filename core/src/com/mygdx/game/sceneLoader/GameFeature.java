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

package com.mygdx.game.sceneLoader;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.features.FeatureAdaptor;

public class GameFeature {

    public String featureName;
    private Entity entity;


    // this is only being used for type information ... it is instanced in SceneeData but the idea
    // is for each game Object (Entity) to have it's own feature adatpr instance
    FeatureAdaptor featureAdaptor;

    public Vector3 vSnfi;


    public GameFeature(){
    }

    public GameFeature(String featureName){

        this.featureName = featureName;
    }

    public void setEntity(Entity entity){
        this.entity = entity;
    }
    public Entity getEntity(){
        return this.entity;
    }
}
