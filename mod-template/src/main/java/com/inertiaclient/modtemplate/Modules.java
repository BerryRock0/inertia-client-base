package com.inertiaclient.modtemplate;

import com.inertiaclient.base.module.Module;
import com.inertiaclient.modtemplate.modules.player.AutoRespawn;
import com.inertiaclient.modtemplate.modules.render.BetterSelection;
import lombok.Getter;

import java.util.ArrayList;

public class Modules {

    @Getter
    private BetterSelection betterSelection;

    public void initialize(ArrayList<Module> modules) {
        modules.add(new AutoRespawn());
        modules.add(this.betterSelection = new BetterSelection());
    }

}
