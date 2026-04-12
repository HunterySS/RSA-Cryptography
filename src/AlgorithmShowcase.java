import java.util.List;

public final class AlgorithmShowcase {
    private AlgorithmShowcase() {
    }

    public static void printAll() {
        printFastExponentiationDemo();
        System.out.println();
        printPrimitiveRootsDemo();
        System.out.println();
        printExtendedEuclidDemo();
    }

    private static void printFastExponentiationDemo() {
        long a = 11;
        long z = 23;
        long n = 53;

        System.out.println("=== Быстрое возведение в степень ===");
        System.out.println("Пример: " + a + "^" + z + " mod " + n);
        long result = 1;
        long a1 = a % n;
        long z1 = z;
        int step = 0;

        System.out.printf("%-5s %-8s %-8s %-8s%n", "Шаг", "z1", "a1", "x");
        while (z1 > 0) {
            if ((z1 & 1L) == 0) {
                z1 /= 2;
                a1 = (a1 * a1) % n;
            } else {
                z1 -= 1;
                result = (result * a1) % n;
            }
            System.out.printf("%-5d %-8d %-8d %-8d%n", step++, z1, a1, result);
        }

        System.out.println("Результат: " + a + "^" + z + " mod " + n + " = " + result);
    }

    private static void printPrimitiveRootsDemo() {
        long p = 43;
        List<Long> roots = PrimitiveRootFinder.findAllPrimitiveRoots(p);

        System.out.println("=== Поиск первообразных корней ===");
        System.out.println("Модуль p = " + p);
        System.out.println("Простые делители p-1 = " + MathUtils.primeFactors(p - 1));
        System.out.println("Все первообразные корни по модулю " + p + ":");
        for (Long root : roots) {
            System.out.print(root + " ");
        }
        System.out.println();
        System.out.println("Количество: " + roots.size());
    }

    private static void printExtendedEuclidDemo() {
        long a = 899;
        long b = 493;
        System.out.println("=== Расширенный алгоритм Евклида ===");
        System.out.println("Пример: a = " + a + ", b = " + b);

        long d0 = a;
        long d1 = b;
        long x0 = 1;
        long x1 = 0;
        long y0 = 0;
        long y1 = 1;
        int iter = 0;
        System.out.printf("%-8s %-6s %-8s %-8s %-8s %-8s %-8s %-8s%n",
                "Итер.", "q", "d0", "d1", "x0", "x1", "y0", "y1");
        System.out.printf("%-8d %-6s %-8d %-8d %-8d %-8d %-8d %-8d%n",
                iter++, "-", d0, d1, x0, x1, y0, y1);

        while (d1 != 0) {
            long q = d0 / d1;
            long d2 = d0 % d1;
            long x2 = x0 - q * x1;
            long y2 = y0 - q * y1;

            d0 = d1;
            d1 = d2;
            x0 = x1;
            x1 = x2;
            y0 = y1;
            y1 = y2;

            System.out.printf("%-8d %-6d %-8d %-8d %-8d %-8d %-8d %-8d%n",
                    iter++, q, d0, d1, x0, x1, y0, y1);
        }

        System.out.println("НОД(a,b) = " + d0);
        System.out.println("Коэффициенты: x = " + x0 + ", y = " + y0);
        System.out.println("Проверка: " + x0 + " * " + a + " + " + y0 + " * " + b + " = " + (x0 * a + y0 * b));
    }
}
