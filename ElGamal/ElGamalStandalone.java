import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class ElGamalStandalone {
    private BigInteger g, h, a, p;
    private Random random;

    public ElGamalStandalone() {
        random = new Random();
        generateKey();
    }

    // File operation methods
    public static byte[] readFromFile(String filename) throws IOException {
        File file = new File(filename);
        byte[] data = new byte[(int) file.length()];
        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(data);
        }
        return data;
    }

    public static void writeToFile(byte[] data, String filename) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(filename)) {
            fos.write(data);
        }
    }

    public static BigInteger[] readBigIntArrayFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (BigInteger[]) ois.readObject();
        }
    }

    public static void writeBigIntArrayToFile(BigInteger[] data, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(data);
        }
    }

    public static void writeBigIntArrayToFileWithExtension(BigInteger[] data, String filename, String originalExtension) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(originalExtension);  // Write the original extension first
            oos.writeObject(data);              // Then write the encrypted data
        }
    }

    public static Object[] readBigIntArrayAndExtensionFromFile(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            String originalExtension = (String) ois.readObject();
            BigInteger[] data = (BigInteger[]) ois.readObject();
            return new Object[]{originalExtension, data};
        }
    }

    public static void writeBigIntArrayToFileNewLine(BigInteger[] data, String filename) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            for (BigInteger bi : data) {
                writer.println(bi.toString(16));
            }
        }
    }

    // Data conversion methods
    public static byte[] subArray(byte[] array, int start, int end) {
        byte[] result = new byte[end - start];
        System.arraycopy(array, start, result, 0, end - start);
        return result;
    }

    public static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }

    public static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }

    public static String bigIntToString(BigInteger bi) {
        byte[] bytes = bi.toByteArray();
        return new String(bytes);
    }

    public static BigInteger stringToBigInt(String s) {
        return new BigInteger(1, s.getBytes());
    }

    // Core ElGamal methods
    public void generateKey() {
        // Generate a large prime number p
        p = BigInteger.probablePrime(512, random);
        
        // Find a generator g
        g = findGenerator(p);
        
        // Generate private key a
        a = new BigInteger(512, random).mod(p.subtract(BigInteger.ONE));
        
        // Calculate public key h = g^a mod p
        h = g.modPow(a, p);
    }

    private BigInteger findGenerator(BigInteger p) {
        BigInteger pMinus1 = p.subtract(BigInteger.ONE);
        BigInteger g;
        do {
            g = new BigInteger(512, random).mod(p);
        } while (g.modPow(pMinus1, p).compareTo(BigInteger.ONE) != 0);
        return g;
    }

    public BigInteger[] encrypt(byte[] message) {
        if (message == null || message.length == 0) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        if (g == null || h == null || p == null) {
            throw new IllegalStateException("Keys not initialized. Please generate keys first.");
        }

        try {
            // Generate random r
            BigInteger r = BigInteger.probablePrime(512, random);
            BigInteger Nm1 = p.subtract(BigInteger.ONE);
            while (!r.gcd(Nm1).equals(BigInteger.ONE)) {
                r = r.nextProbablePrime();
            }

            // Calculate chunk size based on p's bit length
            int chunkSize = (p.bitLength() - 1) / 8;
            int chunks = (message.length + chunkSize - 1) / chunkSize; // Ceiling division
            BigInteger[] result = new BigInteger[chunks * 2];

            for (int i = 0; i < chunks; i++) {
                int start = i * chunkSize;
                int end = Math.min(start + chunkSize, message.length);
                byte[] chunk = subArray(message, start, end);
                
                // Convert chunk to BigInteger
                BigInteger m = new BigInteger(1, chunk);
                
                // Calculate c1 = g^r mod p
                BigInteger c1 = g.modPow(r, p);
                
                // Calculate c2 = m * h^r mod p
                BigInteger c2 = m.multiply(h.modPow(r, p)).mod(p);
                
                result[i] = c1;
                result[chunks + i] = c2;
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Encryption failed: " + e.getMessage(), e);
        }
    }

    public byte[] decryptToBytes(BigInteger[] cipher) {
        if (cipher == null || cipher.length == 0) {
            throw new IllegalArgumentException("Cipher cannot be null or empty");
        }
        if (a == null || p == null) {
            throw new IllegalStateException("Keys not initialized. Please generate keys first.");
        }

        try {
            int chunks = cipher.length / 2;
            int chunkSize = (p.bitLength() - 1) / 8;
            byte[] result = new byte[chunks * chunkSize];
            int resultIndex = 0;
            
            for (int i = 0; i < chunks; i++) {
                BigInteger c1 = cipher[i];
                BigInteger c2 = cipher[chunks + i];
                
                // Calculate m = c2 * (c1^a)^(-1) mod p
                BigInteger s = c1.modPow(a, p);
                BigInteger sInv = s.modInverse(p);
                BigInteger decrypted = c2.multiply(sInv).mod(p);
                
                // Convert BigInteger to bytes
                byte[] chunkBytes = decrypted.toByteArray();
                // Remove sign byte if present
                int startIndex = (chunkBytes.length > chunkSize) ? 1 : 0;
                int length = chunkBytes.length - startIndex;
                
                // Pad with leading zeros if necessary
                if (length < chunkSize) {
                    int pad = chunkSize - length;
                    for (int j = 0; j < pad; j++) {
                        result[resultIndex++] = 0;
                    }
                    System.arraycopy(chunkBytes, startIndex, result, resultIndex, length);
                    resultIndex += length;
                } else {
                    System.arraycopy(chunkBytes, startIndex, result, resultIndex, chunkSize);
                    resultIndex += chunkSize;
                }
            }
            
            // If the original file size is known, trim the result to that size when writing to file.
            // Otherwise, return the full result array.
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Decryption failed: " + e.getMessage(), e);
        }
    }

    public String encryptFromStringToString(String message) {
        if (message == null || message.isEmpty()) {
            throw new IllegalArgumentException("Message cannot be null or empty");
        }
        try {
            // Pad message to chunk size
            int chunkSize = (p.bitLength() - 1) / 8;
            while (message.length() % chunkSize != 0) {
                message += ' ';
            }
            
            byte[] messageBytes = message.getBytes();
            BigInteger[] encrypted = encrypt(messageBytes);
            StringBuilder result = new StringBuilder();
            for (BigInteger bi : encrypted) {
                result.append(bi.toString(16)).append("\n");
            }
            return result.toString();
        } catch (Exception e) {
            throw new RuntimeException("String encryption failed: " + e.getMessage(), e);
        }
    }

    public String decryptFromStringToString(String hexInput) {
        if (hexInput == null || hexInput.trim().isEmpty()) {
            throw new IllegalArgumentException("Input cannot be null or empty");
        }
        try {
            String[] hexValues = hexInput.trim().split("\n");
            BigInteger[] cipher = new BigInteger[hexValues.length];
            for (int i = 0; i < hexValues.length; i++) {
                cipher[i] = new BigInteger(hexValues[i], 16);
            }
            
            byte[] decryptedBytes = decryptToBytes(cipher);
            return new String(decryptedBytes).trim(); // Remove padding spaces
        } catch (Exception e) {
            throw new RuntimeException("String decryption failed: " + e.getMessage(), e);
        }
    }

    // Getters for key values
    public BigInteger getG() { return g; }
    public BigInteger getH() { return h; }
    public BigInteger getA() { return a; }
    public BigInteger getN() { return p; }
} 