import java.util.Scanner;

public class TripleDESStandalone {
    private String key1, key2, key3;
    private static final int[] PC1 = {
        57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34, 26, 18,
        10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36,
        63, 55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22,
        14, 6, 61, 53, 45, 37, 29, 21, 13, 5, 28, 20, 12, 4
    };
    
    private static final int[] PC2 = {
        14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10, 23, 19, 12, 4,
        26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55, 30, 40,
        51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
    };
    
    private static final int[] IP = {
        58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36, 28, 20, 12, 4,
        62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32, 24, 16, 8,
        57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19, 11, 3,
        61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7
    };
    
    private static final int[] IP_INV = {
        40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47, 15, 55, 23, 63, 31,
        38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13, 53, 21, 61, 29,
        36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51, 19, 59, 27,
        34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17, 57, 25
    };
    
    private static final int[] E = {
        32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8, 9, 10, 11,
        12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21, 20, 21,
        22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1
    };
    
    private static final int[] P = {
        16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10,
        2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25
    };
    
    private static final int[][] S_BOXES = {
        // S1
        {
            14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
            0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
            4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
            15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
        },
        // S2
        {
            15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
            3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
            0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
            13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
        },
        // S3
        {
            10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
            13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
            13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
            1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
        },
        // S4
        {
            7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
            13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
            10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
            3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
        },
        // S5
        {
            2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
            14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
            4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
            11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
        },
        // S6
        {
            12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
            10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
            9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
            4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
        },
        // S7
        {
            4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
            13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
            1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
            6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
        },
        // S8
        {
            13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
            1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
            7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
            2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
        }
    };
    
    private static final int[] SHIFTS = {1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1};
    
    public TripleDESStandalone() {
        this.key1 = null;
        this.key2 = null;
        this.key3 = null;
    }
    
    public void setKeys(String key1, String key2, String key3) {
        if (key1.length() != 16 || key2.length() != 16 || key3.length() != 16) {
            throw new IllegalArgumentException("Keys must be 16 characters long");
        }
        this.key1 = key1;
        this.key2 = key2;
        this.key3 = key3;
    }
    
    public byte[] encrypt(byte[] message) {
        if (key1 == null || key2 == null || key3 == null) {
            throw new IllegalStateException("Keys not set");
        }
        
        // Triple DES encryption: E(K3, D(K2, E(K1, M)))
        byte[] step1 = desEncrypt(message, key1);
        byte[] step2 = desDecrypt(step1, key2);
        return desEncrypt(step2, key3);
    }
    
    public byte[] decrypt(byte[] cipher) {
        if (key1 == null || key2 == null || key3 == null) {
            throw new IllegalStateException("Keys not set");
        }
        
        // Triple DES decryption: D(K1, E(K2, D(K3, C)))
        byte[] step1 = desDecrypt(cipher, key3);
        byte[] step2 = desEncrypt(step1, key2);
        return desDecrypt(step2, key1);
    }
    
    private byte[] desEncrypt(byte[] message, String key) {
        // Convert hex key string to binary
        long keyBits = Long.parseUnsignedLong(key, 16);
        
        // Generate round keys
        long[] roundKeys = generateRoundKeys(keyBits);
        
        // Pad message to multiple of 8 bytes
        int paddedLength = ((message.length + 7) / 8) * 8;
        byte[] paddedMessage = new byte[paddedLength];
        System.arraycopy(message, 0, paddedMessage, 0, message.length);
        
        // Process each 64-bit block
        byte[] result = new byte[paddedLength];
        for (int i = 0; i < paddedLength; i += 8) {
            // Convert current block to bits
            long block = 0;
            for (int j = 0; j < 8; j++) {
                block = (block << 8) | (paddedMessage[i + j] & 0xFFL);
            }
            
            // Process block
            block = processBlock(block, roundKeys, false);
            
            // Convert block back to bytes
            for (int j = 7; j >= 0; j--) {
                result[i + j] = (byte)(block & 0xFFL);
                block >>= 8;
            }
        }
        return result;
    }
    
    private byte[] desDecrypt(byte[] cipher, String key) {
        // Convert hex key string to binary
        long keyBits = Long.parseUnsignedLong(key, 16);
        
        // Generate round keys
        long[] roundKeys = generateRoundKeys(keyBits);
        
        byte[] result = new byte[cipher.length];
        for (int i = 0; i < cipher.length; i += 8) {
            // Convert current block to bits
            long block = 0;
            for (int j = 0; j < 8; j++) {
                block = (block << 8) | (cipher[i + j] & 0xFFL);
            }
            
            // Process block
            block = processBlock(block, roundKeys, true);
            
            // Convert block back to bytes
            for (int j = 7; j >= 0; j--) {
                result[i + j] = (byte)(block & 0xFFL);
                block >>= 8;
            }
        }
        return result;
    }
    
