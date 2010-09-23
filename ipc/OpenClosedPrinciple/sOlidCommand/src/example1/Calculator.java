package example1;

public class Calculator
{
	private int current = 0;
	public void operation(char operator, int operand)
	{
		switch (operator)
		{
			case '+':
				current += operand;
			break;
			case '-':
				current -= operand;
			break;
			case '*':
				current *= operand;
			break;
			case '/':
				current /= operand;
			break;
		}
		System.out.println(current);
	}
}
