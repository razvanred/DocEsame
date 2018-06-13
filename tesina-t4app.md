# T4App

## Introduzione

Nel giugno del 2017 io ed un compagno di scuola della sezione AII dello stesso anno, Federico Bono, abbiamo iniziato a lavorare presso l'azienda T4Group S.R.L. mediante il programma dell'alternanza della scuola-lavoro.
Abbiamo avuto il compito di progettare un'applicazione Android ed un portale (una Web app) per la gestione degli agenti (i venditori) e dei dati aziendali.

Inanzitutto, ci è stato presentato il problema e abbiamo discusso sulla possibile realizzazione prima di cominciare la seconda fase di alternanza scuola/lavoro.

Io mi sono occupato dell'applicativo Android (grazie alle esperienze passate con questa piattaforma), mentre Federico Bono si è occupato del portale (una Web Application) e della RESTful API (ho usato il termine API perché rappresenta un insieme di procedure disponibili al programmatore) da cui dipende l'applicazione Android. Inoltre, Federico ha realizzato un piccolo database contente una parte dei dati aziendali, usato sia del portale che dalla RESTful API. Questo perché l'azienda ha al suo interno un gestionale con una sua base di dati, dove parte di essi possono essere esportati su file Excel: questi file excel possono essere usati per importare i dati sulla base di dati creata da Federico mediante il portale.

L'applicazione Android, dopo aver effettuato il login con successo, va a scaricare  e salvare i dati all'interno di un database interno (mediante richieste GET). Le tabelle verranno aggiornate solamente se è stato effettuato qualche cambiamento dal portale o da un altro dispositivo. Dovrà dunque essere in grado di inviare nuovi ordini, visite e clienti effettuando le opportune rcihieste al servizio REST.

Possiamo dire che io ed il mio compagno ci siamo occupati di costruire _una parte_ del **S**istema **I**nformativo **A**ziendale, il quale è un sistema informatico che fa parte della tecnostruttura dell'azienda. Tra i tradizionali flussi aziendali oggigiorno assume sempre più importanza il flusso dell'informazione, ovvero quel bene intangibile che fa scaturire delle attività gestionali. La gestione delle informazioni è delegata ad un Sistema Informatico, solitamente finalizzato a:

* migliorare i processi produttivi di un'azienda (come un sistema ERP, con cui il nostro lavoro non va direttamente ad interferire)
* costruire e gestire un patrimonio informativo mediante strumenti come un Web Information System
* Innovare sia i prodotti che i processi produttivi

