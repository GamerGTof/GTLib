package gt.gtlib;

import gt.gtlib.command.*;
import gt.gtlib.command.DefaultCommandHandler;
import gt.gtlib.command.mapped.MappedCommand;
import gt.gtlib.utils.Commands;
import org.bukkit.plugin.java.JavaPlugin;


public class GTLib extends JavaPlugin {
    private static GTLib instance;

    @Override
    public void onEnable() {
        instance = this;
    }


    public int getLen(String s) {
        return s.length();
    }

    public static GTLib getInstance() {
        return instance;
    }
}
