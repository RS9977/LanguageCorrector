package GUI;

import javax.swing.*;
import java.awt.*;

public class ColorSpectrumPanel extends JPanel {
    private int width;
    private int height;

    public ColorSpectrumPanel(int width, int height) {
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw color spectrum
        for (int i = 0; i < width; i++) {
            float saturation = (float) i / (float) width;
            Color color = Color.getHSBColor(0, saturation, 1);
            g2d.setColor(color);
            g2d.drawLine(i, 0, i, height);
        }

        // Add labels
        g2d.setColor(Color.BLACK);
        g2d.drawString("Confidence=0", 5, height - 5);
        g2d.drawString("Confidence=100    ", width - 110, height - 5);
    }
}
