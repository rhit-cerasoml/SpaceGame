package game.util;

public class Transform {
    public double m0, m1, tx,
                  m2, m3, ty;
                //0 , 0 , 1 ;
    public Transform(double tx, double ty, double theta){
        this.tx = tx;
        this.ty = ty;
        this.m0 = Math.cos(theta);
        this.m2 = Math.sin(theta);
        this.m1 = -this.m2;
        this.m3 = this.m0;
    }

    public Transform(){
        this.tx = 0;
        this.ty = 0;
        this.m0 = 1;
        this.m1 = 0;
        this.m2 = 0;
        this.m3 = 1;
    }

    public Transform(double m0, double m1, double tx, double m2, double m3, double ty){
        this.tx = tx;
        this.ty = ty;
        this.m0 = m0;
        this.m1 = m1;
        this.m2 = m2;
        this.m3 = m3;
    }

    public Transform multiply(Transform o){
        double nm0 = m0 * o.m0 + m1 * o.m2;
        double nm1 = m0 * o.m1 + m1 * o.m3;
        double ntx = m0 * o.tx + m1 * o.ty + tx;

        double nm2 = m2 * o.m0 + m3 * o.m2;
        double nm3 = m2 * o.m1 + m3 * o.m3;
        double nty = m2 * o.tx + m3 * o.ty + ty;

        return new Transform(nm0, nm1, ntx, nm2, nm3, nty);
    }

    public Transform invert(){
        return new Transform(m0, m2, (-tx * m0) - (ty * m2),
                             m1, m3, (tx * m2) - (ty * m0));
    }

    public String toString(){
        return  "[" + m0 + ",\t" + m1 + ",\t" + tx + "\t]\n" +
                "[" + m2 + ",\t" + m3 + ",\t" + ty + "\t]\n" +
                "[0.0,\t0.0,\t1.0\t]";

    }
}
