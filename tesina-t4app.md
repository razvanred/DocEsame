# T4App

## Introduzione

Nel giugno del 2017 io ed un compagno di scuola della sezione AII dello stesso anno, Federico Bono, abbiamo iniziato a lavorare presso l'azienda T4Group S.R.L. mediante il programma dell'alternanza della scuola-lavoro.
Abbiamo avuto il compito di progettare un'applicazione Android ed un portale (una Web app) per la gestione degli agenti (i venditori) e dei dati aziendali.

Inanzitutto, ci è stato presentato il problema e abbiamo discusso sulla possibile realizzazione prima di cominciare la seconda fase di alternanza scuola/lavoro.

Io mi sono occupato dell'applicativo Android (grazie alle esperienze passate con questa piattaforma), mentre Federico Bono si è occupato del portale (una Web Application) e della RESTful API (ho usato il termine API perché rappresenta un insieme di procedure disponibili al programmatore) da cui dipende l'applicazione Android. Inoltre, Federico ha realizzato un piccolo database contente una parte dei dati aziendali, usato sia del portale che dalla RESTful API. Questo perché l'azienda ha al suo interno un gestionale con una sua base di dati, dove parte di essi possono essere esportati su file Excel: questi file excel possono essere usati per importare i dati sulla base di dati creata da Federico mediante il portale.

L'applicazione Android, dopo aver effettuato il login con successo, va a scaricare  e salvare i dati all'interno di un database interno (mediante richieste GET). Le tabelle verranno aggiornate solamente se è stato effettuato qualche cambiamento dal portale o da un altro dispositivo. Dovrà dunque essere in grado di inviare nuovi ordini, visite e clienti effettuando le opportune rcihieste al servizio REST.

## Il servizio REST

Per **REST** (**RE**presentation **S**tate **T**ransfer) si intende un tipo di architettura software per i sistemi distribuiti. Non deve esesre scambiato per un protocollo (quindi come una procedura standardizzata), ma indica in generale un sistema di trasmissione dei dati su protocollo HTTP, e dato che si appoggia su protocollo HTTP, essendo state-less, privo di memoria, non si possono implementare meccanismi come quello della _sessione_; inoltre, la risorse o l'insieme di risorse viene identificata dall'**URI** e dal tipo di richiesta effettuata (il verbo HTTP, come GET, POST, PUT, PATCH, DELETE).

_Tim Bernes-Lee_, co-inventore del World Wide Web, ha definito l'**U**niform **R**esource **I**dentificator come:

> Una sequenza di caratteri compatta che identifica una risorsa fisica oppure astratta

Inoltre:

> L'URI può essere classificato come un _"localizzatore"_, come un nome, oppure entrambe. Il termine **U**niform **R**esource **L**ocator si riferisce ad un sottoinsieme dell'URI il quale, oltre ad identificare la risorsa, fornisce un mezzo per identificare la risorsa descrivendo il suo meccanismo di accesso primario.

Questo significa che un URI **può** essere un URL, solo se include anche il meccanismo di accesso, come ```http://``` oppure ```ftp://```, e l'URL rappresenta effettivamente un URI. Ma non è detto che un URI sia un URL.

* ```http://www.esempio.com/``` è sia un URI che un URL
* ```tel:+39xxxxxxxxxx``` è un URI ma non un URL, perché ```tel``` non rappresenta alcun protocollo

Non essendoci la possibilità di usare le sessioni data la tipologia del servizio, Federico ha dovuto implementare un meccanismo di accesso seguendo una versione semplificata dello standard di autenticazione OAuth 2.0, in modo tale che gli agenti non dovessero rifare l'accesso più volte.

Procedura di autenticazione:

