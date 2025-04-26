import java.awt.*;
import java.security.SecureRandom;
import javax.swing.*;
import java.io.*;
import java.nio.file.*;

public class TripleDESGui extends JFrame {
    private TripleDESStandalone tripleDES;
    private JTextField key1Field, key2Field, key3Field;
    private JTextArea inputArea, outputArea;
    private JButton generateKeysButton, encryptButton, decryptButton;
    private JButton encryptFileButton, decryptFileButton;
    private JLabel statusLabel;
    private JComboBox<String> operationMode;

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

        // Operation mode selector
        String[] modes = {"Text Mode", "File Mode"};
        operationMode = new JComboBox<>(modes);
        JPanel modePanel = new JPanel();
        modePanel.add(new JLabel("Operation Mode: "));
        modePanel.add(operationMode);

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
        encryptButton = new JButton("Encrypt Text");
        decryptButton = new JButton("Decrypt Text");
        encryptFileButton = new JButton("Encrypt File");
        decryptFileButton = new JButton("Decrypt File");
        
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);
        buttonPanel.add(encryptFileButton);
        buttonPanel.add(decryptFileButton);

        // Status
        statusLabel = new JLabel("Ready");
        JPanel statusPanel = new JPanel();
        statusPanel.add(statusLabel);

        // Add all components
        mainPanel.add(keysPanel);
        mainPanel.add(genKeyPanel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        mainPanel.add(modePanel);
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
        encryptFileButton.addActionListener(e -> encryptFile());
        decryptFileButton.addActionListener(e -> decryptFile());
        operationMode.addActionListener(e -> updateMode());

        // Add main panel to frame
        add(mainPanel);

        // Set frame properties
        setSize(600, 700);
        setLocationRelativeTo(null);
        
        // Initial mode update
        updateMode();
    }

    private void updateMode() {
        boolean isTextMode = operationMode.getSelectedIndex() == 0;
        inputArea.setEnabled(isTextMode);
        outputArea.setEnabled(isTextMode);
        encryptButton.setEnabled(isTextMode);
        decryptButton.setEnabled(isTextMode);
        encryptFileButton.setEnabled(!isTextMode);
        decryptFileButton.setEnabled(!isTextMode);
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
            updateKeys();
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
            updateKeys();
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

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1 || lastDot == name.length() - 1) {
            return ""; // brak rozszerzenia
        }
        return name.substring(lastDot + 1);
    }
    
    private void encryptFile() {
        try {
            updateKeys();
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File inputFile = fileChooser.getSelectedFile();
                String originalExtension = getFileExtension(inputFile);
                if (originalExtension == null) {
                    originalExtension = ""; // fallback
                }
    
                // Create a new file chooser for the output file
                JFileChooser outputFileChooser = new JFileChooser();
                outputFileChooser.setDialogTitle("Save Encrypted File");
                outputFileChooser.setSelectedFile(new File(inputFile.getName() + ".bin"));
                
                if (outputFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File outputFile = outputFileChooser.getSelectedFile();
    
                    if (outputFile.exists()) {
                        int response = JOptionPane.showConfirmDialog(this,
                                "Output file already exists. Overwrite?",
                                "File exists",
                                JOptionPane.YES_NO_OPTION);
                        if (response != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
    
                    byte[] fileContent = Files.readAllBytes(inputFile.toPath());
    
                    // Add extension header (e.g., "pdf\0" + encrypted content)
                    byte[] extensionBytes = originalExtension.getBytes();
                    byte[] extensionLength = new byte[]{(byte) extensionBytes.length};
    
                    byte[] dataToEncrypt = new byte[1 + extensionBytes.length + fileContent.length];
                    dataToEncrypt[0] = extensionLength[0];
                    System.arraycopy(extensionBytes, 0, dataToEncrypt, 1, extensionBytes.length);
                    System.arraycopy(fileContent, 0, dataToEncrypt, 1 + extensionBytes.length, fileContent.length);
    
                    byte[] encrypted = tripleDES.encrypt(dataToEncrypt);
    
                    Files.write(outputFile.toPath(), encrypted);
                    statusLabel.setText("File encrypted to: " + outputFile.getName());
                }
            }
        } catch (Exception e) {
            statusLabel.setText("File encryption error: " + e.getMessage());
        }
    }

    private void decryptFile() {
        try {
            updateKeys();
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File inputFile = fileChooser.getSelectedFile();
    
                byte[] encryptedContent = Files.readAllBytes(inputFile.toPath());
                byte[] decrypted = tripleDES.decrypt(encryptedContent);
    
                int extensionLength = decrypted[0];
                String extension = new String(decrypted, 1, extensionLength);
                byte[] actualFileData = new byte[decrypted.length - 1 - extensionLength];
                System.arraycopy(decrypted, 1 + extensionLength, actualFileData, 0, actualFileData.length);
    
                String baseName = inputFile.getName();
                if (baseName.toLowerCase().endsWith(".bin")) {
                    baseName = baseName.substring(0, baseName.length() - 4);
                }
    
                // Create a new file chooser for the output file
                JFileChooser outputFileChooser = new JFileChooser();
                outputFileChooser.setDialogTitle("Save Decrypted File");
                outputFileChooser.setSelectedFile(new File(baseName + "_decrypted." + extension));
                
                if (outputFileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                    File outputFile = outputFileChooser.getSelectedFile();
    
                    if (outputFile.exists()) {
                        int response = JOptionPane.showConfirmDialog(this,
                                "Output file already exists. Overwrite?",
                                "File exists",
                                JOptionPane.YES_NO_OPTION);
                        if (response != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
    
                    Files.write(outputFile.toPath(), actualFileData);
                    statusLabel.setText("File decrypted to: " + outputFile.getName());
                }
            }
        } catch (Exception e) {
            statusLabel.setText("File decryption error: " + e.getMessage());
        }
    }
    

    private void updateKeys() {
        String key1 = key1Field.getText();
        String key2 = key2Field.getText();
        String key3 = key3Field.getText();
        tripleDES.setKeys(key1, key2, key3);
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