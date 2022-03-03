package pl.kotmisia.dankmod.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name="dankmod")
public class DankCfg implements ConfigData {
    public boolean warnUrl = false;
    public boolean warnCommand = true;
    public boolean warnClipboard = false;
}
