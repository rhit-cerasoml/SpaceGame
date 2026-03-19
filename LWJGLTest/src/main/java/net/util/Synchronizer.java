package net.util;

public class Synchronizer<T> {
    public final Serializer<T> serializer;
    public final Deserializer<T> deserializer;
    public Synchronizer(Serializer<T> serializer, Deserializer<T> deserializer){
        this.serializer = serializer;
        this.deserializer = deserializer;
    }
}
