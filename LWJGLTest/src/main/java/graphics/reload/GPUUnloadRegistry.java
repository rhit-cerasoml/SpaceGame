package graphics.reload;

import game.util.event.EventListener;
import game.util.event.EventRegistry;
import graphics.reload.events.GPUUnloadEvent;

public class GPUUnloadRegistry extends EventRegistry<GPUUnloadEvent> {
    private static GPUUnloadRegistry INSTANCE = new GPUUnloadRegistry();
    private GPUUnloadRegistry(){}
    public static void register(EventListener<GPUUnloadEvent> l){
        INSTANCE.registerListener(l);
    }

    public static void unregister(EventListener<GPUUnloadEvent> l) {
        INSTANCE.unregisterListener(l);
    }

    public static void fire(){
        INSTANCE.fireEvent(new GPUUnloadEvent());
    }
}
