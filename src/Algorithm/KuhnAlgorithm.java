package Algorithm;

import java.util.*;

public class KuhnAlgorithm {

    private static List<List<Integer>> graph;
    private static int[] matching;
    private static boolean[] used;

    public static void main(String[] args) {


        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите количество вершин в левой доле (n): ");
        int n = scanner.nextInt();
        System.out.print("Введите количество вершин в правой доле (m): ");
        int m = scanner.nextInt();

        graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

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
        int maxMatching = findMaxMatching(n, m);
        System.out.println("\nРезультат:");
        System.out.println("Размер максимального паросочетания: " + maxMatching);

        System.out.println("Паросочетание:");
        for (int v = 0; v < m; v++) {
            if (matching[v] != -1) {
                System.out.println(matching[v] + " → " + v);
            }
        }
    }
    public static int findMaxMatching(int n, int m) {
        matching = new int[m];
        Arrays.fill(matching, -1);
        int result = 0;

        for (int u = 0; u < n; u++) {
            used = new boolean[n];
            if (dfs(u)) {
                result++;
            }
        }
        return result;
    }
    private static boolean dfs(int u) {
        if (used[u]) {
            return false;
        }
        used[u] = true;

        for (int v : graph.get(u)) {
            if (matching[v] == -1 || dfs(matching[v])) {
                matching[v] = u;
                return true;
            }
        }
        return false;
    }
}