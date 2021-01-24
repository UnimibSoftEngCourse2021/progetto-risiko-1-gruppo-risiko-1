# Risiko

## Sviluppo in locale

### Requisiti
* Maven (versione 3.6.3 o superiore)
* Java (JDK versione 15)
* node (per analisi con Sonar, versione 12.18.2 o superiore)
* MySql (versione 8)

### Impostare il database
Il DBMS scelto per ospitare il database dell'applicazione è MySQL.
Lo script `initalize_db.sql` contiene uno script per inizializzare il database e popolarlo con
la mappa di default.
Prima di eseguire l'applicazione è necessario creare un utente `risiko_user` con password
`eng.soft2020` e assegnargli tutti i permessi per l'accesso al db. (sono le credenziali
usate dall'applicazione per collegarsi al db)
```
GRANT ALL PRIVILEGES ON risiko_db TO 'risiko_user'@'%';
FLUSH PRIVILEGES;
```

### Come eseguire il progetto
Per effettuare la build ed eseguire il progetto è sufficiente il comando:
```
mvn spring-boot:run
```

Se si vuole effettuare la build ed eseguire i controlli di quality assurance (test, code coverage, 
sonar analiysys) bisogna utilizzare il Maven profile "qa" (quality-assurance):
```
mvn clean install sonar:sonar -Pqa -Dsonar.login={SONARCLOUD_TOKEN}
```

Di default viene utilizzato Sonarcloud per l'analisi, se non si desidera utilizzarlo è sufficiente passare 
come parametri di sonar tutte le configurazioni necessarie (server, login, ecc...) a linea di comando.

N.B. Affinché il modulo Javascript venga correttamente riconosciuto dall'analisi sonar è necessario che `node` sia
installato sulla macchina che effettua l'analisi.