Sono considerati parte del SIA anche tutti quei prodotti software di produttività individuale (in questo caso, l'applicazione Android per gli agenti, oppure strumenti da ufficio come la suite di Microsoft Office).

Dato che durante il corso dell'anno sono state rilasciate diverse versioni dell'applicazione, verso marzo 2017 ho costruito una piccola RESTful API (scritta in Node.JS): dato che l'applicazione non si trova in alcun _app marketplace_ (come quello ufficiale, il Google Play Store), essendo un'applicazione destinata ad uso interno all'azienda, realizzando questo servizio ho potuto tenere traccia delle versioni rilasciate e delle novità e modifiche apportate su ciascuna; in questo modo gli agenti possono verificare se dispongono dell'ultima versione sul proprio dispositivo. Ritorna solamente risposte in formato JSON, e l'interazione con il servizio è possibile solamente mediante richieste POST/PUT/GET/DELETE: il servizio, non appena elabora la richiesta, risponde con un body formattato in JSON, che verrà interpretato dal'applicazione, la quale visualizzerà i dati in caso di successo.
Questo servizio è di natura temporanea (infatti è stato caricato su un sito di hosting gratuito) e verrà integrato successivamente nell'API REST di Federico, implementando nel portale una sezione dedicata allo sviluppatore.

## Il servizio REST aziendale

Per **REST** (**RE**presentation **S**tate **T**ransfer) si intende un tipo di architettura software per i sistemi distribuiti. Non deve esesre scambiato per un protocollo (quindi come una procedura standardizzata), ma indica in generale un sistema di trasmissione dei dati su protocollo HTTP, e dato che si appoggia su protocollo HTTP, essendo state-less, privo di memoria, non si possono implementare meccanismi come quello della _sessione_; inoltre, la risorse o l'insieme di risorse viene identificata dall'**URI** e dal tipo di richiesta effettuata (il verbo HTTP, come GET, POST, PUT, PATCH, DELETE).

_Tim Bernes-Lee_, co-inventore del World Wide Web, ha definito l'**U**niform **R**esource **I**dentificator come:

> Una sequenza di caratteri compatta che identifica una risorsa fisica oppure astratta

Inoltre:

> L'URI può essere classificato come un _"localizzatore"_, come un nome, oppure entrambe. Il termine **U**niform **R**esource **L**ocator si riferisce ad un sottoinsieme dell'URI il quale, oltre ad identificare la risorsa, fornisce un mezzo per identificare la risorsa descrivendo il suo meccanismo di accesso primario.

Questo significa che un URI **può** essere un URL, solo se include anche il meccanismo di accesso, come ```http://``` oppure ```ftp://```, e l'URL rappresenta effettivamente un URI. Ma non è detto che un URI sia un URL.

* ```http://www.esempio.com/``` è sia un URI che un URL
* ```tel:+39xxxxxxxxxx``` è un URI ma non un URL, perché ```tel``` non rappresenta alcun protocollo

### L'auteticazione

Non essendoci la possibilità di usare le sessioni data la tipologia del servizio, Federico ha dovuto implementare un meccanismo di accesso seguendo una versione semplificata dello standard di autenticazione OAuth 2.0, in modo tale che gli agenti non dovessero rifare l'accesso più volte.

Procedura di autenticazione:

* dal portale è possibile accedere ad una schermata dedicata agli accessi di ogni singolo agente, dove è possibile generare un **access code** (codice di accesso), un codice di 5 cifre casuali
* l'applicazione Android, mediante il codice agente, l'access code generato e la chiave pubblica (che identifica in modo univoco l'applicazione, in questo caso vi sono l'applicazione Android e l'applicazione Android in fase di testing), va ad eseguire una richiesta POST al servizio RESTful.
* se la richiesta risulta corretta e tutti i campi soddisfano i requisiti, il servizio REST andrà a generare una stringa casuale di una lunghezza fissa (il _**token**_), che verrà ritornato al client assieme alla sua data di scadenza.

Il token, secondo OAuth 2.0, dovrebbe essere rigenerato ad ogni richiesta effetuata; tuttavia, per diminuire il numero di richieste al server, è stata scelta una scedenza di **tre** giorni per ogni token generato. Per rinnovarlo, il client deve inviare il token scaduto, l'access code e la chiave pubblica: verrà ritornato il nuovo token con la nuova data di scadenza.

**N.B.** si possono generare **più** access code ma un access code può essere usato **una sola volta**, questo perché un acess code viene associato a solo un token (che può essere eventualmente rinnovato, usando lo stesso codice di accesso).

Il token, combinato con il codice agente (ed in certi casi con la chiave pubblica)

### I dati

Come ho descritto precendentemente, parte dei dati aziendali (visualizzabili sul portale e sull'applicazione) sono situati all'interno di una base di dati, gestita dal DBMS _Oracle MySQL_, il quale mette a disposizione un server e un client a riga di comando; viene supportato da molti linguaggi di programmazione, tra cui Java e PHP.
MySQL è un sistema software di gestione di basi di dati (in particolare, MySQL è un ***R**elational* **D**ata **B**ase **M**anagement **S**ystem) in grado di gestire collezioni di dati che siano:

* **Grandi**, perché possono avere dimensioni enormi, solitamente maggiori rispetto alla memoria centrale disponibile: dunque i DBMS devono predisporre di un meccanismo di gestione dei dati in memoria di massa (mediante il *gestore della memoria secondaria*)
* **Persistenti**, dato che hanno un tempo di vita che non è limitato alle singole istanze delle applicazioni che le utilizzano (infatti, i dati gestiti in memoria centrale hanno una vita che inizia e finisce con l'esecuzione del programma, quindi dati non persistenti)
* **Condivise**, dove applicazioni ed utenti devono poter accedere a dati comuni, anche contemporaneamente, secondo opportune modalità di accesso per evitare la ridondanza dei dati (per ridurre dunque la possibilità di incontrare inconsistenze). Quindi ci sarà bisogno di un meccanismo per la gestione della concorrenza (il **gestore della concorrenza**)
* **Efficienti**, perché devono essere capaci di svolgere le operazioni usando un insieme di risorse che siano accettabili per l'utente (tempo e spazio)
* **Efficaci**, perché devono rendere produttive le attività dei loro utenti.

Inoltre, il DBMS deve garantire:

* **Affidabilità**, ovvero la capacità del sistema di conservare instatto il contenuto della base di dati o di permetterne la ricostruzione in caso di malfunzionamenti hardware o software; inoltre, i DBMS devono gestire le funzioni di ripristino e salvataggio
* **Privatezza dei dati**, dove gli utenti dovranno essere opportunamente riconosciuti per andare ad eseguire azioni o interrogazioni sulla collezione di dati, attraverso meccanismi di autorizzazione; mediante il **D**ata **C**ontrol **L**anguage l'amministratore del DBMS è in grado di aggiungere o rimuovere utenti, revocare o fornire permessi ad utenti già esistenti, in modo tale che essi possano essee in grado di usare i comandi del **D**ata **D**efinition **L**anguage (il quale permette di agire sullo schema della base di dati) e del **D**data **M**anipulation **L**anguage (la _manipolazione_ dei dati, consente dunque di leggere, scrivere, eliminare, inserire o modificare i dati all'interno delle singole tabelle; il linguaggio più usato attualmente è il linguaggio **SQL**).

Il modello dei dati che segue MySQL è il modello relazionale, attualmente il più diffuso, il quale permette di definire tipi per mezzo del costruttore _relazione_, che consente di organizzare i dati in insiemi di _record_ a struttura fissa. La relazione viene rappresentata per mezzo di una _tabella_, le cui righe rappresentano i specifici record e le cui colonne rappresentano i campi dei record; l'ordine delle righe e delle colonne è sostanzialmente irrilevante. Esistono tanti altri modelli, oltre a quello relazionale, tra cui il modello geraarchico, il modello XML, il modello a oggetti, modelli flessibili e semistrutturati (i NoSQL, cercano di superare le limitazioni dei modelli relazionali) e così via.

Il servizio REST è connesso al database, e risponde alle richieste effettuate dai client basandosi sui dati che ha a disposizione (codice di risposta: 2xx). Se la richiesta non esaudisce i requisiti, viene comunque inviata una risposta con il messaggio di errore (errore lato client, codice 4xx).

L'API è stata rivisitata diverse volte dalla versione originale, ma sin dall'inizio entrambi abbiamo convenuto nell'usare come formato **JSON** (**J**ava**S**cript **O**bject **N**otation) per lo scambio dei dati, essendo facile da leggere, scrivere ed analizzare sia per le persone fisiche che per le macchine.
Può essere considerato un JSONObject come un insieme di coppie nome/valore, infatti, in diversi linguaggi, ciò si rappresenta mediante un oggetto, un elenco di chiavi, una struct o un'array associativo; il JSONObject inizia e termina con una parentesi graffa, e le coppie sono separate da una virgola. Il valore può essere una stringa, un double, un intero, un booleano, un JSONObject o adirittura un JSONArray, mentre il nome deve essere una stringa.
Un JSONArray invece inzia e termina con una parentesi quadra, e deve contenere elementi dello stesso tipo: quindi deve contenere solo interi, solo booleani o anche JSONObject aventi in comune la stessa struttura (o addirittura solo JSONArray della stessa struttura); il singolo valore qui viene identificato dalla posizione assunta, quindi è effettivamente un semplice array.
Sono per queste caratteristiche e per questa elasticità offerta le motivazioni per cui abbiamo scelto di adottare questo formato universale. Consente dunque l'**interoperabilità**, essendo una lingua franca, comune, per i programmi scritti in linguaggi di programmazione differenti.

Con la versione odierna del servizio REST quando si va ad eseguire qualsiasi tipo di richiesta la risposta viene scritta nel _body_ della risposta, rispettando il formato JSON, e parte delle richieste contengono dei campi che devono rispettare il formato JSON.

### Il ruolo della checksum MD5

Quando il client va ad eseguire per la prima volta la richiesta delle singole tabelle al server, riceverà per ciascuna tabella una stringa per il controllo dell'integrità dei dati (una _checksum MD5_). Questa checksum verrà usata a partire dalle richieste successive, quindi se l'agente riavvierà l'applicazione o se andrà a premere il pulsante _aggiorna_: per non andare a scaricare di nuovo tutte le tabelle, il dispositivo, oltre a specificare i parametri nell'URI, andrà a specificare l'header ```If-None-Match```, il quale conterrà la checksum md5; il servizio andrà a confrontare la checksum dei dati che ha a disposizione con quella ricevuta dalla richiesta e se risultano identiche risponderà con il codice di stato 304 (Not modified), ciò significa che i dati all'interno al dispositivo sono aggiornati, quindi non c'è il bisogno di scaricare nulla; altrimenti, risponderà con il codice di stato 200 (OK) e nel body saranno presenti le risorse richieste con la nuova checksum, la quale andrà sostituita alla vecchia checksum presente nel dispostivo: il telefono dovrà perciò cancellare tutti i dati presenti nella tabella per poi sostituirli con i nuovi dati.

Una **funzione hash** è una qualunque funzione che riesce a trasformare i dati in input in un output di lunghezza costante.
Le **funzioni di hash crittografiche** (H(x)) rappresentano una classe speciale delle funzioni di hash che dispongono di alcune proprietà che le rendono adatte per l'uso della crittografia, tra cui:

* L'**unidirezionalità**, cioè conoscendo l'output (h) deve essere computazionalmente impossibile trovare la stringa di origine
* L'**effetto valanga**, dove deve cambiare completamente l'output se c'è una piccola modifica sull'input (M)
* La **resistenza debole alle collisioni**, dove, conoscendo M, deve essere computazionalmente impossibile trovare M' tale che ```H(M)=H(M')```: questo perché è inevitabile la collisione (quindi due messaggi con lo stesso hash), tuttavia, sapendo ```h=H(M)``` e sapendo che esiste un M' tale che ```H(M)=H(M')```, non possa trovare nessuno dei due M avendo h
* La **resistenza forte alle collisioni**, ovvero deve risultare computazionalmente impossibile trovare una coppia M,M' dove vale l'uguaglianza ```H(M)=H(M')```

L'**MD5** è una funzione crittografica di hash; è una funzione unidirezionale (diversa dalla codifica e dalla cifratura perché irreversibile). Questa funzione prende in pasto una stringa di lunghezza variabile e ne produce un'altra a 128bit (32 caratteri, un numero esadecimale),denominata _MD5 checksum_ o _MD5 hash_.
Si può calcolare la checksum di un file (la sua impronta digitale, o _message-digest_, e se due file presentano la stessa checksum c'è un'alta probabilità che siano identici.

Nel nostro progetto, la checksum viene usata solo per verificare se i dati sono stati modificati. Il controllo d'integrità dal lato client non è necessario, in quanto il body è formattato nel formato JSON: se una parte del body non è stata elaborata correttamente l'interpretatore JSON(esisitono librerie specializzate per Java come ```org.json``` oppure ```com.google.code.gson```) nella maggior parte dei casi lancia un errore e viene ri-eseguita la richiesta (non capita spesso, salvo zone con minore copertura cellulare, dove la connessione risulta essere più lenta rispetto al solito).

## L'applicazione Android

## Panoramica su Android

Android è il sistema operativo più diffuso per i dispositivi mobili, sviluppato da Google, basato su kernel Linux; è un sistema **embedded** (integrato) ed è in grado di adattarsi su smartphone, tablet, televisori (Android TV), orologi (Android Wear) ed automobili (Android Auto).

All'inizio era sviluppato da Android Inc., una start-up fondata da Andy Rubin nel 2003, con l'obiettivo di creare un sistema operativo avanzato per fotocamere; dopodiché, nel luglio 2005, l'azienda venne acquisita da Google.

Quando venne annuciato il primo iPhone nel 2007, i rumors riguardo alla produzione di un dispositio simle marchiato Google aumentarono.

### Gli strumenti

Avendo avuto esperienze passate, ho deciso di sviluppare un'applicazione Android **nativa**, scegliendo inizialmente come linguaggio di programmazione _Java_, quindi andando ad usare il **J**ava **D**evelopment **K**it affiancato all'**Android SDK** (un altro **S**oftware **D**evelopment **K**it, un insieme di strumenti per lo sviluppo e la documentazione software).

Il kit di sviluppo Android contiene tutte le librerie e i programmi di sviluppo necessari per la compilazione, il test e per il debug delle applicazioni.
Affiancato all'SDK può essere utilizzato qualsiasi tipo di ambiente di sviluppo (volendo, anche un semplice editor di testo); tuttavia, la miglior integrazione la si ottiene con l'ambiente di sviluppo ufficale, **Android Studio**.

Oltre ad Android Studio, per andare a simulare la risoluzione di piccole e possibili problematiche con Java ho usato IntelliJ IDEA, un IDE diventato famoso negli ultimi anni per le sue caratteristiche d'eccezione, sviluppato dalla software-house JetBrains: infatti, Android Studio non è altro che un _fork_ di IntelliJ IDEA (si parla dell'edizione _Community_, la versione _Ultimate_ è closed-source sotto licenza proprietaria), pesantemente modificato, per renderlo adatto allo sviluppo di applicazioni Android.

Rispetto ad altri ambienti, ho trovato utile IntelliJ IDEA per:

* la sua _keymap_ ricca, comoda e completa: infatti, molti sviluppatori riescono ad utilizzare questo programma senza toccare il mouse
* la sua integrazione con i più famosi strumenti di versioning, tra cui Git; ad esempio, la possibilità di cambiare in pochi click il branch su cui si lavora (ed eventualmente crearne di nuovi), uno strumento di _comparison_ per vedere le modifiche effettuate da un commit all'altro, e tante altre funzionalità utili
* la sua interfaccia chiara, pulita ed adattabile in base alla situazione
* uno degli IDE con supporto immediato alle ultime versioni del JDK
* il suo completamento _"smart"_ (richiamabile mediante ```Cmd + Shift + Spazio```), che suggerisce le classi/i metodi/le variabili più utilizzati nel progetto, basandosi sempre sul **contesto** (per certi versi, somiglia al completamento automatico di Microsoft, impiegato in Visual Studio e Visual Studio Code, IntelliSense)
* il suo rilevamento intelligente di codice duplicato o di possibile semplificazione di un'espressione, con tanto di sostituizione automatica
* il salvataggio automatico, non è più necessario salvare manualmente ogni volta i file (per tornare ad una situazione precedente è sempre possibile mediante la sua sezione dedicata _Local History_)
* la possibilità di usare altri linguaggi di programmazione oltre a Java, ed arriva con un supporto integrato a molti framework moderni (un esempio è il supporto a Node.JS integrato, quindi c'è anche la possibilità di scrivere anche in JavaScript)
* il suo supporto inequiparabile a **Kotlin**, il linguaggio di programmazione sviluppato dalla stessa software-house, di cui ne parlerò successivamente

Essendo suo _fork_, Android Studio riprende tutte queste caratteristiche d'eccezione, e ci affianca dei tool particolarmente utili per lo sviluppo di applicazioni native, tra cui:

* l'Android Device File Explorer, un file explorer particolare che permette di eplorare cartelle normalmente non visibili (se non si dispongono dei permissi di root)
* l'Android Logcat, ovvero la console da cui è possibile vedere tutti i System.out/err effettuati dalle singole applicazioni
* il gestore delle macchine virtuali, l'**A**ndroid **V**irtual **D**evice Manager, da dove è possibile creare una macchina virtuale personalizzata, da dove è possibile scegliere:
  * la versione del sistema operativo desisderata
  * l'architettura desiderata
    * è possibile emulare un'architettura x86 solamente se si dispongono di questi requisiti:
      * sistema operativo e processore a 64bit
      * processore Intel, nel BIOS deve essere abilitata la funzione _Intel Virtualization Technology_
      * almeno 4GB di ram
      * è richiesto su macchine con Winows o macOS avere installato il software Intel HAXM, un motore di virtualizzazione assistito dall'hardware che sfrutta la tecnologia Intel VT per migliorare le performance della macchina virtuale (con a bordo un sistema operativo a 32 o 64bit); prima questo software era stato progettato per far parte dell'Android SDK, ma poi si è trasformato in un acceleratore generale per QEMU (il virtualizzatore ed il _machine emulator_ su cui si basa l'AVD)

<!--Prima di Android Studio (venne annunciato alla conferenza Google I/O del 2013) , gli sviluppatori usavano Eclipse per sviluppare applicazioni Android-->

Nel corso dell'anno ho rilasciato diverse versioni di quest'applicazione, tuttavia, si possono distinguere ben 3 revisioni generali del codice dall'originale.

Nel codice sorgente della prima versione per la comunicazione con il server e per lo scaricamento dei dati ho usato molte classi e metodologie di approccio ai problemi appartenenti al mondo Java.

Per effettuare il collegamento al server ho impeigato l'utilizzo di:

* un oggetto HttpsURLConnection (classe astratta situata nel package _javax.net.ssl_) che rappresenta la connessione con il server
* un oggetto BufferedStreamReader per leggere il contenuto dell'oggetto InputStream (classe astratta che rappresenta un input stream di bytes, quindi in questo caso contenente la risposta del server alla richiesta sotto forma di bytes) ritornato dall'istanza di HttpsURLConnection, prende in pasto l'inputstream
* il BufferedInputStream legge solitamente _bytes_, i quali mediante un _charset_ possono essere convertiti in caratteri. Quindi ho impiegato l'utilizzo di un _Reader_ (BufferedReader)

Inizialmente, come linguaggio di programmazione ho scelto Java, essendo un linguaggio fortemente tipizzato ed orientato agli oggetti (OOP, ovvero Object Oriented Programming), dove vengono ragruppate in un'unica entità (la classe) sia le strutture dati che le procedure che operano su di esse; l'istanza della classe prende il nome di oggetto, dotato di proprietà (i dati) e metodi che operano sui dati dell'oggetto stesso.
Il meccanismo più potente ed utile di questo linguaggio per raggiungere i miei obiettivi è quello dell'ereditarietà, che mi permette di derivare nuove classi a partire da classi già definite, con la possibilità di aggiungere nuove proprietà e nuovi metodi nella classe derivata, ed eventualmente modificare il comportamento di alcuni metodi definiti nella classe sovrastante laddove è possibile
