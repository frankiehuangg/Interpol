# Interpol - Java

## Requirements

- Java
- maven

## Setup

First, make sure all requirements are installed.

Then compile the code using `maven`.

```bash
$ mvn clean package
```

The ELF file will be placed in the `target` directory.

```bash
$ java -jar target/interpol-java-1.0-SNAPSHOT-jar-with-dependencies.jar
```

You can use the `-h` or `--help` flag to show the help information.

```bash
$ java -jar target/interpol-java-1.0-SNAPSHOT-jar-with-dependencies.jar --help
```
