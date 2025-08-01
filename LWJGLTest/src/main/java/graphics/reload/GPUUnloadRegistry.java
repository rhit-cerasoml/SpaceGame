package graphics.reload;

import game.util.event.EventRegistry;

public class GPUUnloadRegistry extends EventRegistry<GPUUnloadEvent> {
    public static GPUUnloadRegistry INSTANCE = new GPUUnloadRegistry();
}
