import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import java.awt.FileDialog;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.File;
import java.nio.file.Path;

public class RSAFrame extends JFrame {
    private final RSAService rsaService = new RSAService();

    public RSAFrame() {
        setTitle("ЛР №2 Криптография - RSA (вариант 1)");
        setSize(900, 650);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Шифрование RSA", buildEncryptPanel());
        tabs.addTab("Расшифрование RSA", buildDecryptPanel());
        // tabs.addTab("Алгоритмы для отчета", buildShowcasePanel());
        add(tabs);
    }

    private JPanel buildEncryptPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(0, 3, 8, 8));

        JTextField pField = new JTextField();
        JTextField qField = new JTextField();
        JTextField dField = new JTextField();
        JTextField inField = new JTextField();
        JTextField outField = new JTextField();
        inField.setEditable(false);
        outField.setEditable(false);

        JButton inBtn = new JButton("Выбрать файл");
        inBtn.addActionListener(e -> chooseFile(inField, true));
        JButton outBtn = new JButton("Куда сохранить");
        outBtn.addActionListener(e -> chooseFile(outField, false));

        form.add(new JLabel("p (простое):"));
        form.add(pField);
        form.add(new JLabel(""));
        form.add(new JLabel("q (простое):"));
        form.add(qField);
        form.add(new JLabel(""));
        form.add(new JLabel("Kc (закрытая экспонента d):"));
        form.add(dField);
        form.add(new JLabel(""));
        form.add(new JLabel("Входной файл:"));
        form.add(inField);
        form.add(inBtn);
        form.add(new JLabel("Выходной шифро-файл:"));
        form.add(outField);
        form.add(outBtn);

        JTextArea resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        JScrollPane scroll = new JScrollPane(resultArea);

        JButton run = new JButton("Шифровать");
        run.addActionListener(e -> {
            try {
                long p = Long.parseLong(pField.getText().trim());
                long q = Long.parseLong(qField.getText().trim());
                long d = Long.parseLong(dField.getText().trim());
                Path input = Path.of(inField.getText().trim());
                Path output = Path.of(outField.getText().trim());

                RSAService.EncryptionResult result = rsaService.encryptFile(input, output, p, q, d);

                StringBuilder sb = new StringBuilder();
                sb.append("Открытый ключ Ko = (e, r): (")
                        .append(result.e()).append(", ").append(result.r()).append(")\n");
                sb.append("Зашифровано байт: ").append(result.blockCount()).append("\n");
                sb.append("Содержимое шифро-файла в 10-й СС:\n");
                for (int i = 0; i < result.encryptedBlocks().size(); i++) {
                    sb.append(result.encryptedBlocks().get(i));
                    if (i != result.encryptedBlocks().size() - 1) {
                        sb.append(' ');
                    }
                }
                resultArea.setText(sb.toString());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(scroll, BorderLayout.CENTER);
        panel.add(run, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildDecryptPanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JPanel form = new JPanel(new GridLayout(0, 3, 8, 8));

        JTextField rField = new JTextField();
        JTextField dField = new JTextField();
        JTextField inField = new JTextField();
        JTextField outField = new JTextField();
        inField.setEditable(false);
        outField.setEditable(false);

        JButton inBtn = new JButton("Выбрать шифро-файл");
        inBtn.addActionListener(e -> chooseFile(inField, true));
        JButton outBtn = new JButton("Куда сохранить");
        outBtn.addActionListener(e -> chooseFile(outField, false));

        form.add(new JLabel("r (модуль):"));
        form.add(rField);
        form.add(new JLabel(""));
        form.add(new JLabel("Kc (закрытая экспонента d):"));
        form.add(dField);
        form.add(new JLabel(""));
        form.add(new JLabel("Входной шифро-файл:"));
        form.add(inField);
        form.add(inBtn);
        form.add(new JLabel("Выходной файл:"));
        form.add(outField);
        form.add(outBtn);

        JTextArea resultArea = new JTextArea();
        resultArea.setLineWrap(true);
        resultArea.setWrapStyleWord(true);
        resultArea.setEditable(false);

        JButton run = new JButton("Расшифровать");
        run.addActionListener(e -> {
            try {
                long r = Long.parseLong(rField.getText().trim());
                long d = Long.parseLong(dField.getText().trim());
                Path input = Path.of(inField.getText().trim());
                Path output = Path.of(outField.getText().trim());

                /*int count = rsaService.decryptFile(input, output, r, d);
                resultArea.setText("Успешно расшифровано блоков: " + count + "\nФайл сохранен: " + output);*/
                RSAService.DecryptionResult result = rsaService.decryptFile(input, output, r, d);
                StringBuilder sb = new StringBuilder();
                sb.append("Успешно расшифровано блоков: ").append(result.blockCount()).append("\n");
                sb.append("Файл сохранен: ").append(output).append("\n\n");
                sb.append("Содержимое шифро-файла (16-битные блоки в 10-й СС:\n");
                for (int i = 0; i < result.encryptedBlocks().size(); i++) {
                    sb.append(result.encryptedBlocks().get(i));
                    if (i != result.encryptedBlocks().size() - 1) {
                        sb.append(' ');
                    }
                }
                resultArea.setText(sb.toString());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        panel.add(form, BorderLayout.NORTH);
        panel.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        panel.add(run, BorderLayout.SOUTH);
        return panel;
    }

    private JPanel buildShowcasePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        JLabel label = new JLabel("Демонстрация 3 алгоритмов для отчета", SwingConstants.CENTER);
        JTextArea area = new JTextArea();
        area.setEditable(false);

        JButton run = new JButton("Сформировать");
        run.addActionListener(e -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                PrintStream ps = new PrintStream(baos);
                PrintStream oldOut = System.out;
                try {
                    System.setOut(ps);
                    AlgorithmShowcase.printAll();
                } finally {
                    System.setOut(oldOut);
                    ps.close();
                }
                area.setText(baos.toString());
            } catch (Exception ex) {
                showError(ex.getMessage());
            }
        });

        panel.add(label, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        panel.add(run, BorderLayout.SOUTH);
        return panel;
    }

    private void chooseFile(JTextField targetField, boolean openMode) {
        FileDialog dialog = new FileDialog(
                this,
                openMode ? "Выбор файла" : "Сохранить файл",
                openMode ? FileDialog.LOAD : FileDialog.SAVE
        );
        dialog.setVisible(true);

        String dir = dialog.getDirectory();
        String file = dialog.getFile();
        if (dir != null && file != null) {
            targetField.setText(new File(dir, file).getAbsolutePath());
        }
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
