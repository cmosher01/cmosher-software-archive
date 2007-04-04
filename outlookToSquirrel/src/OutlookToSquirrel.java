import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import nu.mine.mosher.core.StringFieldizer;

/*
 * Created on Dec 16, 2006
 */
public class OutlookToSquirrel
{
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(final String... rArg) throws IOException
	{
		final OutlookToSquirrel app = new OutlookToSquirrel();
		app.run();
	}

	private final List<String> rFieldName = new ArrayList<String>();
	private Map<String,String> mapFields;

	private void run() throws IOException
	{
		final BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(FileDescriptor.in)));
		final BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(FileDescriptor.out)));

		boolean header = true;
		for (String inLine = in.readLine(); inLine != null; inLine = in.readLine())
		{
			this.mapFields = new HashMap<String,String>();

			final StringFieldizer flds = new StringFieldizer(inLine,'\t');
			int iField = 0;
			for (final String fld : flds)
			{
				fld.replaceAll("\"","");
				if (header)
				{
					this.rFieldName.add(fld);
				}
				else
				{
					if (fld.length() > 0)
					{
						this.mapFields.put(this.rFieldName.get(iField),fld);
					}
				}
				++iField;
			}
			if (!header)
			{
				writeLine(out);
			}
			header = false;
		}
		in.close();
		out.flush();
		out.close();
	}

	private void writeLine(final BufferedWriter out) throws IOException
	{
		final String email = this.mapFields.get("E-mail Address");
		if (email == null || email.length() == 0)
		{
			// no email, so skip it
			return;
		}

		final String nameFirst = buildFirstName();

		String nameLast = this.mapFields.get("Last Name");
		if (nameLast == null)
		{
			nameLast = "";
		}

//		String key = this.mapFields.get("Nickname");
//		if (key == null || key.length() == 0)
//		{
//			key = nameFirst;
//		}
//		if (key == null || key.length() == 0)
//		{
//			key = nameLast;
//		}
//		if (key.length() == 0)
//		{
//			throw new IllegalArgumentException("No first, middle, last, or nick-name");
//		}

		out.write("|");
		out.write(nameFirst);
		out.write("|");
		out.write(nameLast);
		out.write("|");
		out.write(email);
		out.write("|");
		out.newLine();
	}

	private String buildFirstName()
	{
		String nameFirst = this.mapFields.get("First Name");
		if (nameFirst == null)
		{
			nameFirst = "";
		}
		String nameMiddle = this.mapFields.get("Middle Name");
		if (nameMiddle == null)
		{
			nameMiddle = "";
		}

		final StringBuilder sb = new StringBuilder(32);
		sb.append(nameFirst);
		if (nameFirst.length() > 0 && nameMiddle.length() > 0)
		{
			sb.append(' ');
		}
		sb.append(nameMiddle);
		return sb.toString();
	}
}
