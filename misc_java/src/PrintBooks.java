import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.sourceforge.templat.Templat;
import net.sourceforge.templat.exception.TemplateLexingException;
import net.sourceforge.templat.exception.TemplateParsingException;

public class PrintBooks
{
    // A simplified structure that represents one book
    public static class Book
    {
        private final String author;
        private final String title;

        public Book(String a, String t) { author = a; title = t; }

        public String getAuthor() { return this.author;}
        public String getTitle() { return this.title;}
    }

    public static void main(String[] arg) throws TemplateLexingException, TemplateParsingException, IOException
    {
        // This is our inventory of books:
        List books = new ArrayList();
        books.add(new Book("Rudyard Kipling","The Jungle Book"));
        books.add(new Book("Mary Shelley","Frankenstein"));
        books.add(new Book("Oscar Wilde","The Picture of Dorian Gray"));

        // Get our displayBooks template:
        Templat tat = new Templat(PrintBooks.class.getResource("displayBooks.tat"));

        /*
         * Render the template, passing our array of books for
         * the argument, and put the result into the StringBuilder.
         */
        Appendable result = new StringBuilder();
        tat.render(result,books);

        // Print out the result
        System.out.println(result);
    }
}