# PLD Agile

**Hexanôme H21**

## Utilisation

Installer les dépendances Maven :
```bash
mvn install
```

Compiler le code principal :
```bash
javac -d target/classes $(find src/main/java -name '*.java')
```

Exécuter le code principal :
```bash
java -cp target/classes app.Main
```

## Tests

Exécuter les tests :
```bash
mvn clean test
```

Pour voir les résultats du test, ouvrir le fichier généré :
```bash
target/site/jacoco/index.html
```

## Ressources

- [Google Drive](https://drive.google.com/drive/folders/1LWnPBXDfblA7rICWYRWhbtlnZqZKL9tM?usp=sharing)
- [Wiki](https://github.com/INSA-4IF-Hexanome21/PLD-Agile/wiki)
