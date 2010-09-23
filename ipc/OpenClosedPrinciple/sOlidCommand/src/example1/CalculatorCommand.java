package example1;

public class CalculatorCommand implements Command
{
	private char operator;
	private int operand;
	private Calculator calculator;

	public CalculatorCommand(Calculator calculator, char operator, int operand)
	{
		this.calculator = calculator;
		this.operator = operator;
		this.operand = operand;
	}

	@Override
	public void execute()
	{
		calculator.operation(operator,operand);
	}
}
