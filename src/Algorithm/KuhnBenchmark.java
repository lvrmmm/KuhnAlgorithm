package Algorithm;

import java.util.*;

public class KuhnBenchmark {
    private static List<List<Integer>> graph;
    private static int[] matching;

    // Для стандартной версии
    private static boolean[] usedStandard;

    // Для оптимизированной версии
    private static int[] usedOptimized;
    private static int iteration;

    public static void main(String[] args) {
        // Генерация тестового графа (можно заменить на ввод с клавиатуры)
        int n = 10000; // вершин в левой доле
        int m = 10000; // вершин в правой доле
        generateRandomGraph(n, m, 5); // в среднем 5 ребер на вершину

        // Замер стандартной версии
        long startStandard = System.nanoTime();
        int resultStandard = findMaxMatchingStandard(n, m);
        long endStandard = System.nanoTime();

        // Замер оптимизированной версии
        long startOptimized = System.nanoTime();
        int resultOptimized = findMaxMatchingOptimized(n, m);
        long endOptimized = System.nanoTime();

        // Вывод результатов
        System.out.println("Стандартная версия:");
        System.out.println("Результат: " + resultStandard);
        System.out.println("Время: " + (endStandard - startStandard)/1_000_000 + " мс");

        System.out.println("\nОптимизированная версия:");
        System.out.println("Результат: " + resultOptimized);
        System.out.println("Время: " + (endOptimized - startOptimized)/1_000_000 + " мс");
    }

    // Генерация случайного двудольного графа
    private static void generateRandomGraph(int n, int m, int avgEdges) {
        graph = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < n; i++) {
            List<Integer> edges = new ArrayList<>();
            int edgesCount = random.nextInt(avgEdges * 2); // примерное количество ребер

            for (int j = 0; j < edgesCount; j++) {
                edges.add(random.nextInt(m));
            }
            graph.add(edges);
        }
    }

    // Стандартная версия алгоритма Куна
    private static int findMaxMatchingStandard(int n, int m) {
        matching = new int[m];
        Arrays.fill(matching, -1);
        int result = 0;

        for (int u = 0; u < n; u++) {
            usedStandard = new boolean[n];
            if (dfsStandard(u)) {
                result++;
            }
        }
        return result;
    }

    private static boolean dfsStandard(int u) {
        if (usedStandard[u]) {
            return false;
        }
        usedStandard[u] = true;

        for (int v : graph.get(u)) {
            if (matching[v] == -1 || dfsStandard(matching[v])) {
                matching[v] = u;
                return true;
            }
        }
        return false;
    }

    // Оптимизированная версия алгоритма Куна
    public static int findMaxMatchingOptimized(int n, int m) {
        matching = new int[m];
        Arrays.fill(matching, -1);
        usedOptimized = new int[n];
        iteration = 0;
        int result = 0;

        // Фиксированный порядок обработки
        for (int u = 0; u < n; u++) {
            List<Integer> edges = graph.get(u);
            edges.sort(Comparator.naturalOrder()); // Сортировка!
            iteration++;
            if (dfsOptimized(u)) {
                result++;
            }
        }
        return result;
    }

    private static int greedyInitialize(int n, int m) {
        int matched = 0;
        for (int u = 0; u < n; u++) {
            for (int v : graph.get(u)) {
                if (matching[v] == -1) {
                    matching[v] = u;
                    matched++;
                    break;
                }
            }
        }
        return matched;
    }

    private static boolean dfsOptimized(int u) {
        if (usedOptimized[u] == iteration) {
            return false;
        }
        usedOptimized[u] = iteration;

        // Первый проход: ищем свободные вершины
        for (int v : graph.get(u)) {
            if (matching[v] == -1) {
                matching[v] = u;
                return true;
            }
        }

        // Второй проход: рекурсивный поиск
        for (int v : graph.get(u)) {
            if (dfsOptimized(matching[v])) {
                matching[v] = u;
                return true;
            }
        }

        return false;
    }
}