    private long processBlock(long block, long[] roundKeys, boolean decrypt) {
        // Initial permutation
        block = permute(block, IP, 64);
        
        // Split block into left and right halves
        int left = (int)(block >> 32);
        int right = (int)(block & 0xFFFFFFFFL);
        
        // 16 rounds of Feistel network
        for (int round = 0; round < 16; round++) {
            int temp = left;
            int roundKey = decrypt ? 15 - round : round;
            left = right;
            right = temp ^ feistel(right, roundKeys[roundKey]);
        }
        
        // Combine halves (swap left and right for final round)
        block = ((long)right << 32) | (left & 0xFFFFFFFFL);
        
        // Final permutation
        return permute(block, IP_INV, 64);
    }
    
    private int feistel(int right, long roundKey) {
        // Expansion
        long expanded = permute(right & 0xFFFFFFFFL, E, 48);
        
        // XOR with round key
        expanded ^= roundKey;
        
        // S-box substitution
        int result = 0;
        for (int i = 0; i < 8; i++) {
            int chunk = (int)((expanded >> (42 - i * 6)) & 0x3F);
            int row = ((chunk & 0x20) >> 4) | (chunk & 0x01);
            int col = (chunk >> 1) & 0x0F;
            result = (result << 4) | S_BOXES[i][row * 16 + col];
        }
        
        // P-box permutation
        return (int)permute(result & 0xFFFFFFFFL, P, 32);
    }
    
    private long permute(long input, int[] table, int resultSize) {
        long result = 0;
        for (int i = 0; i < table.length; i++) {
            int bit = table[i] - 1;
            result = (result << 1) | ((input >> (resultSize - 1 - bit)) & 1);
        }
        return result;
    }
    
    private int rotateLeft(int value, int shift, int size) {
        return ((value << shift) | (value >>> (size - shift))) & ((1 << size) - 1);
    }
    
    private static String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02X", b));
        }
        return result.toString();
    }
    
    private static byte[] hexToBytes(String hex) {
        int len = hex.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(hex.charAt(i), 16) << 4)
                    + Character.digit(hex.charAt(i+1), 16));
        }
        return data;
    }
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        TripleDESStandalone tripleDES = new TripleDESStandalone();
        
        while (true) {
            System.out.println("\nTripleDES Encryption/Decryption Menu:");
            System.out.println("1. Generate new keys");
            System.out.println("2. Encrypt text");
            System.out.println("3. Decrypt text");
            System.out.println("4. Exit");
            System.out.print("Enter your choice (1-4): ");
            
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline
            
            switch (choice) {
                case 1:
                    generateKeys(tripleDES);
                    break;
                case 2:
                    encryptText(tripleDES, scanner);
                    break;
                case 3:
                    decryptText(tripleDES, scanner);
                    break;
                case 4:
                    System.out.println("Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }
    
    private static void generateKeys(TripleDESStandalone tripleDES) {
        // Generate three 16-character hex keys
        String key1 = "0123456789ABCDEF";
        String key2 = "1133557799BBDDFF";
        String key3 = "0022446688AACCEE";
        
        tripleDES.setKeys(key1, key2, key3);
        
        System.out.println("\nGenerated Keys:");
        System.out.println("Key 1: " + key1);
        System.out.println("Key 2: " + key2);
        System.out.println("Key 3: " + key3);
    }
    
    private static void encryptText(TripleDESStandalone tripleDES, Scanner scanner) {
        try {
            System.out.print("Enter text to encrypt: ");
            String text = scanner.nextLine();
            
            byte[] message = text.getBytes();
            byte[] encrypted = tripleDES.encrypt(message);
            
            System.out.println("\nEncrypted text (hex):");
            System.out.println(bytesToHex(encrypted));
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void decryptText(TripleDESStandalone tripleDES, Scanner scanner) {
        try {
            System.out.print("Enter hex text to decrypt: ");
            String hexText = scanner.nextLine();
            
            byte[] encrypted = hexToBytes(hexText);
            byte[] decrypted = tripleDES.decrypt(encrypted);
            
            System.out.println("\nDecrypted text:");
            System.out.println(new String(decrypted));
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private long[] generateRoundKeys(long key) {
        long[] roundKeys = new long[16];
        
        // Apply PC1 permutation to key
        key = permute(key, PC1, 64);
        
        // Split into left and right halves (28 bits each)
        int c = (int) (key >> 28);
        int d = (int) (key & 0x0FFFFFFF);
        
        // Generate 16 round keys
        for (int i = 0; i < 16; i++) {
            // Apply shift schedule
            c = rotateLeft(c, SHIFTS[i], 28);
            d = rotateLeft(d, SHIFTS[i], 28);
            
            // Combine halves and apply PC2 permutation
            long cd = ((long) c << 28) | (long) d;
            roundKeys[i] = permute(cd, PC2, 56);
        }
        
        return roundKeys;
    }
} 