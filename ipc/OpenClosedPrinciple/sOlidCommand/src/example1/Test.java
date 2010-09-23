package example1;

public class Test
{
	public static void main(String[] args)
	{
		// Create user and let her compute
		User user = new User();
		user.compute('+',100);
		user.compute('-',50);
		user.compute('*',10);
		user.compute('/',2);
	}
}
