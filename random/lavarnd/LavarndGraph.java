import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.StringTokenizer;


public class LavarndGraph {
  public static void main(String[] args) throws IOException {
    final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
    for (String line = in.readLine(); line != null; line = in.readLine()) {

      final StringTokenizer strtok = new StringTokenizer("|");
      //                   0|        test_name   |num|ntup| tsamples |psamples|  p-value |Assessment
      //20110301031301.log:2|    diehard_2dsphere| 11|   2|      8000|     100|0.06500592|  PASSED
      final String date = strtok.nextToken().trim().substring(0,8);
      final String test = strtok.nextToken().trim();
      strtok.nextToken(); // num
      final Integer ntup = Integer.valueOf(strtok.nextToken().trim());
      strtok.nextToken(); // tsamples
      strtok.nextToken(); // psamples
      final String p = strtok.nextToken().trim();

      final String file = String.format("%1$s_%2$02.2d",test,ntup);

      final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(file),true)));
      out.write(date);
      out.write(' ');
      out.write(p);
      out.newLine();
      out.flush();
      out.close();
    }
  }
}
