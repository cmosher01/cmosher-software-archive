package example1;

public class User
{
	private final Calculator calculator = new Calculator();
	public void compute(char operator, int operand)
	{
		final Command command = new CalculatorCommand(calculator,operator,operand);
		command.execute();
	}
}
