import java.util.ArrayList;
import java.util.List;

public final class PrimitiveRootFinder {
    private PrimitiveRootFinder() {
    }

    public static List<Long> findAllPrimitiveRoots(long p) {
        if (!MathUtils.isPrime(p)) {
            throw new IllegalArgumentException("Модуль p должен быть простым числом.");
        }
        long phi = p - 1;
        List<Long> factors = MathUtils.primeFactors(phi);
        List<Long> roots = new ArrayList<>();

        for (long g = 2; g < p; g++) {
            boolean ok = true;
            for (long q : factors) {
                if (MathUtils.modPow(g, phi / q, p) == 1) {
                    ok = false;
                    break;
                }
            }
            if (ok) {
                roots.add(g);
            }
        }
        return roots;
    }
}
