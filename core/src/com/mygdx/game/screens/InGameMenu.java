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

package com.mygdx.game.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ButtonGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;

/**
 * Created by neiderm on 12/18/17.
 */

/*
 * Reference:
 *  on-screen menus:
 *   https://www.gamedevelopment.blog/full-libgdx-game-tutorial-menu-control/
 *  UI skin defined programmatically:
 *   https://github.com/libgdx/libgdx/blob/master/tests/gdx-tests/src/com/badlogic/gdx/tests/UISimpleTest.java
 */

public class InGameMenu extends Stage {

    InputMapper mapper = new InputMapper();

    private int previousIncrement;
    private Array<String> buttonNames = new Array<String>();
    private ButtonGroup<TextButton> bg;
    private int count;
    Table onscreenMenuTbl = new Table();
    boolean menuSelected;

    InGameMenu() {
        super();
        bg = new ButtonGroup<TextButton>();
        bg.setMaxCheckCount(1);
        bg.setMinCheckCount(1);
    }

    void addButton(TextButton button, String name) {

        buttonNames.add(name);
        bg.add(button);

        count += 1;

        onscreenMenuTbl.row();
        onscreenMenuTbl.add(button).fillX().uniformX();

        // action is same regardless so one change listene
        button.addListener(new ChangeListener() {

            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {

                Gdx.app.log("InGameMeu", "button changed \"" + actor + "\"");
            }
        });
    }

    /*
     * returns true if new checked box is different from present one ^H^H^H^H
     */
    void setCheckedBox(int checked) {

        String name = buttonNames.get(checked);

        bg.setChecked(name);
    }

    int getCheckedIndex(){
        return bg.getCheckedIndex();
    }

/*
    int getCurrentSelection(){
        return currentSelection;
    }
*/
    int checkedUpDown(int step) {

        int checkedIndex = bg.getCheckedIndex();

        final int N_SELECTIONS = count;

        int selectedIndex = checkedIndex;

        if (0 == previousIncrement)
            selectedIndex += step;

        previousIncrement = step;

        if (selectedIndex >= N_SELECTIONS) {
//            selectedIndex = 0;
            selectedIndex = (N_SELECTIONS - 1);
        }
        if (selectedIndex < 0) {
//            selectedIndex = N_SELECTIONS - 1;
            selectedIndex = 0;
        }

//        setCheckedBox(selectedIndex);

        return selectedIndex;
    }
}
