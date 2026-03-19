package net.util;

public interface Serializer<T> {
    void serialize(T item, SerializingOutputStream out);
}
