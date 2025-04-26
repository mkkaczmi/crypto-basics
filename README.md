# Cryptography basics

This repository contains an implementation of the Triple DES (3DES) encryption algorithm with both a graphical user interface (GUI) and a standalone version.

## Project Structure

- `TripleDES/` - Main project directory containing the implementation
  - `TripleDESGui.java` - GUI version of the Triple DES implementation
  - `TripleDESStandalone.java` - Standalone version of the Triple DES implementation
  - `graph.jpg` - Performance analysis graph
  - `README.md` - Detailed project documentation

## Features

- Triple DES encryption and decryption
- Two implementation modes:
  - GUI version with user-friendly interface
  - Standalone version for command-line usage
- Support for different key lengths and modes of operation
- Performance analysis and benchmarking

## Requirements

- Java Development Kit (JDK) 8 or higher
- Java Runtime Environment (JRE) 8 or higher

## Usage

### GUI Version

1. Compile the GUI version:
   ```bash
   javac TripleDESGui.java
   ```

2. Run the GUI application:
   ```bash
   java TripleDESGui
   ```

### Standalone Version

1. Compile the standalone version:
   ```bash
   javac TripleDESStandalone.java
   ```

2. Run the standalone application:
   ```bash
   java TripleDESStandalone
   ```

## License

This project is licensed under the MIT License.
