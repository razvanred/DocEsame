# T4App

## Introduzione

Nel giugno del 2017 io ed uno studente della sezione AII dello stesso anno, Federico Bono, abbiamo iniziato a lavorare presso l'azienda T4Group S.R.L. mediante il programma dell'alternanza della scuola-lavoro.
Abbiamo avuto il compito di progettare un'applicazione Android ed un portale (una Web app) per la gestione degli agenti (i venditori) e dei dati aziendali.

Inanzitutto, ci è stato presentato il problema e abbiamo discusso sulla possibile realizzazione prima di cominciare la seconda fase di alternanza scuola/lavoro.

Io mi sono occupato dell'applicativo Android (grazie alle esperienze passate con questa piattaforma), mentre Federico Bono si è occupato del portale (una Web Application) e della RESTful API (ho usato il termine API perché rappresenta un insieme di procedure disponibili al programmatore) da cui dipende l'applicazione Android. Inoltre, Federico ha realizzato un piccolo database contente una parte dei dati aziendali, usato sia del portale che dalla RESTful API. Questo perché l'azienda ha al suo interno un gestionale con una sua base di dati, dove parte di essi possono essere esportati su file Excel: questi file excel possono essere usati per importare i dati sulla base di dati creata da Federico mediante il portale.

L'applicazione Android, dopo aver effettuato il login con successo, va a scaricare  e salvare i dati all'interno di un database interno (mediante richieste GET). Le tabelle verranno aggiornate solamente se è stato effettuato qualche cambiamento dal portale o da un altro dispositivo. Dovrà dunque essere in grado di inviare nuovi ordini, visite e clienti effettuando le opportune richieste al servizio REST.

Possiamo dire che io ed il mio compagno ci siamo occupati di costruire _una parte_ del **S**istema **I**nformativo **A**ziendale, il sistema informatico che fa parte della tecnostruttura dell'azienda. Tra i tradizionali flussi aziendali oggigiorno assume sempre più importanza il flusso dell'informazione, ovvero quel bene intangibile che fa scaturire delle attività gestionali. La gestione delle informazioni è delegata ad un Sistema Informatico, solitamente finalizzato a:

* migliorare i processi produttivi di un'azienda (come un sistema ERP, con cui il nostro lavoro non va direttamente ad interferire)
* costruire e gestire un patrimonio informativo mediante strumenti come un Web Information System
* innovare sia i prodotti che i processi produttivi

Sono considerati parte del SIA anche tutti quei prodotti software di produttività individuale (in questo caso, l'applicazione Android per gli agenti, oppure strumenti da ufficio come la suite di Microsoft Office).

Durante il corso dell'anno sono state rilasciate diverse versioni dell'applicazione, e verso il mese di marzo del 2018 ho costruito una piccola RESTful API (scritta in Node.JS): dato che l'applicazione non si trova in alcun _app marketplace_ (come quello ufficiale, il Google Play Store), essendo un'applicazione destinata ad uso interno all'azienda, realizzando questo servizio ho potuto tenere traccia delle versioni rilasciate e delle novità e modifiche apportate su ciascuna; in questo modo gli agenti possono verificare se dispongono dell'ultima versione sul proprio dispositivo. Ritorna solamente risposte in formato JSON, e l'interazione con il servizio è possibile solamente mediante richieste POST/PUT/GET/DELETE: il servizio, non appena elabora la richiesta, risponde con un body formattato in JSON, che verrà interpretato dal'applicazione, la quale visualizzerà i dati in caso di successo.
Questo servizio è di natura temporanea (difatti, è stato caricato su un sito di hosting gratuito) e verrà integrato successivamente nell'API RESTful di Federico, implementando nel portale una sezione dedicata allo sviluppatore.

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

## Il sistema operativo Android

### Com'è nato

Android è il sistema operativo più diffuso per i dispositivi mobili, sviluppato da Google, basato su kernel Linux; è un sistema **embedded** (integrato); inoltre, sono state prodotte diverse versioni per alcune categorie di prodotti, come i televisori (Android TV), i  dispositivi _wearable_ come gli smartwatch(Android Wear) e le auto (Android Auto), ciascuna con la propria interfaccia utente.

All'inizio Android era un progetto sviluppato da Android Inc., una start-up fondata a Palo Alto da Andy Rubin nel 2003, con l'obiettivo di creare un sistema operativo avanzato per fotocamere; decisero successivamente, nel 2004, di renderlo un sistema operativo per dispositivi mobili per entrare in competizione con Microsoft Windows Mobile e Symbian OS. Nel luglio del 2005, l'azienda venne acquisita da Google per 50 milioni di dollari.

Quando venne annuciato il primo iPhone nel 2007, i rumors riguardo alla produzione di un dispositio simile marchiato Google aumentarono.

Il 5 novembre 2007 si svelò l'Open Handset Alliance (dopo alcuni incontri _"clandestini"_), un consorzio di aziende tecnologiche tra cui Google, produttori di dispositivi come HTC, Samsung, ASUS e Motorola, operatori telefonici tra cui le americane  Sprint, T-Mobile e l'italiana Telecom Italia, e produttori di chipset come Qualcomm, e presentò:

> La prima piattaforma veramente **aperta** e **completa** per i dispositivi mobili, **_Android_**.

Il primo dispositivo consumer ad essere commercializzato con il neonato sistema operativo fu l'_HTC Dream_, noto anche con il nome di _T-Mobile G1_, a partire dal 2008.

Dal 2008 in poi Google ha continuato a sviluppare il suo sistema operativo, e ciascuna _release_ di Android è caratterizzata da:

* un nome in codice, che spesso raggruppa più versioni in caso di revisioni minori (ispirato a nomi di dolci, in ordine alfabetico: Alpha, Beta, Cupcake, Donut, Eclair, Froyo, ..., attualmente Oreo)
* un numero di versione numerico (siamo arrivati alla versione 8.1.0)
* un API level, un identificativo numerico che specifica la versione delle API utilizzabili dalle app (oggi, API 27)

Rispetto ad altri sistemi operativi, Android permetteva lo sviluppo di applicazioni Android in maniera più _modulare_ rispetto alla concorrenza: questa sua caratteristica rappresentò uno dei maggiori motivi per cui è riuscito a riscuotere grande successo, specie tra le community di sviluppatori dell'intero globo.

### L'architettura a layer

L'architettura di Android è composta dai seguenti layer, partendo dal livello più basso:

1. Il **kernel Linux**, fornisce i servizi essenziali di gestione della memoria centrale, della sicurezza e dei driver delle singole componenti del dispositivo (i produttori hardware riescono a sviluppare i driver per un kernel ben noto). Ad esempio, per le funzionalità di base come il threading o la gestione della memoria centrale l'ART (Android Runtime) si basa sul kernel Linux.
2. L'**Hardware Absraction Layout**, fornisce interfacce standard, le quali espongono i servizi dell'hardware sottostante al framework Java al livello sovrastante; consiste dunque in un insieme di moduli di librerie, ciascuno dei quali va ad implementare un'interfaccia specifica per una specifica tipologia di componente hardware, come la fotocamera o il sensore della geolocalizzazione. Quando il framework Java va ad eseguire una chiamata per accedere all'hardware del dispositivo, il sistema carica il modulo della libreria per il componente hardware richiesto.
3. L'**Android RunTime**, dove ciascuna applicazione gira nel suo processo e con la sua istanza di ART Virtual Machine (nel caso di dispositivi con versione 5.0 o superiori) o di Dalvik Virtual Machine (versioni 4.4.4 o inferiori). Entrambe le tipologie macchine virtuali erano state progettate per avere un minor impatto sulla memoria centrale del dispositivo, eseguendo file DEX (simili ai file class prodotti dal compilatore Java), un formato particolare di bytecode progettato specificamente per Android, tuttavia:
    * la virtual machine Dalvik permetteva la compilazione _**J**ust-**I**n-**T**ime_ del bytecode, dove la compilazione avveniva solamente se richiesta; perciò, ogni volta che l'utente richiedeva l'avvio di un'applicazione, il sistema si occupava di convertire i file DEX associati in istruzioni native, e solo dopo la conclusione di questo processo l'applicazione partiva. Questo processo ripetitivo si traduceva in uno spreco della durata della batteria, impiegando più volte cicli preziosi del processore.
    * la virtual machine ART ora permette la compilazione _**A**head-**O**f-**T**ime_ del bytecode: qui i file DEX vengono compilati prima di essere richiesti, durante l'installazione del file APK (Android Package, usato per la distribuzione e l'installazione di applicazioni Android). Ciò significa che i tempi di installazione risultano più lunghi (impercettibile la differenza), tuttavia, si riesce ad ottenere un netto risparmio della batteria e performance generali migliorate. Inoltre, la nuova _virtual machine_ ha portato ottimizzazioni del garbage collector ed un miglior supporto al debug.
