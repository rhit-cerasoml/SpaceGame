package net.util;

public class Synchronizers {
    public static final Synchronizer<String> STRING_SYNCHRONIZER = new Synchronizer<>((item, out) -> out.writeString(item), SerializingInputStream::readString);
    public static final Synchronizer<Integer> INTEGER_SYNCHRONIZER = new Synchronizer<>((item, out) -> out.writeInt(item), SerializingInputStream::readInt);
}
