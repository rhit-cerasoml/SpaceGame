package test;

import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

public class Util {
    public static ByteBuffer storeArrayInBuffer(double[] array) {

        //8 bytes (64-bits) in a double, multiplied by the number of values in the array
        ByteBuffer buffer = BufferUtils.createByteBuffer(array.length * 8);

        for(double i : array) {
            buffer.putDouble(i);
        }

        buffer.position(0);

        return buffer;
    }

    public static ByteBuffer storeArrayInBuffer(int[] array) {

        //8 bytes (64-bits) in a double, multiplied by the number of values in the array
        ByteBuffer buffer = BufferUtils.createByteBuffer(array.length * 4);

        for(int i : array) {
            buffer.putInt(i);
        }

        buffer.position(0);

        return buffer;
    }
}
