package net.util;

public interface Deserializer<T> {
    T deserialize(SerializingInputStream in) throws SerializingInputStream.InvalidStreamLengthException;
}
