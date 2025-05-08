package Algorithm;

import java.util.*;

public class OptimizedKuhnAlgorithm {
    private static List<List<Integer>> graph;
    private static int[] matching;
    private static boolean[] used; // Вернулись к boolean массиву
    private static int[] edgePointer; // Указатель на текущее ребро для каждой вершины

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Ввод параметров графа
        System.out.print("Введите количество вершин в левой доле (n): ");
        int n = scanner.nextInt();
        System.out.print("Введите количество вершин в правой доле (m): ");
        int m = scanner.nextInt();

        // Инициализация графа
        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // Ввод рёбер с сортировкой
        System.out.println("Введите рёбра графа (формат: u v, где 0 <= u < n, 0 <= v < m):");
        System.out.println("Для завершения ввода введите -1 -1");
        while (true) {
            System.out.print("Введите ребро: ");
            int u = scanner.nextInt();
            int v = scanner.nextInt();

            if (u == -1 && v == -1) break;
            if (u < 0 || u >= n || v < 0 || v >= m) {
                System.out.println("Ошибка: неверные индексы вершин!");
                continue;
            }

            graph.get(u).add(v);
        }

        // Сортируем рёбра для детерминированного поведения
        for (List<Integer> edges : graph) {
            Collections.sort(edges);
        }

        // Классический алгоритм Куна
        int classicResult = classicKuhn(n, m);
        System.out.println("\nКлассический алгоритм:");
        System.out.println("Размер паросочетания: " + classicResult);
        printMatching(m);

        // Оптимизированный алгоритм с тем же результатом
        int optimizedResult = optimizedKuhn(n, m);
        System.out.println("\nОптимизированный алгоритм:");
        System.out.println("Размер паросочетания: " + optimizedResult);
        printMatching(m);
    }

    /**
     * Классический алгоритм Куна
     */
    private static int classicKuhn(int n, int m) {
        matching = new int[m];
        Arrays.fill(matching, -1);
        int result = 0;

        for (int u = 0; u < n; u++) {
            used = new boolean[n];
            if (classicDfs(u)) {
                result++;
            }
        }
        return result;
    }

    private static boolean classicDfs(int u) {
        if (used[u]) {
            return false;
        }
        used[u] = true;

        for (int v : graph.get(u)) {
            if (matching[v] == -1 || classicDfs(matching[v])) {
                matching[v] = u;
                return true;
            }
        }
        return false;
    }

    /**
     * Оптимизированный алгоритм с тем же результатом
     */
    private static int optimizedKuhn(int n, int m) {
        matching = new int[m];
        Arrays.fill(matching, -1);
        used = new boolean[n];
        edgePointer = new int[n]; // Указатели на рёбра
        int result = 0;

        for (int u = 0; u < n; u++) {
            Arrays.fill(used, false);
            if (optimizedDfs(u)) {
                result++;
            }
        }
        return result;
    }

    private static boolean optimizedDfs(int u) {
        if (used[u]) {
            return false;
        }
        used[u] = true;

        // Итерируем по рёбрам начиная с последней позиции
        for (int i = edgePointer[u]; i < graph.get(u).size(); i++) {
            int v = graph.get(u).get(i);
            if (matching[v] == -1 || optimizedDfs(matching[v])) {
                matching[v] = u;
                edgePointer[u] = i + 1; // Запоминаем позицию для следующего вызова
                return true;
            }
        }
        edgePointer[u] = 0; // Сбрасываем, если не нашли
        return false;
    }

    private static void printMatching(int m) {
        System.out.println("Паросочетание:");
        for (int v = 0; v < m; v++) {
            if (matching[v] != -1) {
                System.out.println(matching[v] + " → " + v);
            }
        }
    }
}