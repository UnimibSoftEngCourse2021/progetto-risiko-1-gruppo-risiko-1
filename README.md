# Risiko

## Sviluppo in locale

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
Per effettuare la build ed eseguire il progetto è sufficiente il comando
```
mvn spring-boot:run
```