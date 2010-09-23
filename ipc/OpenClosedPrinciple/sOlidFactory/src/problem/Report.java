package problem;
import java.io.PrintStream;
import java.util.Date;


public interface Report
{
	void print(PrintStream out);
	void setDate(Date time);
	void setFont(String serif);
	void setMargins(double d);
}
