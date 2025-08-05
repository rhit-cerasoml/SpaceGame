package game.util.physics;

public class Transform {
    private float x, y, t;

    public Transform(float x, float y, float t){
        this.x = x;
        this.y = y;
        this.t = t;
    }

    public Transform(float x, float y){
        this(x, y, 0);
    }

    public float getX(){
        return x;
    }

    public float getY(){
        return y;
    }

    public float getT(){
        return t;
    }
}
