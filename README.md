# Diabete Tracker

> Un software per la comunicazione tra paziente affetto da diabete di tipo 2 e medico.

## 📋 Requisiti

Prima di compilare o eseguire il progetto, assicurarsi di avere installato:

- **Java JDK 17** (o versione compatibile — verifica con `java -version`)
- **Apache Maven 3.8+** (verifica con `mvn -version`)

## 📦 Dipendenze

Tutte le dipendenze del progetto (incluso **JavaFX**) sono gestite interamente tramite **Maven** e sono definite nel file [`pom.xml`](./pom.xml).

Non è necessario scaricare o configurare manualmente alcuna libreria: Maven si occuperà automaticamente di:

- scaricare le dipendenze richieste (comprese le librerie JavaFX);
- gestire le versioni;
- includere le dipendenze nel classpath durante la compilazione e l'esecuzione.


## 🛠️ Compilazione

Per compilare il progetto, posizionarsi nella cartella radice (dove si trova il file `pom.xml`) ed eseguire:

```bash
mvn clean compile
```

Questo comando:
1. Pulisce eventuali build precedenti (`clean`)
2. Scarica le dipendenze necessarie
3. Compila il codice sorgente

## ▶️ Esecuzione

### 1. Generare il database di prova

Prima di avviare l'applicazione, è **necessario** eseguire la classe `GeneratoreDatiTest`, che popola un database di prova indispensabile per il funzionamento dell'app.

```bash
mvn exec:java -Dexec.mainClass="application.classiGeneriche.GeneratoreDatiTest"
```

### 2. Avviare l'applicazione JavaFX

Una volta generato il database di prova, avviare l'applicazione con il plugin Maven per JavaFX:

```bash
mvn clean javafx:run
```
