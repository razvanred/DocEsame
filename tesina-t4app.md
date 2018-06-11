# T4App

Nel giugno del 2017 io ed un compagno di scuola della sezione AII dello stesso anno, Federico Bono, abbiamo iniziato a lavorare presso l'azienda T4Group S.R.L. mediante il programma dell'alternanza della scuola-lavoro.
Abbiamo avuto il compito di progettare un'applicazione Android ed un portale Web per la gestione degli agenti (i venditori), dei clienti, dei relativi ordini e delle relative visite, degli scaduti (i _"debiti"_ dei singoli clienti), delle promozioni e delle statistiche generali.
Io mi sono occupato dell'applicativo Android (grazie alle poche esperienze passate con questa piattaforma), mentre Federico Bono si è occupato del portale (una Web Application) e del relativo Web service (da cui sia la Web Application che l'applicazione Android vanno ad eseguire chiamate, le informazioni vengono scambiate mediante il protocollo HTTP e la risorsa o l'insieme di risorse in questo caso viene identificata dall'URI e dal _verbo_ HTTP, tra cui GET, POST, DELETE), ed ancora oggi ci impegnamo entrambi a migliorare i servizi offerti.

Grazie a quest'esperienza lavorativa sono riuscito a consolidare le mie conoscenze sul mondo Android, sul mondo dei database e di quello relativo ai Web service.

Come ambiente di sviluppo ho usato Android Studio, l'IDE ufficiale per lo sviluppo di applicazioni Android. Si può dire che questo ambiente di sviluppo è una versione pesantemente modificata di un altro IDE Java famoso completamente open-source denominato IntelliJ IDEA (la versione _Community Edition_, la versione Ultimate è sotto licenza proprietaria), sviluppato dalla software house JetBrains

Nel corso dell'anno ho rilasciato diverse versioni di quest'applicazione, tuttavia, si possono distinguere ben 3 revisioni generali del codice dall'originale.

Nel codice sorgente della prima versione per la comunicazione con il server e per lo scaricamento dei dati ho usato molte classi e metodologie di approccio ai problemi appartenenti al mondo Java.

Per effettuare il collegamento al server ho impeigato l'utilizzo di:

 * un oggetto HttpsURLConnection (classe astratta situata nel package _javax.net.ssl_) che rappresenta la connessione con il server
 * un oggetto BufferedStreamReader per leggere il contenuto dell'oggetto InputStream (classe astratta che rappresenta un input stream di bytes, quindi in questo caso contenente la risposta del server alla richiesta sotto forma di bytes) ritornato dall'istanza di HttpsURLConnection, prende in pasto l'inputstream
 * il BufferedInputStream legge solitamente _bytes_, i quali mediante un _charset_ possono essere convertiti in caratteri. Quindi ho impiegato l'utilizzo di un _Reader_ (BufferedReader)



Inizialmente, come linguaggio di programmazione ho scelto Java, essendo un linguaggio fortemente tipizzato ed orientato agli oggetti (OOP, ovvero Object Oriented Programming), dove vengono ragruppate in un'unica entità (la classe) sia le strutture dati che le procedure che operano su di esse; l'istanza della classe prende il nome di oggetto, dotato di proprietà (i dati) e metodi che operano sui dati dell'oggetto stesso.
Il meccanismo più potente ed utile di questo linguaggio per raggiungere i miei obiettivi è quello dell'ereditarietà, che mi permette di derivare nuove classi a partire da classi già definite, con la possibilità di aggiungere nuove proprietà e nuovi metodi nella classe derivata, ed eventualmente modificare il comportamento di alcuni metodi definiti nella classe sovrastante laddove è possibile
