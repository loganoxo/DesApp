package com.logan;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.nio.charset.StandardCharsets;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.logan.component.RoundButton;

/**
 * swing启动类
 *
 * @author logan
 * @version 1.0
 */
public class DesApp extends JFrame {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            DesApp demo = new DesApp();
            demo.setVisible(true);
        });
    }

    /**
     * 包含组件的面板
     */
    private final JPanel panel;

    /**
     * 网格布局
     */
    private final GridBagConstraints constraints;

    /**
     * 字体
     */
    private final Font font = new Font("Arial", Font.PLAIN, 18);

    // 创建主窗口
    private DesApp() {
        this.setTitle("3DES加解密");
        // this.setSize(1800, 600); //调用pack()根据组件的大小自动调整窗口大小
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // 防止默认关闭操作
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });
        panel = new JPanel(new GridBagLayout()); // 创建包含组件的面板
        constraints = new GridBagConstraints(); // 网格布局
        constraints.insets = new Insets(5, 5, 5, 5);

        // 输入要加解密的内容
        JLabel contentLabel = createLabel("输入内容:"); // 创建标签
        addComponent(contentLabel, 0, 0, null, GridBagConstraints.WEST, false);
        JTextArea contentTextArea = createTextArea(4, 70); // 创建文本区域
        addComponent(new JScrollPane(contentTextArea), 1, 0, 3, GridBagConstraints.WEST, false);

        // 输入密钥和偏移量
        JLabel keyLabel = createLabel("密钥(24位):"); // 创建标签
        addComponent(keyLabel, 0, 1, null, GridBagConstraints.WEST, false);
        JPasswordField keyField = createPasswordField(30);
        // 输入字符数回显
        keyField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCount();
            }

            private void updateCount() {
                int count = keyField.getPassword().length;
                keyLabel.setText("密钥(" + count + "/24位):");
            }
        });
        addComponent(keyField, 1, 1, null, GridBagConstraints.WEST, false);

        JLabel ivLabel = createLabel("偏移量(8位):"); // 创建标签
        addComponent(ivLabel, 2, 1, null, GridBagConstraints.WEST, false);
        JPasswordField ivField = createPasswordField(30);
        // 输入字符数回显
        ivField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                updateCount();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateCount();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                updateCount();
            }

            private void updateCount() {
                int count = ivField.getPassword().length;
                ivLabel.setText("偏移量(" + count + "/8位):");
            }
        });
        addComponent(ivField, 3, 1, null, GridBagConstraints.WEST, false);

        // 创建加解密按钮
        JButton encodeButton = crecateButton("3DES加密", 20); // 创建自定义样式的按钮
        addComponent(encodeButton, 0, 2, null, GridBagConstraints.WEST, false);
        JButton decodeButton = crecateButton("3DES解密", 20); // 创建自定义样式的按钮
        addComponent(decodeButton, 1, 2, null, GridBagConstraints.WEST, false);

        // 创建返回结果
        JTextArea resultTextArea = createTextArea(10, 80); // 创建文本区域
        resultTextArea.setEditable(false); // 设置为不可编辑
        addComponent(new JScrollPane(resultTextArea), 0, 3, null, GridBagConstraints.CENTER, true);

        // 复制按钮
        JButton copyButton = crecateButton("复制结果", 20); // 创建自定义样式的按钮
        addComponent(copyButton, 0, 4, null, GridBagConstraints.CENTER, true);

        // 备注
        JLabel remarkLabel = createLabel("3DES加解密，使用 Cipher Block Chaining (CBC) 模式，并且采用 PKCS5 填充方式"); // 创建标签
        remarkLabel.setFont(font);
        addComponent(remarkLabel, 0, 5, null, GridBagConstraints.WEST, true);

        // 加密
        encodeButton.addActionListener(e -> {
            String content = contentTextArea.getText();
            String key = new String(keyField.getPassword());
            String iv = new String(ivField.getPassword());
            boolean flag = check(content, key, iv);

            if (flag) {
                try {
                    String result = DES3.encodeCBC(key.getBytes(StandardCharsets.UTF_8),
                        iv.getBytes(StandardCharsets.UTF_8), content.getBytes(StandardCharsets.UTF_8));
                    resultTextArea.setText(result); // 将连接结果显示在文本区域中
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "加密失败：" + ex.getMessage(), "抛出异常", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }

            }
        });

        // 解密
        decodeButton.addActionListener(e -> {
            String content = contentTextArea.getText();
            String key = new String(keyField.getPassword());
            String iv = new String(ivField.getPassword());
            boolean flag = check(content, key, iv);
            if (flag) {
                try {
                    String result = DES3.decodeCBC(key.getBytes(StandardCharsets.UTF_8),
                        iv.getBytes(StandardCharsets.UTF_8), content.getBytes(StandardCharsets.UTF_8));
                    resultTextArea.setText(result); // 将连接结果显示在文本区域中
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "解密失败：" + ex.getMessage(), "抛出异常", JOptionPane.ERROR_MESSAGE);
                    throw new RuntimeException(ex);
                }
            }
        });

        // 复制
        copyButton.addActionListener(e -> {
            String result = resultTextArea.getText();
            try {
                if (result == null || result.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "结果为空");
                } else {
                    StringSelection selection = new StringSelection(result);
                    Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                    clipboard.setContents(selection, null); // 复制结果到剪贴板
                    JOptionPane.showMessageDialog(this, "结果已复制到剪贴板。");
                }
            } catch (SecurityException ex) {
                JOptionPane.showMessageDialog(this, "复制到剪贴板失败：" + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            }
        });

        this.add(panel); // 将面板添加到主窗口
        this.pack(); // 根据组件的大小自动调整窗口大小
        // 获取屏幕尺寸
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        int screenWidth = (int) screenSize.getWidth();
        int screenHeight = (int) screenSize.getHeight();
        int windowWidth = this.getWidth();
        int windowHeight = this.getHeight();

        // 计算窗口位置
        int x = (screenWidth - windowWidth) / 2;
        int y = (screenHeight - windowHeight) / 2;

        // 设置窗口位置
        this.setLocation(x, y);
        this.setVisible(true); // 显示窗口
    }

    /**
     * 校验
     *
     * @param content 输入内容不能为空
     * @param key     密钥为24位
     * @param iv      偏移量为8位
     * @return 布尔值
     */
    private boolean check(String content, String key, String iv) {
        boolean flag = true;
        if (content == null || content.isEmpty()) {
            flag = false;
            JOptionPane.showMessageDialog(this, "内容不能为空", "校验错误", JOptionPane.ERROR_MESSAGE);
        } else if (key == null || key.isEmpty()) {
            flag = false;
            JOptionPane.showMessageDialog(this, "密钥不能为空", "校验错误", JOptionPane.ERROR_MESSAGE);
        } else if (iv == null || iv.isEmpty()) {
            flag = false;
            JOptionPane.showMessageDialog(this, "偏移量不能为空", "校验错误", JOptionPane.ERROR_MESSAGE);
        } else if (key.length() != 24 || iv.length() != 8) {
            JOptionPane.showMessageDialog(this, "密钥或偏移量长度不对", "校验错误", JOptionPane.ERROR_MESSAGE);
            flag = false;
        }
        return flag;
    }

    // 将组件添加到面板的辅助方法
    private void addComponent(JComponent component, int x, int y, Integer gridwidth, Integer anchor, boolean allLine) {
        constraints.gridwidth = 1;
        constraints.gridx = x;
        constraints.gridy = y;
        constraints.anchor = GridBagConstraints.CENTER; // 居中对齐
        if (gridwidth != null) {
            constraints.gridwidth = gridwidth;
        }
        if (anchor != null) {
            constraints.anchor = anchor; // 对齐
        }
        if (allLine) {
            constraints.gridwidth = GridBagConstraints.REMAINDER; // REMAINDER 表示横跨整行
        }

        panel.add(component, constraints);
    }

    // 创建标签
    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text); // 创建标签
        label.setFont(font); // 设置字体
        label.setForeground(Color.BLACK); // 设置文本颜色
        label.setBackground(Color.WHITE); // 设置背景颜色
        // label.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 设置边框
        return label;
    }

    // 创建文本输入框的辅助方法
    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(font); // 设置字体
        passwordField.setForeground(Color.BLACK); // 设置文本颜色
        passwordField.setBackground(Color.WHITE); // 设置背景颜色
        passwordField.setCaretColor(Color.BLACK); // 设置光标颜色
        passwordField.setMargin(new Insets(10, 10, 10, 10)); // 设置边距
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 设置边框
        return passwordField;
    }

    // 创建文本区域的辅助方法
    private JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(new Font("Arial", Font.PLAIN, 18)); // 设置字体
        textArea.setForeground(Color.BLACK); // 设置文本颜色
        textArea.setBackground(Color.WHITE); // 设置背景颜色
        textArea.setLineWrap(true); // 启用自动换行
        textArea.setCaretColor(Color.BLACK); // 设置光标颜色
        textArea.setMargin(new Insets(10, 10, 10, 10)); // 设置边距
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 设置边框
        return textArea;
    }

    // 创建带有自定义样式的按钮的辅助方法
    private JButton crecateButton(String text, int arc) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 128, 128));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setUI(new RoundButton(arc));
        return button;
    }

    private void handleWindowClosing() {
        int option = JOptionPane.showConfirmDialog(this, "确定要退出吗？", "退出", JOptionPane.YES_NO_OPTION);
        if (option == JOptionPane.YES_OPTION) {
            this.dispose();
        }
    }
}
