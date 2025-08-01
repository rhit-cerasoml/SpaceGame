package graphics.reload;

import game.util.event.EventListener;
import game.util.event.EventRegistry;

public class GPUReloadRegistry extends EventRegistry<GPUReloadEvent> {
    private static GPUReloadRegistry INSTANCE = new GPUReloadRegistry();
    private GPUReloadRegistry(){}
    public static void register(EventListener<GPUReloadEvent> l){
        INSTANCE.registerListener(l);
    }

    public static void unregister(EventListener<GPUReloadEvent> l) {
        INSTANCE.unregisterListener(l);
    }

    public static void fire(){
        INSTANCE.fireEvent(new GPUReloadEvent());
    }
}