package uk.co.proxying.treegravity;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameAboutToStartServerEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import uk.co.proxying.treegravity.listeners.WorldListener;

@Plugin(
        id = "treegravity",
        name = "TreeGravity",
        description = "Adds gravity to tree logs",
        authors = {
                "Proxying"
        }
)
public class TreeGravity {

    @Inject
    private Logger logger;

    @Inject
    private Game game;

    private static TreeGravity instance;

    @Listener
    public void preInit(GamePreInitializationEvent event) {
        instance = this;
    }

    @Listener
    public void aboutToStart(GameAboutToStartServerEvent event) {
        EventManager em = game.getEventManager();
        em.registerListeners(this, new WorldListener());
    }

    @Listener
    public void onServerStart(GameStartedServerEvent event) {
    }

    public Logger getLogger() {
        return this.logger;
    }

    public static TreeGravity getInstance() {
        return instance;
    }
}
