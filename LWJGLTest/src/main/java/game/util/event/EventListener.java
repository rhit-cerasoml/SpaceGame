package game.util.event;

public interface EventListener<E> {
    void fire(E event);
}
