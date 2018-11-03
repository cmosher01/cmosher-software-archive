import geb.Page

class SurefireHomePage extends Page {
    protected static url = "http://maven.apache.org/surefire/"
    protected static at =
        {
            waitFor(30.0) { title.contains("Surefire") }
        }
}
