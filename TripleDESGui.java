import java.awt.*;
import java.security.SecureRandom;
import javax.swing.*;

public class TripleDESGui extends JFrame {
    private TripleDESStandalone tripleDES;
    private JTextField key1Field, key2Field, key3Field;
    private JTextArea inputArea, outputArea;
    private JButton generateKeysButton, encryptButton, decryptButton;
    private JLabel statusLabel;

    public TripleDESGui() {
        tripleDES = new TripleDESStandalone();
        initializeGUI();
    }

    private void initializeGUI() {
        // Basic frame setup
        setTitle("TripleDES Encryption/Decryption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Initialize components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Keys section
        JPanel keysPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        keysPanel.setBorder(BorderFactory.createTitledBorder("Keys"));
        
        key1Field = new JTextField(16);
        key2Field = new JTextField(16);
        key3Field = new JTextField(16);
        
        keysPanel.add(new JLabel("Key 1:"));
        keysPanel.add(key1Field);
        keysPanel.add(new JLabel("Key 2:"));
        keysPanel.add(key2Field);
        keysPanel.add(new JLabel("Key 3:"));
        keysPanel.add(key3Field);
        
        // Generate Keys button
        generateKeysButton = new JButton("Generate Keys");
        JPanel genKeyPanel = new JPanel();
        genKeyPanel.add(generateKeysButton);

        // Text areas
        JPanel textAreasPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        
        inputArea = new JTextArea(5, 40);
        outputArea = new JTextArea(5, 40);
        outputArea.setEditable(false);
        
        // Set larger font for text areas
        Font textAreaFont = new Font("Dialog", Font.PLAIN, 14);
        inputArea.setFont(textAreaFont);
        outputArea.setFont(textAreaFont);
        
        JPanel inputPanel = new JPanel();
        inputPanel.setBorder(BorderFactory.createTitledBorder("Input Text"));
        inputPanel.add(new JScrollPane(inputArea));
        
        JPanel outputPanel = new JPanel();
        outputPanel.setBorder(BorderFactory.createTitledBorder("Output Text"));
        outputPanel.add(new JScrollPane(outputArea));
        
        textAreasPanel.add(inputPanel);
        textAreasPanel.add(outputPanel);

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout());
        encryptButton = new JButton("Encrypt");
        decryptButton = new JButton("Decrypt");
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        // Status
        statusLabel = new JLabel("Ready");
        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);

        // Add all components
        mainPanel.add(keysPanel);
        mainPanel.add(genKeyPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(textAreasPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(statusPanel);

        // Add action listeners
        generateKeysButton.addActionListener(e -> generateKeys());
        encryptButton.addActionListener(e -> encrypt());
        decryptButton.addActionListener(e -> decrypt());

        // Add main panel to frame
        add(mainPanel);

        // Set frame properties
        setSize(500, 600);
        setLocationRelativeTo(null);
    }

    private void generateKeys() {
        try {
            SecureRandom random = new SecureRandom();
            byte[] key1Bytes = new byte[8];
            byte[] key2Bytes = new byte[8];
            byte[] key3Bytes = new byte[8];
            
            random.nextBytes(key1Bytes);
            random.nextBytes(key2Bytes);
            random.nextBytes(key3Bytes);
            
            String key1 = bytesToHex(key1Bytes);
            String key2 = bytesToHex(key2Bytes);
            String key3 = bytesToHex(key3Bytes);

            key1Field.setText(key1);
            key2Field.setText(key2);
            key3Field.setText(key3);

            tripleDES.setKeys(key1, key2, key3);
            statusLabel.setText("Keys generated successfully");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void encrypt() {
        try {
            // Get current keys from text fields
            String key1 = key1Field.getText();
            String key2 = key2Field.getText();
            String key3 = key3Field.getText();
            
            // Update keys in the tripleDES instance
            tripleDES.setKeys(key1, key2, key3);
            
            String input = inputArea.getText();
            if (input.isEmpty()) {
                statusLabel.setText("Please enter text to encrypt");
                return;
            }

            byte[] encrypted = tripleDES.encrypt(input.getBytes());
            outputArea.setText(bytesToHex(encrypted));
            statusLabel.setText("Text encrypted successfully");
        } catch (Exception e) {
            statusLabel.setText("Encryption error: " + e.getMessage());
        }
    }

    private void decrypt() {
        try {
            // Get current keys from text fields
            String key1 = key1Field.getText();
            String key2 = key2Field.getText();
            String key3 = key3Field.getText();
            
            // Update keys in the tripleDES instance
            tripleDES.setKeys(key1, key2, key3);
            
            String hexInput = inputArea.getText().trim().replaceAll("\\s", "");
            if (hexInput.isEmpty()) {
                statusLabel.setText("Please enter hex text to decrypt");
                return;
            }

            byte[] decrypted = tripleDES.decrypt(hexToBytes(hexInput));
            outputArea.setText(new String(decrypted));
            statusLabel.setText("Text decrypted successfully");
        } catch (Exception e) {
            statusLabel.setText("Decryption error: " + e.getMessage());
        }
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
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            TripleDESGui gui = new TripleDESGui();
            gui.setVisible(true);
        });
    }
}