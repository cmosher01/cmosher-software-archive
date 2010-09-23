package problem.shape;

public abstract class Circle extends Shape
{
	private double _x, _y, _r;

	public Circle(double x, double y, double r)
	{
		_x = x;
		_y = y;
		_r = r;
	}

	@Override
	public void draw()
	{
		drawCircle(_x,_y,_r);
	}

	abstract protected void drawCircle(double x, double y, double r);
}
