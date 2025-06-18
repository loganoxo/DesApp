package com.logan.component;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;
import java.awt.*;

/**
 * 自定义按钮UI，用于绘制圆角按钮
 *
 * @author logan
 */
public class RoundButton extends BasicButtonUI {
    private final int arc;

    public RoundButton(int arc) {
        this.arc = arc;
    }

    @Override
    public void installUI(JComponent button) {
        super.installUI(button);
        button.setOpaque(false);
    }

    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        Graphics2D g2 = (Graphics2D) g;

        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g2.setColor(model.isArmed() ? b.getBackground().darker() : b.getBackground());
        g2.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc); // 绘制圆角矩形

        super.paint(g, c);
    }

    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect, String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        if (!model.isEnabled()) {
            super.paintText(g, c, textRect, text);
            return;
        }

        g.setColor(b.getForeground());

        if (model.isArmed()) {
            g.translate(1, 1);
        }

        super.paintText(g, c, textRect, text);
    }

    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        g.setColor(b.getBackground().darker());
        g.fillRoundRect(0, 0, b.getWidth(), b.getHeight(), arc, arc); // 绘制按下状态的圆角矩形
    }
}
