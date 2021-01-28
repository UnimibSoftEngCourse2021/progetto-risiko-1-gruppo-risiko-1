# Risiko

## Sviluppo in locale

### Requisiti
* Maven (versione 3.6.3 o superiore)
* Java (JDK versione 15)
* MySql (versione 8)
#### Requisiti secondari
* node (solo per analisi con Sonar, versione 12.18.2 o superiore)
* npm (solo per sviluppo del frontend, v6.14.11)

### Preparazione del database
Il DBMS scelto per ospitare il database dell'applicazione è MySQL.
Lo script `initalize_db.sql` contiene uno script per inizializzare il database e popolarlo con
la mappa di default.

Prima di eseguire l'applicazione è necessario inoltre creare un utente `risiko_user` con password
`eng.soft2020` e assegnargli tutti i permessi per l'accesso al db. (sono le credenziali
usate dall'applicazione per collegarsi al db):
```
create user 'risiko_user'@'%' identified by 'eng.soft2020';
grant all privileges on risiko_db.* to 'risiko_user'@'%';
flush privileges;
```
Ovviamente, queste credenziali potranno essere aggiornate in fase di rilascio. Si può modificare le credenziali usate
dall'applicazione per l'accesso al DB nel file `main/resources/application.properties`.

### Come eseguire il progetto
Per effettuare la build ed eseguire il progetto è sufficiente il comando:
```
mvn spring-boot:run
```
L'applicazione sarà attiva sulla porta 8080, e può essere testata tramite browser all'indirizzo `localhost:8080`.

### Come analizzare il progetto

Se si vuole effettuare la build ed eseguire i controlli di quality assurance (test, code coverage, 
sonar analiysys) bisogna utilizzare il Maven profile "qa" (quality-assurance):
```
mvn clean install sonar:sonar -Pqa -Dsonar.login={SONARCLOUD_TOKEN}
```

Di default viene utilizzato Sonarcloud per l'analisi, se non si desidera utilizzarlo è sufficiente passare 
come parametri di sonar tutte le configurazioni necessarie (server, login, ecc...) a linea di comando.

N.B. Affinché il modulo Javascript venga correttamente riconosciuto dall'analisi sonar è necessario che `node` sia
installato sulla macchina che effettua l'analisi.

### Come eseguire i test
Se si è solamente interessati all'esecuzione dei test, senza ulteriori analisi di QA, è sufficiente utilizzare il
comando:
```
mvn clean test -Pqa
```
Vi sono due tipi di test: alcuni sono strettamente classificabili come Unit test e testano il comportamento di una 
singola classe, altri sono più complessi (quasi degli integration test) e testano la risposta dell'applicazione di fronte
a richieste provenienti dall'esterno (richieste http).
Per testare l'applicazione in modo completo senza tuttavia modificare i dati presenti nel database di sviluppo, è stato
predisposto un database in-memory di tipo H2 che viene utilizzato in automatico solo per i test: esso è inizializzato
ogni volta mediante lo script `test/resources/test_db.sql` e contiene in partenza solo la mappa standard del Risiko.

### Sviluppo del frontend
Quando viene effettuata la build con Maven, viene effettuato la build del frontend ed i file html/css/js statici così
prodotti vengono inseriti tra le risorse esposte dall'applicazione Spring boot.
Ciò è molto comodo in fase di rilascio in quanto consente di produrre un'applicazione funzionante con un singolo
comando, tuttavia è scomodo in fase di sviluppo poiché non consente di sviluppare il frontend sfruttando la capacità di
[hot reload](https://vue-loader.vuejs.org/guide/hot-reload.html) del framework Vue js.

Per sviluppare il Frontend è dunque preferibile eseguire i due moduli software in modo separato, con il seguente metodo:
* avviare il backend springboot sulla porta 8080
* spostarsi nella directory `risiko-frontend`
* eseguire `npm install` per scaricare tutte le dipendenze javascript (solo la prima volta)
* eseguire `npm run serve` per avviare il frontend in modo separato rispetto al backend, su una porta differente (in 
  genere la 8081, ma non necessariamente)
  
In `risiko-frontend/vue.config.js` è stata predisposta una proxy (attiva solo in fase di sviluppo) che indirizza tutte
le chiamate a indirizzi non noti del frontend (ossia le `**/api/**`) alla porta 8080 dove dovrebbe essere attivo il backend.
Chiaramente, il frontend sarà testabile alla porta comunicata da npm dopo aver eseguito il comando `npm run serve`.

