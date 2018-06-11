import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class Principale {

    private final static int TIMEOUT = 15000;

    public static void main(final String[] args) throws RuntimeException {

        try {

            final HttpsURLConnection connection = (HttpsURLConnection) new URL("https://t4developer.herokuapp.com/versions/").openConnection();
            connection.setConnectTimeout(TIMEOUT);
            connection.setDoOutput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("User-Agent", "Mozilla/5.0");
            connection.setRequestMethod("GET");
            connection.connect();

            try {
                System.out.println("Connessione riuscita: " + readContent(connection.getInputStream()));
            } catch (final FileNotFoundException exc) {
                System.err.println("Errore nella richiesta: " + readContent(connection.getErrorStream()));
            }

        } catch (final MalformedURLException malExc) {

            throw new RuntimeException("Errore di programmazione: URL errato");

        } catch (final IOException ioExc) {

            System.err.println("Impossibile connettersi oppure errore di lettura");
            ioExc.printStackTrace();

        }
    }

    @NotNull
    private static String readContent(final InputStream in) throws IOException {

        final StringBuilder builder = new StringBuilder();
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(in);
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream, StandardCharsets.UTF_8));

        String s;

        while ((s = bufferedReader.readLine()) != null)
            builder.append(s);

        in.close();
        bufferedInputStream.close();
        bufferedReader.close();

        return builder.toString();
    }

}
