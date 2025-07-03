package game.util.event;

import java.util.ArrayList;

public class EventRegistry<T> {
    private ArrayList<EventListener<T>> listeners = new ArrayList<>();
    public void registerListener(EventListener<T> listener){
        listeners.add(listener);
    }

    public void unregisterListener(EventListener<T> listener){
        if(event_firing) active_removed = true;
        listeners.remove(listener);
    }

    private boolean event_firing = false;
    private boolean active_removed = false;
    public void fireEvent(T event){
        event_firing = true;
        for(int i = 0; i < listeners.size(); i++){
            listeners.get(i).fire(event);
            if(active_removed){
                active_removed = false;
                i--;
            }
        }
        event_firing = false;
    }
}
