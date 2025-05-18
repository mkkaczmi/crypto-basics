import java.awt.*;
import java.io.*;
import java.nio.file.*;
import javax.swing.*;
import java.math.BigInteger;

public class ElGamalGui extends JFrame {
    private ElGamalStandalone elGamal;
    private JTextField gField, hField, aField, nField;
    private JTextArea inputArea, outputArea;
    private JButton generateKeysButton, encryptButton, decryptButton;
    private JButton encryptFileButton, decryptFileButton;
    private JLabel statusLabel;
    private JComboBox<String> operationMode;

    public ElGamalGui() {
        elGamal = new ElGamalStandalone();
        initializeGUI();
    }

    private void initializeGUI() {
        // Basic frame setup
        setTitle("ElGamal Encryption/Decryption");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        // Initialize components
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Keys section
        JPanel keysPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        keysPanel.setBorder(BorderFactory.createTitledBorder("Keys"));
        
        gField = new JTextField(32);
        hField = new JTextField(32);
        aField = new JTextField(32);
        nField = new JTextField(32);
        
        keysPanel.add(new JLabel("Public Key (g):"));
        keysPanel.add(gField);
        keysPanel.add(new JLabel("Public Key (h):"));
        keysPanel.add(hField);
        keysPanel.add(new JLabel("Private Key (a):"));
        keysPanel.add(aField);
        keysPanel.add(new JLabel("Modulus (N):"));
        keysPanel.add(nField);
        
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
        setSize(800, 700);
        setLocationRelativeTo(null);
        
        // Initial mode update
        updateMode();
        
        // Initialize with current keys
        updateKeyFields();
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

    private void updateKeyFields() {
        gField.setText(elGamal.getG().toString(16));
        hField.setText(elGamal.getH().toString(16));
        aField.setText(elGamal.getA().toString(16));
        nField.setText(elGamal.getN().toString(16));
    }

    private void generateKeys() {
        try {
            elGamal.generateKey();
            updateKeyFields();
            statusLabel.setText("Keys generated successfully");
        } catch (Exception e) {
            statusLabel.setText("Error: " + e.getMessage());
        }
    }

    private void encrypt() {
        try {
            String input = inputArea.getText();
            if (input.isEmpty()) {
                statusLabel.setText("Please enter text to encrypt");
                return;
            }

            String encrypted = elGamal.encryptFromStringToString(input);
            outputArea.setText(encrypted);
            statusLabel.setText("Text encrypted successfully");
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Input error: " + e.getMessage());
        } catch (IllegalStateException e) {
            statusLabel.setText("Key error: " + e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Encryption error: " + e.getMessage());
            e.printStackTrace(); // This will help with debugging
        }
    }

    private void decrypt() {
        try {
            String hexInput = inputArea.getText().trim();
            if (hexInput.isEmpty()) {
                statusLabel.setText("Please enter hex text to decrypt");
                return;
            }

            String decrypted = elGamal.decryptFromStringToString(hexInput);
            outputArea.setText(decrypted);
            statusLabel.setText("Text decrypted successfully");
        } catch (IllegalArgumentException e) {
            statusLabel.setText("Input error: " + e.getMessage());
        } catch (IllegalStateException e) {
            statusLabel.setText("Key error: " + e.getMessage());
        } catch (Exception e) {
            statusLabel.setText("Decryption error: " + e.getMessage());
            e.printStackTrace(); // This will help with debugging
        }
    }

    private void encryptFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File inputFile = fileChooser.getSelectedFile();
                String originalExtension = getFileExtension(inputFile);
                
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

                    byte[] fileContent = ElGamalStandalone.readFromFile(inputFile.getPath());
                    BigInteger[] encrypted = elGamal.encrypt(fileContent);
                    
                    // Write encrypted data with original extension
                    ElGamalStandalone.writeBigIntArrayToFileWithExtension(encrypted, outputFile.getPath(), originalExtension);
                    
                    statusLabel.setText("File encrypted to: " + outputFile.getName());
                }
            }
        } catch (Exception e) {
            statusLabel.setText("File encryption error: " + e.getMessage());
        }
    }

    private void decryptFile() {
        try {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                File inputFile = fileChooser.getSelectedFile();
                
                // Read encrypted data and original extension
                Object[] encryptedData = ElGamalStandalone.readBigIntArrayAndExtensionFromFile(inputFile.getPath());
                String originalExtension = (String) encryptedData[0];
                BigInteger[] encrypted = (BigInteger[]) encryptedData[1];
                
                // Decrypt to bytes
                byte[] decrypted = elGamal.decryptToBytes(encrypted);
                
                JFileChooser outputFileChooser = new JFileChooser();
                outputFileChooser.setDialogTitle("Save Decrypted File");
                
                // Create default filename with original extension
                String defaultName = inputFile.getName();
                if (defaultName.endsWith(".bin")) {
                    defaultName = defaultName.substring(0, defaultName.length() - 4);
                }
                
                // Add _decrypted before the extension
                if (!originalExtension.isEmpty() && !defaultName.toLowerCase().endsWith("." + originalExtension.toLowerCase())) {
                    defaultName = defaultName + "_decrypted." + originalExtension;
                } else {
                    // If extension is already present, insert _decrypted before it
                    int lastDot = defaultName.lastIndexOf('.');
                    if (lastDot != -1) {
                        defaultName = defaultName.substring(0, lastDot) + "_decrypted" + defaultName.substring(lastDot);
                    } else {
                        defaultName = defaultName + "_decrypted";
                    }
                }
                
                outputFileChooser.setSelectedFile(new File(defaultName));
                
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
                    
                    ElGamalStandalone.writeToFile(decrypted, outputFile.getPath());
                    statusLabel.setText("File decrypted to: " + outputFile.getName());
                }
            }
        } catch (Exception e) {
            statusLabel.setText("File decryption error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot == -1 || lastDot == name.length() - 1) {
            return "";
        }
        return name.substring(lastDot + 1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            ElGamalGui gui = new ElGamalGui();
            gui.setVisible(true);
        });
    }
} 