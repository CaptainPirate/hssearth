package com.hss.saveEarth.game;

import com.hss.savaEarth.*;
import com.hss.savaEarth.impl.*;

import android.view.*;
import android.graphics.*;


public class GameActivity extends AndroidGame {
    @Override
    public Screen getStartScreen() {
        return new GameScreen(this); 
    }

    
}
