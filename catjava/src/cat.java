import java.io.BufferedReader;
import java.io.FileReader;

public class cat {
	public static void main(final String... rArg) throws Exception {
		if (rArg.length < 1) {
			System.err.println("usage: cat filename [lines]");
			System.exit(1);
		}
		int maxlin = 40;
		if (rArg.length >= 2) {
			maxlin = Integer.valueOf(rArg[1]).intValue();
		}
		final FileReader fr = new FileReader(rArg[0]);
		final BufferedReader br = new BufferedReader(fr);
		final byte[] rb = new byte[1024];

		int lin = 0;
		String s = br.readLine();
		boolean quit = false;
		while (s != null && !quit) {
			if (lin >= maxlin) {
				System.out.print(">");
				System.out.flush();
				int c = System.in.available();
				while (c == 0) {
					Thread.sleep(10);
					c = System.in.available();
				}
				while (c > 0) {
					System.in.read(rb, 0, c);
					if (rb[0] == 'q')
						quit = true;
					Thread.sleep(10);
					c = System.in.available();
				}
				lin = 0;
			}
			if (!quit) {
				System.out.println(s);
				++lin;
				s = br.readLine();
			}
		}
	}
}
