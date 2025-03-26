# TripleDES Encryption Program

This is a Java implementation of the Triple DES (Data Encryption Standard) algorithm with a graphical user interface. The program allows users to encrypt and decrypt text using three different DES keys.

## Prerequisites

- Java Development Kit (JDK) 8 or higher
- Java Runtime Environment (JRE)
- A Java IDE (optional) or command line tools

## Files Structure

The program consists of two main files:
- `TripleDESStandalone.java` - Contains the core encryption/decryption logic
- `TripleDESGui.java` - Contains the graphical user interface implementation

## How to Compile and Run

### Using Command Line

1. Open a terminal/command prompt
2. Navigate to the directory containing the source files:
```bash
cd path/to/your/files
```

3. Compile both Java files:
```bash
javac TripleDESStandalone.java
javac TripleDESGui.java
```

4. Run the program:
```bash
java TripleDESGui
```

### Using an IDE

1. Create a new Java project in your IDE
2. Copy both source files into your project's source directory
3. Build the project
4. Run `TripleDESGui.java` as the main class

## How to Use the Program

1. When the program starts, you'll see a window with several sections:
   - Keys section at the top
   - Input/Output text areas in the middle
   - Control buttons at the bottom

2. Generate Keys:
   - Click the "Generate Keys" button to create three default keys
   - The keys will automatically be set in the key fields

3. To Encrypt Text:
   - Enter your plain text in the input text area
   - Click the "Encrypt" button
   - The encrypted text (in hexadecimal format) will appear in the output area

4. To Decrypt Text:
   - Copy the encrypted hexadecimal text into the input area
   - Click the "Decrypt" button
   - The decrypted text will appear in the output area

## Notes

- All keys must be 16 hexadecimal characters long
- The program uses padding for messages that aren't multiples of the block size
- Encrypted text is displayed in hexadecimal format
- When decrypting, make sure to input the exact hexadecimal text that was generated during encryption

## Error Handling

The program will display error messages in the status label at the bottom of the window for various situations:
- Invalid key length or format
- Empty input text
- Invalid hexadecimal input for decryption
- Other encryption/decryption errors

## Security Considerations

- This implementation is for educational purposes
- For production use, consider using standard cryptographic libraries
- Keep your keys secure and never share them
- Triple DES is considered legacy; for new applications, consider using AES

## Troubleshooting

If you encounter any issues:
1. Make sure both Java files are in the same directory
2. Verify that you have the correct Java version installed
3. Check that all keys are properly set before encryption/decryption
4. Ensure the input text is in the correct format (plain text for encryption, hex for decryption)

## License

This project is provided as-is for educational purposes.