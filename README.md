# PLD Agile

**Hexanôme H21**

## Utilisation

Exécuter le code principal :
```bash
java -cp target/classes app.Main
```

Compiler le code principal :
```bash
javac -d target/classes $(find src/main/java -name '*.java')
```

## Tests

Compiler les tests :
```bash
javac -cp target/classes -d target/test-classes src/test/java/model/*.java
```

Exécuter les tests :
```bash
java -cp target/classes model.<classTest>
```

## Ressources

- [Google Drive](https://drive.google.com/drive/folders/1LWnPBXDfblA7rICWYRWhbtlnZqZKL9tM?usp=sharing)
- [Wiki](https://github.com/INSA-4IF-Hexanome21/PLD-Agile/wiki)