4. Le **librerie in C/C++**. Alcune componenti del sistema come ART o HAL sono state originariamente scritte in codice nativo, e richiedono librerie scritte in C e C++. Alcune di queste librerie possono essere usate in Java grazie a framework integrati e specializzati. Se si sviluppa un'applicazione che richiede parti di codice in C o in C++, è possibile usare l'Android **N**ative **D**evelopment **K**it per accedere a parte di queste librerie native direttamente dal codice nativo.
5. Il framework **Java API Framework**, un set di funzionalità del sistema operativo disponibili mediante API scritte in Java (parte di queste ora sono scritte in Kotlin). Questo permette di poter scrivere applicazioni in una manierà più modulare possibile, ed includono:
    * il sistema grafico (_View System_, dove tutte le componenti grafiche sono derivate della classe **View**)
    * il Resource Manager (la classe statica R che permette di accedere a risorse come stringhe, layout, drawable, ecc.)
    * l'Activity Manager (comprendente il *lifecycle* dell'applicazione)
    * un gestore delle notifiche
    * un Content Provider, il quel permette ad un'applicazione di accedere al contenuto di altre applicazioni (per esempio, l'applicazione WhatsApp può accedere al contenuto della rubrica) o di condividere i propri dati
6. Le **applicazioni di sistema**; questo perché Android comprende una serie di applicazioni preinstallate come il calendario, un browser, l'applicazione di messaggistica SMS e tante altre. Queste app possono essere usate sia dall'utente che da altre applicazioni di terze parti, includendole come componenti: ad esempio, per inviare un SMS da un'app di terze parti è possibile sfruttare un'applicazione specializzata di cui si occuperà dell'invio.

### Il progetto Android

Un progetto Android, oltre ai file di configurazione di Gradle, è composto da tre sezioni fondamentali:

