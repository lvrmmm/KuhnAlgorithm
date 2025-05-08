package Visualization;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class KuhnVisualizer extends JFrame {
    private final int MAX_VERTICES = 10;
    private int leftCount = 0, rightCount = 0;
    private ArrayList<Point> leftVertices = new ArrayList<>();
    private ArrayList<Point> rightVertices = new ArrayList<>();
    private boolean[][] edges = new boolean[MAX_VERTICES][MAX_VERTICES];
    private Integer selectedVertex = null;
    private JTextArea explanationArea;
    private GraphPanel graphPanel;
    private JButton runButton, resetButton, stepButton;

    private int[] matchRight = new int[MAX_VERTICES];
    private boolean[] used;

    private int currentStepVertex = 0;
    private boolean[] currentUsed;
    private boolean[] currentUsedRight = new boolean[MAX_VERTICES];

    public KuhnVisualizer() {
        setTitle("Визуализация алгоритма Куна");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel();
        JTextField leftInput = new JTextField(2);
        JTextField rightInput = new JTextField(2);
        JButton buildButton = new JButton("Построить граф");
        controlPanel.add(new JLabel("Левая доля:"));
        controlPanel.add(leftInput);
        controlPanel.add(new JLabel("Правая доля:"));
        controlPanel.add(rightInput);
        controlPanel.add(buildButton);

        runButton = new JButton("Запустить алгоритм");
        stepButton = new JButton("Шаг алгоритма");
        runButton.setEnabled(false);
        stepButton.setEnabled(false);
        resetButton = new JButton("Сбросить");
        controlPanel.add(runButton);
        controlPanel.add(stepButton);
        controlPanel.add(resetButton);
        add(controlPanel, BorderLayout.NORTH);

        explanationArea = new JTextArea(3, 20);
        explanationArea.setEditable(false);
        add(new JScrollPane(explanationArea), BorderLayout.SOUTH);

        graphPanel = new GraphPanel();
        add(graphPanel, BorderLayout.CENTER);

        buildButton.addActionListener(e -> {
            try {
                leftCount = Math.min(MAX_VERTICES, Integer.parseInt(leftInput.getText()));
                rightCount = Math.min(MAX_VERTICES, Integer.parseInt(rightInput.getText()));
                generateVertices();
                edges = new boolean[MAX_VERTICES][MAX_VERTICES];
                selectedVertex = null;
                Arrays.fill(matchRight, -1);
                runButton.setEnabled(true);
                stepButton.setEnabled(true);
                currentStepVertex = 0;
                currentUsed = new boolean[MAX_VERTICES];
                currentUsedRight = new boolean[MAX_VERTICES];
                explanationArea.setText("Граф построен. Добавьте ребра по клику.");
                graphPanel.repaint();
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Введите корректное количество вершин.");
            }
        });

        runButton.addActionListener(e -> runKuhnAlgorithm());

        stepButton.addActionListener(e -> stepKuhnAlgorithm());

        resetButton.addActionListener(e -> {
            leftCount = rightCount = 0;
            leftVertices.clear();
            rightVertices.clear();
            selectedVertex = null;
            runButton.setEnabled(false);
            stepButton.setEnabled(false);
            explanationArea.setText("");
            Arrays.fill(matchRight, -1);
            edges = new boolean[MAX_VERTICES][MAX_VERTICES];
            currentStepVertex = 0;
            currentUsed = new boolean[MAX_VERTICES];
            currentUsedRight = new boolean[MAX_VERTICES];
            graphPanel.repaint();
        });
    }

    private void generateVertices() {
        leftVertices.clear();
        rightVertices.clear();
        int height = graphPanel.getHeight();
        int spacingL = height / (leftCount + 1);
        int spacingR = height / (rightCount + 1);
        for (int i = 0; i < leftCount; i++) {
            leftVertices.add(new Point(100, spacingL * (i + 1)));
        }
        for (int i = 0; i < rightCount; i++) {
            rightVertices.add(new Point(600, spacingR * (i + 1)));
        }
    }

    private void runKuhnAlgorithm() {
        Arrays.fill(matchRight, -1);
        for (int v = 0; v < leftCount; v++) {
            used = new boolean[leftCount];
            dfs(v);
        }
        explanationArea.setText("Алгоритм завершен. Найдено паросочетание.");
        graphPanel.repaint();
    }

    private void stepKuhnAlgorithm() {
        if (currentStepVertex >= leftCount) {
            explanationArea.setText("Пошаговое выполнение завершено. Результат готов.");
            return;
        }
        currentUsed = new boolean[leftCount];
        currentUsedRight = new boolean[rightCount];
        boolean found = dfsStep(currentStepVertex);
        explanationArea.setText("Обработка вершины L" + currentStepVertex + (found ? ": найден путь." : ": путь не найден."));
        currentStepVertex++;
        graphPanel.repaint();
    }

    private boolean dfs(int v) {
        if (used[v]) return false;
        used[v] = true;
        for (int u = 0; u < rightCount; u++) {
            if (edges[v][u]) {
                if (matchRight[u] == -1 || dfs(matchRight[u])) {
                    matchRight[u] = v;
                    return true;
                }
            }
        }
        return false;
    }

    private boolean dfsStep(int v) {
        if (currentUsed[v]) return false;
        currentUsed[v] = true;
        for (int u = 0; u < rightCount; u++) {
            if (edges[v][u]) {
                currentUsedRight[u] = true;
                if (matchRight[u] == -1 || dfsStep(matchRight[u])) {
                    matchRight[u] = v;
                    return true;
                }
            }
        }
        return false;
    }

    private class GraphPanel extends JPanel {
        public GraphPanel() {
            addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    Point click = e.getPoint();
                    for (int i = 0; i < leftVertices.size(); i++) {
                        if (click.distance(leftVertices.get(i)) < 20) {
                            selectedVertex = i;
                            return;
                        }
                    }
                    for (int i = 0; i < rightVertices.size(); i++) {
                        if (selectedVertex != null && click.distance(rightVertices.get(i)) < 20) {
                            edges[selectedVertex][i] = !edges[selectedVertex][i];
                            selectedVertex = null;
                            repaint();
                            return;
                        }
                    }
                }
            });
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setFont(new Font("Arial", Font.PLAIN, 14));

            // Устанавливаем толщину линии для ребер
            g2d.setStroke(new BasicStroke(3)); // Толщина 3 пикселя

            for (int i = 0; i < leftVertices.size(); i++) {
                for (int j = 0; j < rightVertices.size(); j++) {
                    if (edges[i][j]) {
                        if (matchRight[j] == i) {
                            g2d.setColor(Color.GREEN);
                        } else {
                            g2d.setColor(Color.LIGHT_GRAY);
                        }
                        Point p1 = leftVertices.get(i);
                        Point p2 = rightVertices.get(j);
                        g2d.drawLine(p1.x, p1.y, p2.x, p2.y);
                    }
                }
            }

            // Возвращаем стандартную толщину для остальных элементов
            g2d.setStroke(new BasicStroke(1));

            for (int i = 0; i < leftVertices.size(); i++) {
                if (currentUsed != null && currentUsed[i]) {
                    g2d.setColor(Color.ORANGE);
                } else {
                    g2d.setColor(Color.CYAN);
                }
                g2d.fillOval(leftVertices.get(i).x - 10, leftVertices.get(i).y - 10, 20, 20);
                g2d.setColor(Color.BLACK);
                g2d.drawString("L" + i, leftVertices.get(i).x - 20, leftVertices.get(i).y);
            }

            for (int i = 0; i < rightVertices.size(); i++) {
                if (currentUsedRight != null && currentUsedRight[i]) {
                    g2d.setColor(Color.ORANGE);
                } else {
                    g2d.setColor(Color.PINK);
                }
                g2d.fillOval(rightVertices.get(i).x - 10, rightVertices.get(i).y - 10, 20, 20);
                g2d.setColor(Color.BLACK);
                g2d.drawString("R" + i, rightVertices.get(i).x + 15, rightVertices.get(i).y);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KuhnVisualizer().setVisible(true));
    }
}
