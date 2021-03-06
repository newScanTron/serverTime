package collector.src.tileMapStuff;

import main.DBOps;
import main.Driver;

import java.awt.*;

public class Entity {
	/** The x and y position of this entity in terms of grid cells
     * that make up the map object*/
	protected float x;
	protected float y;
	protected Map map;
	/** The size of this entity, this is used to calculate collisions with walls */
	private float size = 0.4f;
	private boolean grounded = false;
    protected int score = 0;
    protected Color playColor = new Color(203, 200, 106);
    private Thread scoreThread = new Thread(new UpdateScore());

    public Entity(Map map, float x, float y)
    {
        this.map = map;
        this.x = x;
        this.y = y;
    }
    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public void setScore(int add)
    {
        this.score += add;
    }

    public boolean move(float dx, float dy)
    {
        float nx = x + dx;
		float ny = y + dy;
        // check if the new position of the entity collides with
		// anything
        if (validLocation(nx, ny)) {
			// if it doesn't then change our position to the new position
			x = nx;
			y = ny;
			if (map.isCoin(x, y, 1))
            {
                map.setClear((int)x, (int)y);
                setScore(1);
                scoreThread = new Thread(new UpdateScore());
                scoreThread.start();
                System.out.println(score);
            }
			return true;
		}
		return false;
	}

	public boolean validLocation(float nx, float ny)
    {

        grounded = false;
        if (map.isBlocked(nx, ny))
        {
            return false;
        }
        if (map.isBlocked(nx + size, ny))
        {
            return false;
        }
        if (map.isBlocked(nx, ny + size))
        {
            grounded = true;
            return false;
        }
        if (map.isBlocked(nx + size, ny + size))
        {
            grounded = true;
            return false;
        }
        return true;
    }

    // check to see if Entity is grounded
    public boolean isGrounded()
    {
        grounded = true;
        if (map.isBlocked(x, y +size))
        {
            grounded = true;
        }
        return grounded;
    }

	public void paint(Graphics g) {
		int xp = (int) (Map.TILE_SIZE * x);
		int yp = (int) (Map.TILE_SIZE * y);
		g.setColor(playColor);
        g.fillRect(xp, yp, 10, 10);
	}
	
	private class UpdateScore implements Runnable {

		public void run() {
			int DBscore = Integer.parseInt((DBOps.getData("scores", "1", "id", "Collector")).get(0)) + 5;
            DBOps.updateData("scores", "Collector", "" + DBscore, "id", "1");
            DBscore = Integer.parseInt((DBOps.getData("users", Driver.currentUser.getName(), "user", "scoredCollector")).get(0)) + 5;
			DBOps.updateData("users", "scoredCollector", "" + DBscore, "user", Driver.currentUser.getName() );
            return;
		}
		
	}
}
