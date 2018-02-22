import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.Random;

public class AICharacter extends Character
{
	private Point _target;

	public AICharacter(double x, double y, int width, int height, double baseSpeed, String framesPath)
	{
		super(x, y, width, height, baseSpeed, framesPath);
		Random r = new Random();
		_target = new Point(-1, -1);
		setAngle(r.nextInt(360));
		updateFinalSpeed();
		setNewTarget();
	}

	public void basicAIMovement(Player p)
	{
		updateFinalSpeed();
		if (getLoc().distance(p.getLoc()) < BlockType.getSize() * 5)
		{
			setAngleAndTarget((Math.toDegrees(-Math.atan2((p.getX() + p.getWidth() / 2)	- (getX() + getWidth() / 2),
															(p.getY() + p.getHeight() / 2) - (getY() + getHeight() / 2)))));
		}
		if (getLoc().distance(_target) < getFinalSpeed())
		{
			setNewTarget();
		}
		move(getAngle(), getFinalSpeed());
	}

	public void setNewTarget()
	{
		Random r = new Random();
		int a = r.nextInt(90) - 45;
		setAngleAndTarget((getAngle() + a) % 360);
	}

	public void setAngleAndTarget(double a)
	{
		int radius = 50;
		setAngle(a);
		_target.setLocation(getX() + radius * Math.sin(Math.toRadians(getAngle())), getY() - radius * Math.cos(Math.toRadians(getAngle())));
	}

	@Override
	public void Paint(Graphics g, boolean isDebug)
	{
		super.Paint(g, isDebug);
		g.setColor(Color.orange);
		if (isDebug)
			g.drawRect(_target.x, _target.y, 100, 100);
	}

	public Point getTarget()
	{
		return _target;
	}

	public void setTarget(Point target)
	{
		_target = target;
	}

}