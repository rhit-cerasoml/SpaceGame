public class Util {
    public static byte[] writeInt(int v){
        byte[] buf = new byte[4];

        for (int i = 4; i != 0; i--) {
            buf[i - 1] = (byte)(v & 0xFF);
            v >>= 8;
        }

        return buf;
    }

    public static int readInt(byte[] d){
        int val = 0;

        for (int i = 0; i < 4; i++) {
            val <<= 8;
            val |= Byte.toUnsignedInt(d[i]);
        }

        return val;
    }
}
