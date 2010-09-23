package solution;
import java.io.PrintStream;
import java.util.Date;


public interface Report
{
	void setFont(String serif);
	void setMargins(double d);
	void setDate(Date time);
	void print(PrintStream out);
}
