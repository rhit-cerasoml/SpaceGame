package graphics.reload;

import game.util.event.EventRegistry;

public class GPUReloadRegistry extends EventRegistry<GPUReloadEvent> {
    public static GPUReloadRegistry INSTANCE = new GPUReloadRegistry();
}