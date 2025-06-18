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

    private final Font font = new Font("Arial", Font.PLAIN, 18);

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            DesApp app = new DesApp();
            app.setVisible(true);
        });
    }

    private DesApp() {
        this.setTitle("3DES加解密");
        // this.setSize(1800, 600); //调用pack()根据组件的大小自动调整窗口大小
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);// 防止默认关闭操作
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                handleWindowClosing();
            }
        });

        // 输入文本框
        JPanel contentPanel = new JPanel(new BorderLayout());//边界布局
        JLabel contentLabel = createLabel("输入内容:");
        JTextArea contentTextArea = createTextArea(15, 80); // 输入框
        contentPanel.add(contentLabel, BorderLayout.NORTH);
        contentPanel.add(new JScrollPane(contentTextArea), BorderLayout.CENTER);


        // 输出文本框
        JPanel resultPanel = new JPanel(new BorderLayout());
        JLabel resultLabel = createLabel("输出内容:");
        JTextArea resultTextArea = createTextArea(15, 80); // 结果框
        resultTextArea.setEditable(false);
        resultPanel.add(resultLabel, BorderLayout.NORTH);
        resultPanel.add(new JScrollPane(resultTextArea), BorderLayout.CENTER);

        // 上半部分：两个文本框
        JSplitPane textAreaSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, contentPanel, resultPanel);
        textAreaSplit.setResizeWeight(0.5);    // 平等拉伸
        textAreaSplit.setContinuousLayout(true);

        // 密钥、偏移量、按钮区域
        JPanel formPanel = new JPanel(new GridBagLayout());// 网格布局
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.add(formPanel, BorderLayout.NORTH);
        formWrapper.setPreferredSize(new Dimension(0, 80));
        formWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));
        formWrapper.setMinimumSize(new Dimension(0, 80));

        JLabel keyLabel = createLabel("密钥(24位):");
        formPanel.add(keyLabel, createGbc(0, 0, 0, GridBagConstraints.NONE, 1, new Insets(0, 2, 0, 5)));
        JPasswordField keyField = createPasswordField(24);
        formPanel.add(keyField, createGbc(1, 0, 3, GridBagConstraints.HORIZONTAL, 1, new Insets(0, 0, 0, 30)));
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
                keyLabel.setText("密钥(" + keyField.getPassword().length + "/24位):");
            }
        });

        JLabel ivLabel = createLabel("偏移量(8位):");
        formPanel.add(ivLabel, createGbc(2, 0, 0, GridBagConstraints.NONE, 1, new Insets(0, 0, 0, 5)));
        JPasswordField ivField = createPasswordField(8);
        formPanel.add(ivField, createGbc(3, 0, 1, GridBagConstraints.HORIZONTAL, 1, new Insets(0, 0, 0, 0)));
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
                ivLabel.setText("偏移量(" + ivField.getPassword().length + "/8位):");
            }
        });

        // 创建按钮
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        JButton encodeButton = crecateButton("3DES加密", 20);
        JButton decodeButton = crecateButton("3DES解密", 20);
        JButton copyButton = crecateButton("复制结果", 20);
        JLabel remarkLabel = createLabel("3DES加解密，使用 CBC 模式 + PKCS5 填充");
        remarkLabel.setFont(font);
        buttonPanel.add(encodeButton);
        buttonPanel.add(decodeButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(remarkLabel);
        formPanel.add(buttonPanel, createGbc(0, 1, 0, GridBagConstraints.NONE, GridBagConstraints.REMAINDER, new Insets(0, 0, 0, 0)));

        // 主区域
        JSplitPane mainSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, textAreaSplit, formPanel);
        mainSplit.setResizeWeight(1);
        mainSplit.setContinuousLayout(true);

        // 将面板添加到主窗口
        this.setContentPane(mainSplit);
        // 根据组件的大小自动调整窗口大小
        this.pack();
        //将当前窗口置于屏幕中央
        this.setLocationRelativeTo(null);
        // 显示窗口
        this.setVisible(true);

        // 加密按钮事件
        encodeButton.addActionListener(e -> {
            String content = contentTextArea.getText();
            String key = new String(keyField.getPassword());
            String iv = new String(ivField.getPassword());
            if (check(content, key, iv)) {
                try {
                    String result = DES3.encodeCBC(
                        key.getBytes(StandardCharsets.UTF_8),
                        iv.getBytes(StandardCharsets.UTF_8),
                        content.getBytes(StandardCharsets.UTF_8));
                    resultTextArea.setText(result);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "加密失败：" + ex.getMessage(), "异常", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 解密按钮事件
        decodeButton.addActionListener(e -> {
            String content = contentTextArea.getText();
            String key = new String(keyField.getPassword());
            String iv = new String(ivField.getPassword());
            if (check(content, key, iv)) {
                try {
                    String result = DES3.decodeCBC(
                        key.getBytes(StandardCharsets.UTF_8),
                        iv.getBytes(StandardCharsets.UTF_8),
                        content.getBytes(StandardCharsets.UTF_8));
                    resultTextArea.setText(result);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "解密失败：" + ex.getMessage(), "异常", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // 复制按钮事件
        copyButton.addActionListener(e -> {
            String result = resultTextArea.getText();
            if (result == null || result.isEmpty()) {
                JOptionPane.showMessageDialog(this, "结果为空");
            } else {
                StringSelection selection = new StringSelection(result);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(selection, null);
                JOptionPane.showMessageDialog(this, "结果已复制到剪贴板。");
            }
        });
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
        if (content == null || content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "内容不能为空", "校验错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (key == null || key.isEmpty()) {
            JOptionPane.showMessageDialog(this, "密钥不能为空", "校验错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (iv == null || iv.isEmpty()) {
            JOptionPane.showMessageDialog(this, "偏移量不能为空", "校验错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (key.length() != 24 || iv.length() != 8) {
            JOptionPane.showMessageDialog(this, "密钥或偏移量长度不对", "校验错误", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    /**
     * 创建并配置 GridBagConstraints 实例，便于复用和统一管理属性
     *
     * @param gridx   所在列
     * @param gridy   所在行
     * @param weightx 水平方向权重
     * @param fill    填充模式，如 GridBagConstraints.NONE / HORIZONTAL
     * @param insets  四周边距
     * @return 配置好的 GridBagConstraints 对象
     */
    private GridBagConstraints createGbc(int gridx, int gridy, double weightx, int fill, int gridWidth, Insets insets) {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gridx;           // 设置列索引
        gbc.gridy = gridy;           // 设置行索引
        gbc.weightx = weightx;       // 水平方向占比
        gbc.weighty = 0;             // 竖直方向不扩展
        gbc.fill = fill;             // 填充策略
        gbc.anchor = GridBagConstraints.WEST; // 锚点靠左
        gbc.insets = insets;         // 边距
        gbc.gridwidth = gridWidth;   //占据几列
        return gbc;
    }

    // 创建文本框的辅助方法
    private JTextArea createTextArea(int rows, int columns) {
        JTextArea textArea = new JTextArea(rows, columns);
        textArea.setFont(font);
        textArea.setForeground(Color.BLACK); // 设置文本颜色
        textArea.setBackground(Color.WHITE); // 设置背景颜色
        textArea.setLineWrap(true); // 启用自动换行
        textArea.setCaretColor(Color.BLACK); // 设置光标颜色
        textArea.setMargin(new Insets(10, 10, 10, 10)); // 设置边距
        textArea.setBorder(BorderFactory.createLineBorder(Color.GRAY)); // 设置边框
        return textArea;
    }

    // 创建密码输入框的辅助方法
    private JPasswordField createPasswordField(int columns) {
        JPasswordField passwordField = new JPasswordField(columns);
        passwordField.setFont(font); // 设置字体
        passwordField.setForeground(Color.BLACK); // 设置文本颜色
        passwordField.setBackground(Color.WHITE); // 设置背景颜色
        passwordField.setCaretColor(Color.BLACK); // 设置光标颜色
        passwordField.setBorder(BorderFactory.createLineBorder(Color.GRAY));// 设置边框
        return passwordField;
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
