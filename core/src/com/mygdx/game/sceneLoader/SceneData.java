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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashMap;

/* References
 *  http://niklasnson.com/programming/network/tips%20and%20tricks/2017/09/15/libgdx-save-and-load-game-data.html
 */
public class SceneData {

    public static final String LOCAL_PLAYER_FNAME = "Player"; // can  globalize the string e.g.  SceneData.LOCAL_PLAYER_FNAME

    // ignore Lint warning about using type Map ?
    public HashMap<String, GameFeature> features = new HashMap<String, GameFeature>();
    public HashMap<String, ModelGroup> modelGroups = new HashMap<String, ModelGroup>();
    public HashMap<String, ModelInfo> modelInfo = new HashMap<String, ModelInfo>();


    private static void saveData(SceneData data) {
        Json json = new Json();
        json.setOutputType(JsonWriter.OutputType.json); // see "https://github.com/libgdx/libgdx/wiki/Reading-and-writing-JSON"
        FileHandle fileHandle = Gdx.files.local("GameData_out.json");
        if (data != null) {
//            fileHandle.writeString(Base64Coder.encodeString(json.prettyPrint(gameData)), false);
            fileHandle.writeString(json.prettyPrint(data), false);
            //System.out.println(json.prettyPrint(gameData));
        }
    }


    public static SceneData loadData(String path, String playerObjectName){

        Json json = new Json();
        FileHandle fileHandle = Gdx.files.internal(path);
        //        gameData = json.fromJson(sceneData.class, Base64Coder.decodeString(fileHandle.readString()));
        SceneData sd = json.fromJson(SceneData.class, fileHandle.readString());

        // localplayer object-name is passed along from parent screen ... make a Geame Feature in which
        // to stash this "persistent" local player info . Other systems/comps will be looking for this
        // magik name to get reference to the entity.
        GameFeature gf = sd.features.get(LOCAL_PLAYER_FNAME);

        saveData(sd);  // test ... write out the data in order to verify order and format

        if (null != gf) {
            // Allow local player game feature to be defined in JSON (user Data)
            gf.setObjectName(playerObjectName);
        }
        else{
            gf = new GameFeature( playerObjectName );
            sd.features.put(LOCAL_PLAYER_FNAME, gf  /* new GameFeature( playerObjectName ) */ );
        }

        return sd;
    }
}