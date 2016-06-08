import java.io.Serializable;

public class GameState implements Serializable{
    public int x;
    public int y;
    public boolean shot;
    public Direction direction;

    GameState(int x, int y, boolean shot, Direction direction){
        this.x = x;
        this.y = y;
        this.shot = shot;
        this.direction = direction;
    }

    public int getX(){
        return x;
    }
}