* dal portale è possibile accedere ad una schermata dedicata agli accessi di ogni singolo agente, dove è possibile generare un **access code** (codice di accesso), un codice di 5 cifre casuali
* l'applicazione Android, mediante il codice agente, l'access code generato e la chiave pubblica (che identifica in modo univoco l'applicazione, in questo caso vi sono l'applicazione Android e l'applicazione Android in fase di testing), va ad eseguire una richiesta POST al servizio RESTful.
* se la richiesta risulta corretta e tutti i campi soddisfano i requisiti, il servizio REST andrà a generare una stringa casuale di una lunghezza fissa (il _**token**_), che verrà ritornato al client assieme alla sua data di scadenza.

Il token, secondo OAuth 2.0, dovrebbe essere rigenerato ad ogni richiesta effetuata; tuttavia, per diminuire il numero di richieste al server, è stata scelta una scedenza di **tre** giorni per ogni token generato. Per rinnovarlo, il client deve inviare il token scaduto, l'access code e la chiave pubblica: verrà ritornato il nuovo token con la nuova data di scadenza.

**N.B.** si possono generare **più** access code ma un access code può essere usato **una sola volta**, questo perché un acess code viene associato a solo un token (che può essere eventualmente rinnovato, usando lo stesso codice di accesso).

## Lo scambio dei dati

Come ho descritto precendentemente, parte dei dati aziendali (quelli visualizzabili nel portale e nell'applicazione) sono situati all'interno di una base di dati, gestita dal DBMS _Oracle MySQL_, il quale mette a disposizione un server e un client a riga di comando. Viene supportato da molti linguaggi di programmazione, tra cui Java e PHP.
MySQL è un sistema software di gestione di basi di dati (in particolare, MySQL è un ***R**elational* **D**ata **B**ase **M**anagement **S**ystem) in grado di gestire collezioni di dati che siano:

* **Grandi**, perché possono avere dimensioni enormi, solitamente maggiori rispetto alla memoria centrale disponibile: dunque i DBMS devono predisporre di un meccanismo di gestione dei dati in memoria di massa (mediante il *gestore della memoria secondaria*)
* **Persistenti**, dato che hanno un tempo di vita che non è limitato alle singole istanze delle applicazioni che le utilizzano (infatti, i dati gestiti in memoria centrale hanno una vita che inizia e finisce con l'esecuzione del programma, quindi dati non persistenti)
* **Condivise**, dove applicazioni ed utenti devono poter accedere a dati comuni, anche contemporaneamente, secondo opportune modalità di accesso per evitare la ridondanza dei dati (per ridurre dunque la possibilità di incontrare inconsistenze). Quindi ci sarà bisogno di un meccanismo per la gestione della concorrenza (il **gestore della concorrenza**)
* **Efficienti**, perché devono essere capaci di svolgere le operazioni usando un insieme di risorse che siano accettabili per l'utente (tempo e spazio)
* **Efficaci**, perché devono rendere produttive le attività dei loro utenti.

Inoltre, il DBMS deve garantire:

* **Affidabilità**, ovvero la capacità del sistema di conservare instatto il contenuto della base di dati o di permetterne la ricostruzione in caso di malfunzionamenti hardware o software; inoltre, i DBMS devono gestire le funzioni di ripristino e salvataggio
* **Privatezza dei dati**, dove gli utenti dovranno essere opportunamente riconosciuti per andare ad eseguire azioni o interrogazioni sulla collezione di dati, attraverso meccanismi di autorizzazione; mediante il **D**ata **C**ontrol **L**anguage l'amministratore del DBMS è in grado di aggiungere o rimuovere utenti, revocare o fornire permessi ad utenti già esistenti, in modo tale che essi possano essee in grado di usare i comandi del **D**ata **D**efinition **L**anguage (il quale permette di agire sullo schema della base di dati) e del **D**data **M**anipulation **L**anguage (la _manipolazione_ dei dati, consente dunque di leggere, scrivere, eliminare, inserire o modificare i dati all'interno delle singole tabelle; il linguaggio più usato attualmente è il linguaggio **SQL**).

Il servizio REST è connesso al database, e risponde alle richieste effettuate dai client basandosi sui dati che ha a disposizione (codice di risposta: 2xx). Se la richiesta non esaudisce i requisiti, viene comunque inviata una risposta con il messaggio di errore (errore lato client, codice 4xx).

L'API è stata rivisitata diverse volte dalla versione originale, ma sin dall'inizio entrambi abbiamo convenuto nell'usare come formato **JSON** (**J**ava**S**cript **O**bject **N**otation) per lo scambio dei dati, essendo facile da leggere, scrivere ed analizzare sia per le persone fisiche che per le macchine.
Può essere considerato un JSONObject come un insieme di coppie nome/valore, infatti, in diversi linguaggi, ciò si rappresenta mediante un oggetto, un elenco di chiavi, una struct o un'array associativo; il JSONObject inizia e termina con una parentesi graffa, e le coppie sono separate da una virgola. Il valore può essere una stringa, un double, un intero, un booleano, un JSONObject o adirittura un JSONArray, mentre il nome deve essere una stringa.
Un JSONArray invece inzia e termina con una parentesi quadra, e deve contenere elementi dello stesso tipo: quindi deve contenere solo interi, solo booleani o anche JSONObject aventi in comune la stessa struttura (o addirittura solo JSONArray della stessa struttura); il singolo valore qui viene identificato dalla posizione assunta, quindi è effettivamente un semplice array.
Sono per queste caratteristiche e per questa elasticità offerta le motivazioni per cui abbiamo scelto di adottare questo formato universale. Consente dunque l'**interoperabilità**, essendo una lingua franca, comune, per i programmi scritti in linguaggi di programmazione differenti.

Con la versione odierna del servizio REST quando si va ad eseguire qualsiasi tipo di richiesta la risposta viene scritta nel _body_ della risposta, rispettando il formato JSON, e parte delle richieste contengono dei campi che devono rispettare il formato JSON.

Quando il dispositivo Android va ad eseguire per la prima volta la richiesta delle singole tabelle al server, riceverà per ciascuna tabella una stringa per il controllo dell'integrità dei dati (una _checksum MD5_). Questa checksum verrà usata solo per le richieste successive, se l'agente farà ripartire l'applicazione o se andrà a premere il pulsante _aggiorna_: per non andare a scaricare di nuovo tutte le tabelle, il dispositivo invierà tra i parametri della richiesta anche la checksum prima ricevuta, il servizio andrà a confrontare la sua checksum con quella ricevuta e se risultano identiche risponderà con il codice di stato 304 (Not modified), ciò significa che i dati all'interno al dispositivo sono aggiornati; altrimenti, risponderà con il codice di stato 200 (OK) e nel body saranno presenti tutti i dati con la nuova checksum, il quale andrà sostituita alla vecchia checksum presente nel dispostivo: il telefono dovrà perciò cancellare tutti i dati presenti nella tabella per poi inserire tutti i nuovi dati.

Una **funzione hash** è una qualunque funzione che riesce a trasformare i dati in input in un output di lunghezza costante.
Le **funzioni di hash crittografiche** (H(x)) rappresentano una classe speciale delle funzioni di hash che dispongono di alcune proprietà che le rendono adatte per l'uso della crittografia, tra cui:

* L'**unidirezionalità**, cioè conoscendo l'output (h) deve essere computazionalmente impossibile trovare la stringa di origine
* L'**effetto valanga**, dove deve cambiare completamente l'output se c'è una piccola modifica sull'input (M)
* La **resistenza debole alle collisioni**, dove, conoscendo M, deve essere computazionalmente impossibile trovare m' tale che ```H(M)=H(M')```: questo perché è inevitabile la collisione (quindi due messaggi con lo stesso hash), tuttavia, sapendo ```h=H(M)``` e sapendo che esiste un M' tale che ```H(M)=H(M')```, non posso trovare nessuno dei due M avendo h
* La **resistenza forte alle collisioni**, ovvero deve risultare computazionalmente impossibile trovare una coppia M,M' dove vale l'uguaglianza ```H(M)=H(M')```

L'**MD5** è una funzione crittorgrafica di hash; è una funzione unidirezionale (diversa dalla codifica e dalla cifratura perché irreversibile). Questa funzione prende in pasto una stringa di lunghezza variabile e ne produce un'altra a 128bit (denominata _MD5 checksum_ o _MD5 hash_);

## Gli ambienti di sviluppo

Come ambiente di sviluppo ho scelto **Android Studio**, essendo l'IDE ufficiale per lo sviluppo di applicazioni Android, sviluppato dagli ingegneri Google. Si può dire che questo ambiente di sviluppo è un _fork_ di un altro IDE per Java piuttosto famoso per le sue caratteristiche di eccezione, completamente open-source, denominato IntelliJ IDEA (si parla della versione _Community Edition_, la versione _Ultimate_ è sotto licenza proprietaria), sviluppato dalla software house JetBrains.
Si contraddistingue dagli altri ambienti di sviluppo Java per:

* la sua _keymap_ ricca e completa: infatti, molti sviluppatori riescono ad usare questo programma senza toccare il mouse
* la sua integrazione con i più famosi strumenti di versioning, tra cui Git

Nel corso dell'anno ho rilasciato diverse versioni di quest'applicazione, tuttavia, si possono distinguere ben 3 revisioni generali del codice dall'originale.

Nel codice sorgente della prima versione per la comunicazione con il server e per lo scaricamento dei dati ho usato molte classi e metodologie di approccio ai problemi appartenenti al mondo Java.

Per effettuare il collegamento al server ho impeigato l'utilizzo di:

* un oggetto HttpsURLConnection (classe astratta situata nel package _javax.net.ssl_) che rappresenta la connessione con il server
* un oggetto BufferedStreamReader per leggere il contenuto dell'oggetto InputStream (classe astratta che rappresenta un input stream di bytes, quindi in questo caso contenente la risposta del server alla richiesta sotto forma di bytes) ritornato dall'istanza di HttpsURLConnection, prende in pasto l'inputstream
* il BufferedInputStream legge solitamente _bytes_, i quali mediante un _charset_ possono essere convertiti in caratteri. Quindi ho impiegato l'utilizzo di un _Reader_ (BufferedReader)

Inizialmente, come linguaggio di programmazione ho scelto Java, essendo un linguaggio fortemente tipizzato ed orientato agli oggetti (OOP, ovvero Object Oriented Programming), dove vengono ragruppate in un'unica entità (la classe) sia le strutture dati che le procedure che operano su di esse; l'istanza della classe prende il nome di oggetto, dotato di proprietà (i dati) e metodi che operano sui dati dell'oggetto stesso.
Il meccanismo più potente ed utile di questo linguaggio per raggiungere i miei obiettivi è quello dell'ereditarietà, che mi permette di derivare nuove classi a partire da classi già definite, con la possibilità di aggiungere nuove proprietà e nuovi metodi nella classe derivata, ed eventualmente modificare il comportamento di alcuni metodi definiti nella classe sovrastante laddove è possibile
