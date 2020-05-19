# MovingFast
Gruppo17 project SMS1920
PROGETTO SMS – APP NOLEGGIO MONOPATTINI

Il sistema è diviso in due componenti: la componente app che va installata su smartphone e la componente monopattino, per cui abbiamo pensato di sviluppare due applicazioni. L’app monopattino servirà per simulare le funzionalità del mezzo. Di seguito indicheremo le funzionalità divise per app.

# APP SMARTPHONE
Questa è l’applicazione che va installata da parte dell’utente sul proprio smartphone.
Abbiamo pensato che ci possano essere due tipi di utenti:
- Utente FRUITORE, è colui che utilizza il servizio di noleggio del monopattino;
- Utente MANUTENTORE, è colui che si occupa della manutenzione ordinaria dei monopattini, e che utilizzerà la medesima app per scopi diversi dal FRUITORE.

# UTENTE NON REGISTRATO:
L’utente non registrato al sistema può, una volta installata l’app sul proprio dispositivo REGISTRARSI al sistema con un indirizzo di posta elettronica e indicando i suoi dati anagrafici nel caso in cui è un Utente di tipo FRUITORE.
Nel caso in cui egli sia un Utente Manutentore, abbiamo ipotizzato che le credenziali di accesso gli saranno fornite direttamente dall’azienda che gestisce il servizio noleggi, per cui per lui non è prevista una registrazione in-app.
Di conseguenza per l’utente non ancora registrato il requisito sarà la registrazione.

# UTENTE FRUITORE:
- ACCESSO. L’utente accede al sistema tramite una schermata che gli permette di inserire le sue credenziali con cui si è precedentemente registrato.
- GEOLOZALIZZAZIONE SU MAPPA. L’utente una volta registrato, potrà vedere la sua posizione sulla mappa.
- TROVA MONOPATTINO. L’utente potrà visualizzare sulla mappa la posizione dei monopattini.
- VISUALIZZA MONOPATTINO. L’utente cliccando sull’icona del monopattino, dalla mappa, può visualizzare le info del mezzo come il suo stato di carica e il collegamento al navigatore (google maps) per raggiungerlo
- NOLEGGIA MONOPATTINO. Una volta raggiunto il monopattino, l’utente avvicinandosi potrà sbloccare il mezzo tramite l’utilizzo dell’NFC. Il sistema registra il tempo di sblocco per calcolarne successivamente i costi. Finita la sessione di noleggio l’utente può bloccare il monopattino. Il sistema memorizza il tempo di blocco per calcolarne il tempo della sessione di noleggio, in base al tempo e alla tariffa.
- PORTAFOGLIO: l'utente ricarica il credito da utilizzare per i noleggi.


# UTENTE MANUTENTORE:
- ACCESSO. L’utente accede al sistema tramite una schermata che gli permette di inserire le sue credenziali con cui si è precedentemente registrato.
- GEOLOZALIZZAZIONE SU MAPPA. L’utente una volta registrato, potrà vedere la sua posizione sulla mappa.
- TROVA MONOPATTINO. L’utente potrà visualizzare sulla mappa la posizione dei monopattini.
- VISUALIZZA MONOPATTINO. L’utente cliccando sull’icona del monopattino, dalla mappa, può visualizzare le info del mezzo come il suo stato di carica.
- MANUTENZIONE *. Una volta eseguita un’operazione di manutenzione, il manutentore può segnalarlo al sistema, specidicando il tipo di intervento (ricarica, …)
(*)Si può pensare che tali operazioni possono essere usati a fini statistici dall’azienda anche per eventuali bonus sulle ricompense dei manutentori.
NB: abbiamo pensato che l’utente FRUITORE non debba occuparsi della ricarica dei monopattini. Se da un lato è vero che tale operazione incentiverebbe alla sostenibilità della carica elettrica, dall’altro non contribuisce all’idea di servizio “condiviso”. Infatti, tale operazione incentiva gli utenti FRUITORI a portare a casa i monopattini, e così facendo potrebbe privare del servizio altri utenti. Inoltre sarebbe cosa poco gradita visualizzare sulla mappa un monopattino ma una volta sul posto non trovarlo poiché qualcun altro lo ha portato nel suo appartamento.



# APP MONOPATTINO
La componente monopattino serve solo per geolocalizzare il mezzo e per interagire con la componente smartphone per il blocco e lo sblocco del mezzo.