1. Le **risorse**; contenute nella cartella ```res```, sono un insieme di particolari file di configurazione o immagini accessibili da codice Java (```R.tipo_risorsa.nome_risorsa```) o da XML (```@tipo_risorsa/nome_risorsa```) e dotate della possibilità di essere selezionate in base al particolare dispositivo che esegue l'applicazione, mediante i _qualificatori_. Ad esempio, possono esistere una moltituide di file ```strings.xml``` (collocati in cartelle differenti per distinguersi), che rappresentano le risorse di tipo ```string```, ma tra tutti questi verrà usato il più opportuno in base alla lingua del dispositivo selezionata: verrà usato il file ```values-it/strings.xml``` invece del file ```values/strings.xml``` se la lingua del dispositivo selezionata è l'italiano. Per poter accedere alle risorse, Gradle va a generare automaticamente una classe statica Java denominata R, contente una serie di interi che vanno ad identificare ciascuno la risorsa in modo univoco.
2. Il **codice sorgente Java**; contenuto all'interno della cartella ```java```, contiene tutte le classi necessarie per far funzionare le applicazioni (le Activity, i Fragment ecc.) scritte dallo sviluppatore o generate automaticamente dal compilatore (un esempio è la classe R)
3. Il file **AndroidManifest.xml**; contenuto all'interno della cartella ```manifests``` (si possono creare più versioni di questo file, ciascuna associata ad una _build variant_), le sue funzioni principali sono:
    * specificare il **package** dell'applicazione
    * descrivere i componenti dell'applicazione
    * dichiarare i permessi che l'applicazione deve avere per poter funzionare (connessione a internet, fotocamera, microfono ecc.)
    * dichiarare il minimo livello di Android API che l'app supporta
    * dichiarare le activity (in particolare, specificare l'activity iniziale)

### Il ruolo dell'Activity

Generalmente, un'applicazione è suddivisa in più schermate, e ciascuna schermata rappresenta (il più delle volte) un'**Activity**, ovvero una classe che estende la classe **Activity** (o **AppCompatActivity**) messa a disposizione dal layer **Java API Framwork**.

Ciascuna activity è caratterizzata da un insieme di metodi che possono essere sovrascritti dalla classe derivata, i quali rappresentano il **Lifecycle** della schermata: sono tutti quei metodi **callback** che vengono richiamati quando la schermata subisce particolari cambiamenti, come la rotazione dello schermo, la chiusura dell'applicazione, la pressione del tasto _indietro_, la ricevuta di una chiamata e così via.

I metodi appartenenti al Lifecycle sono i seguenti:

* quando viene invocata la visualizzazione della schermata, il primo metodo ad essere richiamato è il metodo ```onCreate(savedInstanceState:Bundle)```, dove solitamente le componenti dell'interfaccia grafica vengono istanziate:
  * per andare a caricare il layout associato alla schermata che si desidera visualizzare, dopo aver richiamato il metodo onCreate sovrastante (mediante il costrutto ```super.nomeMetodo(args)```), bisognerà andare a richiamare il metodo ```setContentView(int risorsa)``` appartenente alla classe padre
  * mediante il metodo ```findViewById(int risorsa)``` è possibile andare ad ottenere l'istanza del componente grafico, per assegnarla ad un puntatore dichiarato all'interno della classe activity. Questo metodo ritornerà sempre un oggetto di tipo _View_, tuttavia, mediante il _cast_ è possibile ottenere la sua _forma_ originale (il tipo con cui si va ad eseguire la conversione deve essere in qualche modo derivato della classe View, e deve corrispondere al tipo dichiarato all'interno del file XML)
* dopo aver terminato il metodo ```onCreate``` con successo viene richiamato il metodo ```onStart()```, il quale rende visibile la schermata all'utente, prima di diventare interagibile (metodo molto veloce)
* dopo la terminazione del metodo ```onStart``` viene richiamato il metodo ```onResume()```, e qui l'utente è in grado di interagire con la schermata. Alla fine di questo metodo, l'activity passa nello stato **RUNNING** (in esecuzione). Può essere richiamato anche dopo il metodo ```onPause```
* il metodo ```onPause()``` viene richiamato quando parte dell'activity risulta ancora visibile, ma in primo piano vi può essere ad esempio una notifica o una finestra di dialogo. Se dopo questo metodo la schermata viene completamente nascosta, viene richiamato il metodo ```onStop()```, altrimenti viene di nuovo richiamato il metodo ```onResume()```
* il metodo ```onStop()``` viene richiamato quando la schermata viene nascosta, dopo la terminazione del metodo ```onPause()```. Se l'utente sceglie di tornare alla schermata viene richiamato il metodo ```onRestart()```; se un'altra applicazione in primo piano richiede memoria l'applicazione passerà allo stato **KILLED** (uccisa), e se la schermata in questione verrà di nuovo richiamata si ricomincia dal metodo ```onCreate()```
* il metodo ```onDestroy()```, dopo la terminazione del metodo ```onStop()```, viene richiamato solamente se l'activity ha invocato il metodo ```finish()``` o se viene invocato dal sistema: l'activity è stata **chiusa**

**N.B.** la classe Activity estende una classe astratta di particolare rilievo, denominata _Context_: permette l'accesso alle risorse e a classi specifiche dell'applicazione, il lancio di una nuova Activity mediante il meccanismo degli _Intent_ o la ricezione di un _Intent_ (meccanismo principalmnete usato per far partire una nuova activity, può essere usato anche per trasportare dati o può essere ricevuto come una risposta attesa da parte di un'altra schermata), e molto altro.

### Il processo di build di un file APK (non firmato)

Il processo di compilazione per le applicazioni Android risulta completamente diverso rispetto al processo di compilazione delle applicazioni tradizionali Java. Tutta, entrambi i processi hanno una fase iniziale in comune: il codice sorgente Java viene compilato in _bytecode_ (file .class) mediante il comando ```javac```.

I file .class e tutte le librerie JAR vengono poi convertite in bytecode **D**alvik **EX**ecutable (i file DEX sopra citati), mediante il comando ```dx```: quindi tutti i file .class e .jar vengono tutti uniti in un singolo file, ```classes.dex```, scritto nel formato bytecode Dalvik.

Infine, il file generato e le risorse come i layout o le immagini, vengono compresse in un file simile allo zip, denominato _**A**ndroid **P**ac**K**age_, mediante lo strumento _**A**ndroid **A**ssets **P**ackaging **T**ool_, ```aapt```, presente all'interno dell'**Android SDK**.

Il file APK potrà essere utilizzato per distribuire l'applicazione. Per poterla distribuire sul _Google Play Store_, dovrà essere firmato mediante ulteriori passaggi.

## L'applicazione

### Gli strumenti

Avendo avuto esperienze passate, ho deciso di sviluppare un'applicazione Android **nativa**, scegliendo inizialmente come linguaggio di programmazione _Java_, sfruttando il kit di sviluppo **Android SDK** (un **S**oftware **D**evelopment **K**it, un insieme di strumenti per lo sviluppo e la documentazione software).

Il kit di sviluppo Android contiene tutte le librerie e i programmi di sviluppo necessari per la compilazione, il test e per il debug delle applicazioni.
Affiancato all'SDK può essere utilizzato qualsiasi tipo di ambiente di sviluppo (volendo, anche un semplice editor di testo); tuttavia, la miglior integrazione la si ottiene con l'ambiente di sviluppo ufficale, **Android Studio**.

Oltre ad Android Studio, per andare a simulare la risoluzione di piccole e possibili problematiche con Java, ho usato IntelliJ IDEA, un IDE divenuto famoso negli ultimi anni per le sue caratteristiche d'eccezione, sviluppato dalla software-house JetBrains: infatti, Android Studio non è altro che un _fork_ di IntelliJ IDEA (si parla dell'edizione _Community_, la versione _Ultimate_ è closed-source sotto licenza proprietaria), pesantemente modificato, per renderlo adatto allo sviluppo di applicazioni Android.

Rispetto ad altri ambienti, ho trovato utile IntelliJ IDEA per:

* la sua _keymap_ ricca, comoda e completa: infatti, molti sviluppatori riescono ad utilizzare questo programma senza toccare il mouse (ottimizzazione degli _import_, formattazione automatica ecc.)
* la sua integrazione con i più famosi strumenti di versioning, tra cui Git; ad esempio, la possibilità di cambiare in pochi click il branch su cui si lavora (ed eventualmente crearne di nuovi), uno strumento di _comparison_ per vedere le modifiche effettuate da un commit all'altro, e tante altre funzionalità utili
* la sua interfaccia chiara, pulita ed adattabile in base alla situazione
* uno degli IDE con supporto immediato alle ultime versioni del JDK
* il suo completamento _"smart"_ (richiamabile mediante ```Cmd + Shift + Spazio```), che suggerisce le classi/i metodi/le variabili più utilizzati nel progetto, basandosi sempre sul **contesto** (per certi versi, somiglia al completamento automatico di Microsoft, impiegato in Visual Studio e Visual Studio Code, _IntelliSense_)
* il suo rilevamento intelligente di codice duplicato o di possibile semplificazione di un'espressione, con tanto di sostituizione automatica
* il salvataggio automatico, non è più necessario salvare manualmente ogni volta i file (per tornare ad una situazione precedente è sempre possibile mediante la sua sezione dedicata _Local History_)
* la possibilità di usare altri linguaggi di programmazione oltre a Java, ed arriva con un supporto integrato a molti framework moderni (un esempio è il supporto a Node.JS integrato, quindi c'è anche la possibilità di scrivere anche in JavaScript)
* il suo supporto inequiparabile a **Kotlin**, il linguaggio di programmazione sviluppato dalla stessa software-house, di cui ne parlerò successivamente
* _highlight_ (correzione, testo evidenziato) della sintassi SQL in caso di rilevamento query

Essendo suo _fork_, Android Studio riprende tutte queste caratteristiche d'eccezione, e ci affianca dei tool particolarmente utili per lo sviluppo di applicazioni native, tra cui:

* l'Android Device File Explorer, un file explorer particolare che permette di eplorare cartelle normalmente non visibili (se non si dispongono dei permissi di root) mediante il comando ```adb```
* l'Android Logcat, ovvero la console da cui è possibile vedere tutti i System.out/err effettuati dalle singole applicazioni (è preferibile usare i metodi della classe **Log** del package _android.util_)
* il gestore delle macchine virtuali, l'**A**ndroid **V**irtual **D**evice Manager, da dove è possibile creare ed avviare una macchina virtuale personalizzata, c'è la possibilità di scegliere per ciascuna macchina:
  * la versione del sistema operativo desisderata
  * la risoluzione e le dimensioni dello schermo
  * l'architettura desiderata:
    * arm a 32 bit (difficile da emulare su un'architettura di un PC tradizionale, la macchina virtuale risluta particolarmente lenta)
    * è possibile emulare un'architettura x86 solamente se si dispongono di questi requisiti:
      * sistema operativo e processore a 64bit
      * processore Intel, mediante il BIOS deve essere abilitata la funzione _Intel Virtualization Technology_
      * almeno 4GB di ram
      * su macchine con Winows o macOS è richiesta l'installazione del software Intel HAXM, un motore di virtualizzazione assistito dall'hardware che sfrutta la tecnologia _Intel VT_ per migliorare le performance della macchina virtuale (con a bordo un sistema operativo a 32 o 64bit); prima questo software era stato progettato per far parte dell'Android SDK, ma poi si è trasformato in un acceleratore generale per QEMU (il _machine emulator_ su cui si basa AVD)

Prima di Android Studio, gli sviluppatori usavano Eclipse per creare applicazioni Android, il quale sfruttava **Maven** come gestore delle dipendenze (uscito del 2004) e si occupava di eseguire automaticamente le fasi necessarie per la costruzione del file APK. Tuttavia, il tool non risultava abbastanza flessibile con l'evoluzione della programmazione Android, tanto da diventare ad un certo punto difficile e scomodo da usare.

Alla conferenza annuale _Google I/O_ del 2013, oltre ad essere stato annunciato Android Studio, venne presentato anche **Gradle** (la dua prima versione uscì nel 2007, ottenne popolarità grazie al mondo Android), che non è altro che un altro sistema di build e gestore di dipendenze, più flessibile rispetto al rimpiazzato Maven: è un sistema di build basato sulla JVM, ciò significa che è possibile scrivere il proprio script in Java che poi potrà essere eseguito.

### L'evoluzione nel tempo

Nel corso dell'anno ho rilasciato diverse versioni di quest'applicazione, tuttavia, si possono distinguere ben 3 revisioni generali del codice originale.

Inizialmente, come linguaggio di programmazione, ho scelto **Java**, essendo un linguaggio fortemente tipizzato ed orientato agli oggetti (_**O**bject **O**riented **P**rogramming_), dove vengono raggruppate in un'unica entità (la **classe**) sia le strutture dati che le procedure che operano su di esse; l'istanza della classe prende il nome di **oggetto**, dotata di proprietà (i dati) e metodi che operano sui dati dell'oggetto stesso.
Il meccanismo più potente ed utile di questo linguaggio per raggiungere i miei obiettivi è stato quello dell'ereditarietà, che mi permette di derivare nuove classi a partire da classi già definite, con la possibilità di aggiungere nuove proprietà e nuovi metodi nella classe derivata, ed eventualmente modificare il comportamento di alcuni metodi definiti nella classe sovrastante laddove è possibile.

#### Prima versione

Nel codice sorgente della prima versione per la comunicazione con il server e per lo scaricamento dei dati ho usato molte classi e metodologie di approccio ai problemi appartenenti al mondo Java.

Un esempio è la procedura di collegamento al server, dove ho impeigato l'utilizzo di:

* un oggetto _HttpsURLConnection_ (classe astratta situata nel package _javax.net.ssl_) che rappresenta la connessione con il server
* il _BufferedInputStream_ legge solitamente _bytes_, i quali mediante un _charset_ possono essere convertiti in caratteri. Quindi per poterlo leggere (dopo averlo opportunamente istanziato mediante l'_InputStream_ ricevuto) ho impiegato l'utilizzo di un _Reader_, altrimenti sarei rimasto costretto ad interpretare char-per-char mediante il metodo ```read()``` (operazione lentissima e dispendiosa)
* un oggetto _BufferedReader_, che richiedeva un _InputStreamReader_, e quest'ultimo prendeva in pasto il BufferedInputStream istanziato precedentemente e il charset su cui doveva basarsi per effettuare la conversione (tutti i charset supportati sono definiti all'interno della classe statica _StandardCharsets_)
  * quest'oggetto offre la possibilità di leggere un'intera riga, e quando si raggiunge la fine dell'InputStream ritorna ```null```
  * mediante l'istanza _StringBuilder_ sono ad aggiungere ogni volta la riga letta (se diversa da ```null```). Le principali motivazioni per cui sono andato ad usare questa classe sono le seguenti:
    * la classe convenzionale **String** non consente l'aggiunta in coda (l'_appending_): ogni volta che si va ad effettuare un'operazione simile a questa ```str += aggStr``` viene creato un nuovo oggetto e il puntatore viene riassegnato, impiegando dunque l'uso del _Garbage Collector_ per andare ad eliminare il vecchio oggetto. Per questo motivo String viene definito come _immutabile_, non riesce a cambiare il suo stato interno
    * ed è qui che assume importanza l'oggetto **StringBuilder**, essendo mutabile: mediante i suoi metodi come ```builder.append(nuovaStr)``` va ad alterare il suo array di char interno, invece di andare a creare un nuovo oggetto

Tutte le tabelle richieste e lette andavano a salvarsi su file JSON presenti all'interno del dispositivo; ciò nonostante, vi erano alcuni svantaggi riguardo alla proceduta:

* i tempi di elaborazione (dopo lo scaricamento) erano troppo lunghi, facendo sprecare tempo prezioso all'agente ad ogni avvio dell'applicazione
* tutta la procedura era circondata (_surrounded_) da una complicatissima **gestione delle eccezioni**, che cercava di risolvere particolari imprevisti come:
  * l'interruzione dell'applicazione durante lo scaricamento
  * l'interruzione dell'applicazione durante l'elaborazione
  * a volte il processo di elaborazione o di scaricamento falliva, quindi bisognava andare anche a gestire quei determinati casi
  * mancanza di collegamento ad Internet da parte dell'agente, quindi bisognava andare a verificare se vi erano già presenti i file scaricati all'interno del dispositivo (questo perché l'applicazione deve continuare a funzionare anche in assenza di connessione) oppure se ritornare un messaggio di errore che bloccava completamente l'accesso all'applicazione
  * ...altre casistiche particolari
* tutta la procedura veniva eseguita all'interno di un thread separato rispetto al thread dell'UI, quindi sono andato a sfruttare il meccanismo dei **semafori** (come facevo in passato su applicazioni desktop tradizionali Java) per far attendere il thread dell'UI la terminazione del thread che si occupava dello scaricamento e dell'elaborazione; tuttavia:
  * dato che andavano a bloccare il thread dell'UI, non potevo far visualizzare alcuna animazione grafica di caricamento, l'applicazione risultava **congelata** durante questo lasso di tempo
  * il sistema Android non si trovava particolarmente a suo agio con l'uso dei semafori, e spesso le loro azioni si facevano risentire anche in applicazioni di terze parti, nel launcher di sistema o nel gestore delle notifiche

Le credenziali (per mantenere il login) le andavo a salvare all'interno di un file XML particolare, accessibile solo dall'applicazione appartenente, mediante il comando ```adb``` da terminale o mediante permessi di root; si trovava nel percorso ```/data/data/[package]/shared_prefs/```, ed era accessibile all'interno dell'applicazione mediante l'API **SharedPreferences**. Esistono diverse modalità con cui si può ottenere un oggetto _SharedPreferences_, tra cui quella di sfruttare la classe ```PreferenceManager``` contenente il metodo statico ```getDefaultSharedPreferences(contesto)``` (per contesto si intende l'istanza di una classe che estende _Context_, come l'activity o il contesto dell'appliacazione generale, accessibile mediante il costrutto ```contesto.getApplicationContext()```).
La peculiarità dell'oggetto _SharedPreferences_ è rappresentata dal fatto che permette la scrittura su uno specifico file XML (contenuto all'interno del percorso sopra indicato) di una piccola _"collezione"_ di coppie chiave-valore (mediante l'oggetto _Editor, e l'eventuale lettura dopo la riapertura dell'applicazione.

In questa versione era possibile scaricare la lista dei clienti dell'agente, i listini di integratori e mangimi disponibili e gli ordini effettuati, mentre era possibile l'invio di visite e ordini (ciascun ordine deve essere composto di almeno un prodotto). Non venne utilizzata dagli agenti.

#### Seconda versione

Nella seconda versione invece, iniziata nel settembre del 2017, dopo essermi documentato adeguatamente sul sito ufficiale [Android Developers](http://developer.android.com/) durante l'estate (al di fuori del lavoro), ho scoperto che il layer **Java API Framework** mette a disposizione una serie di classi e metodi che permettono la creazione di database interni relazionali mediante la libreria integrata **SQLIte**. Quest'ultima, implementa un DBMS SQL di tipo ACID incorporabile da applicazioni.

Per l'acronimo **ACID** si intende l'insieme delle proprietà di cui godono le **transazioni** (unità elementare indivisibile di lavoro):

* **Atomicità**, devono essere resi visibili tutti gli effetti di una transazione, oppure quella transazione non deve avere alcun effetto sulla base di dati (politica _all or nothing_). Non è possibile lasciare la base di dati in uno stato intermedio: se una delle operazioni di lettura o scrittura non può essere portata a compimento, il sistema deve essere in grado di tornare alla situazione iniziale mediante operazioni di _undo_; inoltre, dopo l'esecuzione di un _commit_, il sistema deve assicurare che la transazione lascia la base di dati nel suo stato finale.
* **Consistenza**, l'esecuzione di un transazione non deve violare i vincoli di integrità definit sulla base di dati: quando il sistema rileva che la transazione sta violando uno dei vincoli deve intervenire per annullarla o per correggere ove possibile la viocazione del vincolo.
* **Isolamento**, l'esecuzione di una transazione deve essere indipendente dalla contemporanea esecuzione di altre transazioni: si vuole impedire che l'esecuzione di un _rollback_ di una transazione causi il _rollback_ di altre transazioni (effetto domino). In caso di mancata gestione della concorrenza si possono trovare diversi errori:
  * **Perdita di aggiornamento** dove non viene rilevato l'aggiornamento effettuato da una transazione in un'altra transazione
  * **Dirty read** dove vi è una dipendenza di una transazione da un'altra non ancora completata con successo (quindi abbiamo una lettura dei dati inconsistente)
  * **Unrepeteable read** un'analisi inconsistente dei dati da parte di una transazione causata da aggiornamenti prodotti da un'altra
  * **Phantom read** questo caso si può presentare quando vengono inserite o cancellate tuple che un'altra transazione doveva logicamente considerare
* **Persistenza** l'effetto di una transazione che ha eseguito il _commit_ non deve essere perso

SQLite è una libreria compatta, veloce (in molti casi riesce a superare MySQL), _open-source_, interpreta il linguaggio SQL ed è multipiattaforma: sono molto probabilmente queste le caratteristiche per cui è stata scelta la sua integrazione all'interno del framework Android.
Vi sono presenti alcune lacune, tra cui la mancata gestione dei permessi di accesso, demanadata al software che interagisce con il database o al meccanismo dei permessi del file system; inoltre, non ha una vera gestione della concorrenza, che deve essere implementata dal programma che lo utilizza.

Ero andato a creare una classe denominata _DBHelper_, figlia della classe astratta ```SQLiteOpenHelper``` fornita dal framework Java, il cui scopo era quello di facilitare l'accesso al database interno, automatizzando delle operazioni attraverso l'_overload_ di alcuni metodi, tra cui ```onCreate(db:SQLiteDatabase)```, ```onUpgrade(db:SQLiteDatabase, oldVersion:int, newVersion:Int)``` (richiamato quando l'applicazione viene aggiornata, in particolare, quando cambia lo schema del database, ed aumenta la versione del database) ed ```onDowngrade(db:SQLiteDatabase, oldVersion:int, newVersion:Int)``` (ricihiamato solitamente quando si passa ad una versione precedente dell'applicazione, la versione del database "scende"); gli ultimi due metodi citati devono contenere tutte le operazioni necessarie per garantire la persistenza dei dati quando lo schema del database cambia (rimorzione di una colonna, aggiunta di una tabella, modifica di una colonna ecc.).

Ogni volta per andare ad eseguire un'operazione sul database (lettura, scrittura, modifica) andavo a scrivere i comandi manualmente mediante i metodi ```execSQL("INSERT/DELETE/UPDATE")``` e ```rawQuery("QUERY")```(ritornava un oggetto ```Cursor``` con il quale era possibile andare a prelevare i dati al suo interno, scorrendolo con il metodo ```moveToNext()```). Rappresentava comunque un grande passo avanti rispetto alla versione precedente, dato che non dovevo più andare a gestire i file, ed era possibile esprimere più relazioni con minore difficoltà.

La REST API cambiò (seconda revisione), e cambiarono anche i metodi di acesso ai dati: quindi venne rinnovata tutta la parte relativa alla comunicazione con il server.
Dopo aver rimosso i problematici semafori, ho inziato ad usare la classe astratta _AsyncTask_ integrata nel framework, che mi ha facilitato la gestione delle operazioni in background (in questo caso, il download delle tabelle): questa classe non sostituisce in alcun modo i Thread (non è una libreria di threading), ed è possibile anche specificargli un _timeout_ di esecuzione.

In questa versione ho implementato la registrazione vocale sugli ordini, l'uso della fotocamera per scattare le foto dei timbri aziendali o l'eventuale caricamento di una foto presente all'interno del dispositivo. Con queste funzioni ho docuto prevedere anche la gestione del file system interno, ed assicurare la retro-compatibilità con versioni di Android inferiori a Lollipop.

Per risolvere il problema dell'elaborazione della risposta in tempi troppo lunghi, ho impiegato l'uso della libreria **Apache IO Commons**, che permetteva un'elaborazione fulminea dell'intero InputStream di risposta: successivamente, dopo alcune settimane dal rilascio della versione, il suo impego sui telefoni degli agenti si rilevò disastroso, portando problemi di compatibilità su processori con un certo tipo di architettura (in particolare, sui processori x86 di Intel l'applicazione non poteva essere installata, e su alcuni processori MediaTek) e crash improvvisi dell'intera applicazione. Questa versione rappresentò un vero e proprio **fallimento**.

#### Terza versione

Nel periodo novembre-dicembre, Federico si è occupato di aggiornare il portale e l'API REST (la terza ed ultima grande revisione), portando una maggiore chiarezza sui metodi di accesso alle risorse, ed un miglioramento netto in termini di performace e stabilità, mentre io ho cominciato la scrittura della terza revisione.

Essendo stato un fiasco totale la versione precedente, decisi di riscrivere il codice sorgente da zero, e ne approfittai per inziare a scriverlo interamente in **Kotlin**, il linguaggio di programmazione della software-house JetBrains, divenuto nuovo linguaggio di programmazione **ufficiale** per Android alla conferenza Google I/O 2017 (rimpiazzando Java).

Le caratteristiche che lo contraddistinguono sono le seguenti:

* è al 100% **interoperabile** con le classi e le librerie Java e Android
* è possibile scrivere il codice in maggiore sicurezza, dato che la sintassi di Kotlin è stata progettata per evitare il più possibile il famoso ```NullPointerException```
* la parte statica dalla parte dinamica della classe si contraddistingue in maniera più netta, mediante il costrutto ```companion object```: questo perché, rispetto a Java, il ```companion object``` non rappresenta altro che un oggetto fornito con una classe in Kotlin (indipendente dalla parte dinamica: questo oggetto può addirittura estendere classi che non sono estese dalla classe che lo comprende)
* meccanismi di _delegazione_ (ho affrontato per ora solamente casi semplici; in futuro, espanderò le mie ricerche)
* leggibile e circonciso, meno codice _boilerplate_ rispetto a Java
* supporto alle espressioni _lambda_ (introdotte anche su Java nella versione 8)
* su Android, si può dire addio al metodo ```findViewById(int risorsa)``` (il quale permetteva di usare il componente dichiarato all'interno del file XML di layout)
* estensione delle funzionalità di un particolare tipo senza andare ad usare il meccanismo dell'ereditarietà
* consentita la programmazione funzionale
* può essere compilato in JavaScript (come TypeScript)

Il processo di compilazione, usando questo linguaggio, non cambia in alcun modo: tutto il codice Kotlin, mediante apposito compilatore, viene convertito in bytecode Java in modo tale da poter funzionare su tutti i dispositivi Android (JetBrains sta lavorando al progetto _Kotlin/Native_, per far eseguire in un futuro codice Kotlin senza il supporto della **J**ava **V**irtual **M**achine di _Oracle_).

La libreria problematica _Apache IO Commons_ è stata completamente rimossa, ed è stata sostituita con la classe Scanner: si rilevò sorprendentemente utilie, rilevandosi più veloce la lettura ripetto all'approccio della prima versione, ma non fulminea quanto la seconda; tuttavia, permise di mantenere una certa stabilità e velocità all'interno dell'applicazione.

Tutte le activity sono state rinnovate, permettendo una maggiore modularità rispetto alle versioni precedenti: per esempio, veniva usata una sola activity per la selezione sia di clienti che di prodotti (successivamente si rivelò una cattiva idea in termini di gestione del codice, ma ha funzionato).

Il servizio REST rinnovato non prevedeva l'invio di file multimediali, dato che l'azienda ci ha chiesto prima di concentrarci sul migliorare la ricezione delle tabelle e l'invio dei nuovi ordini/visite; perciò, funzioni che includevano la fotocamera o la registrazione vocale, non sono state incluse.

Per andare ad eliminare codice _boilerplate_ dedicato all'interpretazione del JSON scaricato e alla creazione delle tabelle, all'inserimento e alle interrogazioni, avevo creato una piccola libreria capace di creare query in maniera più dinamica, tuttavia:

* doveva essere qualcosa di standardizzato, e invece, si rivelò utile solamente ai casi di cui avevo bisogno inizialmente (non avevo previsto ad esempio interrogazioni che richiedevano JOIN o selezioni particolari)
* il processo di creazione e di esecuzione della query non era tanto veloce quanto la scrittura della query manuale

Con questa libreria, ho cercato dunque di fornire un livello di astrazione sulla formazione delle query. Si basava molto sull'utilizzo delle **interfacce** Java/Kotlin (su Kotlin è possibile andare a dichiarare metodi già definiti al loro interno ed attributi astratti): sono simili alle classi, ma contengono solo dichiarazioni di metodi.

Devo ammettere che inizialmente non riuscivo a dare una giustificazione alla loro esistenza, eppure, si sono rilevate davvero utili nel campo: **la dichiarazione di un metodo garantisce che sarà presente in tutte le sottoclassi**.

La libreria era composta da:

* un'interfaccia, _TableProperties_, contenente una serie di attributi che dovevano essere definito all'interno dei figli
  * pk, contenente il nome della chiave primaria presente all'interno della mappa
  * map, una mappa di stringhe, ```Map<String,String>```, contenente il nome della colonna (e del campo all'interno del JSON scaricato) e il tipo della colonna
  * TABLE_NAME, una stringa contenente il nome della tabella
  * fks, contenente riferimenti alle classi statiche figlie di TableProperties: infatti, era un'array di TableProperties
    * il nome della colonna **foreign key** doveva avere lo stesso nome della colonna primary key della tabella padre
  * alcuni metodi definiti per la creazione/interrogazione di tutte le colonne di una tabella ecc.
* tanti figli ```object``` (si può definire un _companion object_ con un suo nome, appunto, l'_object_) di TableProperties che definevano i pezzi necessari per costruire le tabelle; si può dire che questi figli avevano lo scopo di **rappresentare** la struttura della loro entità
* alcune tabelle erano scaricabili, quindi i figli potevano implentare l'interfaccia _Downloadable_ (tra i campi contenuti vi era il nome della funzione da richiedere alla RESTful API)
* alcune tuple delle tabelle erano inviabili, perciò i figli rappresentanti implementavano l'interfaccia _Uploadable_
* vi erano anche figli che implementavano entrambe le ultime due interfacce citate (ad esempio, è possibile scaricare la lista dei clienti così com'è possibile inviare un nuovo cliente)

Tutto ciò mi ha permesso di creare un _for_ che scorreva tutti gli ```object``` figli all'avvio dell'applicazione, quindi veniva creata ciascuna tabella se non esisteva (più pulito e leggibile il codice rispetto alla seconda versione), e se implementava la classe _Downloadable_, iniziava anche la procedura di _scaricamento_ e di _salvataggio_.

Successivamente, è stata anche implementata la comunicazione con il servizio RESTful dedicato alla verifica dell'ultima versione disponibile, ed in questo modo gli agenti possono sapere se la loro applicazione è aggiornata (non essendo disponibile su alcun _marketplace_ digitale).

Questa versione è stata usata dagli agenti a partire da febbraio, ed ho continuato ad aggiornarla ed a implementare nuove funzionalità.

#### Quarta versione

L'azienda mi ha chiesto di implementare la possibilità di avere più account agente all'interno del dispositivo: avrei potuto cambiare il package dell'applicazione per poter avere tanti clone dell'app sullo stesso dispositivo, ma ho deciso di non farlo, sarebbe stata una ridondanza della stessa applicazione, influendo sulla memoria di massa occupata nel dispositivo.

Verso marzo ho scoperto l'esistenza di un nuovo set di librerie, _Android Architecture Components_, presentato alla conferenza Google I/O del 2017: queste librerie permettono di sviluppare applicazioni **robuste**, **testabili** (mediante librerie come _JUnit_) e **manutenibili**.

##### La libreria Room

Una di queste librerie prende il nome di **_Room_**, la quale fornisce **un livello di astrazione** su SQLite per consentire un accesso più comodo al database:

* ciascuna entità viene rappresentata da una classe, annotata con l'annotazione ```@Entity```
  * ciascun parametro rappresenta una colonna all'interno della tabella
  * mediante l'annotazione ```@PrimaryKey``` è possibile andare a definire la primary key dell'entità
  * mediante l'annotazione ```@ForeignKey``` è possibile andare a definire le foreign keys
  * mediante l'annotazione ```@Index``` è possibile andare a definire gli indici secondari
  * tutti i parametri devono essere ```public```, oppure possono essere ```private``` con gli opportuni getters e setters che rispettino le convenzioni _JavaBeans_ (```getNome()``` e ```setNome(nome:String)``` per il parametro ```nome```)
  * parametri che devono essere ignorati (non inclusi nella tabella) devono essere annotati con ```@Ignore```
  * parametri con un tipo non primitivo o diverso da String devono essere convertiti mediante classi specializzate che possono essere definite dal programmatore, annotate con ```@TypeConverter```
* le relazioni (1 a 1, 1 a N, N a N) tra le entità vengono rappresentate da classi separate (mediante le opportune annotazioni, ```@Embedded``` e ```@Relation```) conenente gli oggetti delle entità
* ciascuna entità (o ciascuna relazione) può avere un suo set di query: devono essere definite all'interno di un'interfaccia annotata con ```@Dao``` contenente metodi astratti annotati con ```@Query("SELECT/INSERT/DELETE")```, ```@Insert```, ```@Delete```o ```@Update```
* la classe che rappresenta il database:
  * deve essere astratta
  * deve estendere la classe astratta ```RoomDatabase``` fornita dal framework
  * annotata con ```@Database``` contenente:
    * devono essere dichiarate tutte le classi delle entità
    * la versione del database
  * devono essere dichiarate tutte le classi TypeConverter all'interno dell'annotazione ```@TypeConverters```
  * deve contenere metodi astratti che ritornano le istanze Dao generate dal compilatore mediante le interfaccie annotate con ```@Dao```

In Java (così come in Kotlin), le **annotazioni** sono una forma di metadati: forniscono informazioni su un programma che non fa parte del programma stesso. Le annotazioni non hanno alcun effetto diretto sul funzionamento del codice annotato.
In questo caso le annotazioni sono utili al compilatore: mediante le annotazioni è in grado di generare codice Java dalle classi, dagli attributi e dai metodi annotati opportunamente, che verrà poi convertito in bytecode per il device.

##### Le basi di dati

<!--immagine con la prima base di dati-->

Ciascun agente che esegue il login con successo viene registrato nel database interno dedicato agli accessi, _Agenti.db_, contenente la sola tabella _Agenti_: qui sono incluse tutte le informazioni dell'agente, ottenute in fase di login, ed il token con cui è possibile effettuare le richieste (con tanto di data di scadenza). La primary key è il codice dell'agente, quindi non è possibile fare il login più volte con lo stesso codice agente.

Per ciascun agente viene creato un database, _x.db_ (x è il codice dell'agente, essendo univoco all'interno della tabella _Agenti_).
Vi sono alcuni campi particolari, come _listini_ all'interno di _Ordine_, contenente fisicamente una striga, ma attraverso un TypeConverter definito viene convertito in ```Array<ProdottiListino>``` in runtime, mediante la libreria **Gson** (libreria che permette di convertire oggetti Java nella loro rappresentazione in JSON, e viceversa, mediante i metodi ```fun toJson(oggetto:Tipo):String``` e ```fun<Tipo> fromJson(json:String, classeTipo:Class<*>):Tipo```.

Come ho descritto nel paragrafo _I dati_, per aggiornare una singola tabella viene cancellato l'intero contenuto della tabella e vengono inserite le nuove tuple.

Prendiamo come esempio la relazione tra la tabella Visite e la tabella Clienti: quando cancello l'intero contenuto di Clienti viene lanciata un'eccezione, dato che Visite contiene riferimenti alle tuple di Clienti (oppure vengono perse tutte le visite se la politica di reazione è _ON DELETE CASCADE_). Per evitare il problema, ho sostituito la foreign key della Visita con un **indice secondario**: tutto il contenuto della tabella Clienti viene cancellato, senza andare ad instaccare la tabella Visite, e successivamente, una volta che il processo di aggiornamento è stato completato, mediante una query vado a cancellare tutte le tuple che contengono riferimenti a codici cliente non più esistenti.

```sql
DELETE FROM Visite
WHERE codiceCliente NOT IN (SELECT c.codiceCliente
FROM Clienti c);
```

Sia la tabella Scaduti che la tabella Ordini contengono riferimenti a Clienti: anche qui le _foreign key_ sono state rimpiazzate con gli indici; tuttavia, la query che va a cancellare le tuple che non contengono riferimenti a clienti non è necessaria, in quanto sono tabelle scaricate dalla RESTful API: se il contenuto delle tabelle Scaduti e Ordini cambia (come l'eliminazione di un cliente, che porterebbe ad una cancellazione di tutti gli ordini e gli scaduti relativi), cambia anche l'hash MD5 delle due tabelle, e di conseguenza il dispositivo si trova costretto ad aggiornarle.

Gli indici rappresentano una struttura ridondante.

##### Comunicazione con il server

Per implementare la comunicazione con il server ho usato una libreria di terze parti, **Retrofit** (mantenuta dall'azienda Square), che mi permette di definire un'interfaccia, contenente metodi astratti annotati con le opportune annotazioni (per specificare se la richiesta è GET/POST/DELETE/..., la **path** della richiesta, manipolazione dell'URL o degli headers ed altro), e chiascun metodo rappresenta una determinata chiamata al server mediante il protocollo HTTP. La chiamata può essere gestita in modo _sincrono_ o _asincrono_.

Anche qui, essendoci le annotazioni, il compilatore genererà il codice necessario per il suo funzionamento.

Dato che il funzionamento di questa libreria si basa sulla libreria _OkHttp_, il body può essere deserializzato solo nel tipo ```ResponseBody``` senza un _converter_ definito.

Il **converter** (in questo progetto ho impiegato l'uso del _converter Gson_) ha il compito di convertire il body nel tipo specificato (mediante l'uso dei _Generics_), quindi con la possibilità di esprimere espressioni come ```fun getAll(parametri):Call<Tipo>``` all'interno dell'interfaccia.