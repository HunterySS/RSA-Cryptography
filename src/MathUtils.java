import java.util.ArrayList;
import java.util.List;

public final class MathUtils {
    private MathUtils() {
    }

    public static long gcd(long a, long b) {
        a = Math.abs(a);
        b = Math.abs(b);
        while (b != 0) {
            long t = a % b;
            a = b;
            b = t;
        }
        return a;
    }

    public static boolean isPrime(long n) {
        if (n < 2) {
            return false;
        }
        if (n % 2 == 0) {
            return n == 2;
        }
        for (long i = 3; i * i <= n; i += 2) {
            if (n % i == 0) {
                return false;
            }
        }
        return true;
    }

    public static long modPow(long base, long exp, long mod) {
        if (mod <= 0) {
            throw new IllegalArgumentException("Модуль должен быть > 0.");
        }
        long result = 1 % mod;
        long a = modNormalize(base, mod);
        long z = exp;

        while (z > 0) {
            if ((z & 1L) == 1L) {
                result = mulMod(result, a, mod);
            }
            a = mulMod(a, a, mod);
            z >>= 1;
        }
        return result;
    }

    public static ExtendedGcdResult extendedGcd(long a, long b) {
        long oldR = a;
        long r = b;
        long oldS = 1;
        long s = 0;
        long oldT = 0;
        long t = 1;

        while (r != 0) {
            long q = oldR / r;
            long tmpR = oldR - q * r;
            oldR = r;
            r = tmpR;

            long tmpS = oldS - q * s;
            oldS = s;
            s = tmpS;

            long tmpT = oldT - q * t;
            oldT = t;
            t = tmpT;
        }
        return new ExtendedGcdResult(oldR, oldS, oldT);
    }

    public static long modInverse(long value, long mod) {
        ExtendedGcdResult r = extendedGcd(mod, value);
        if (Math.abs(r.gcd()) != 1) {
            throw new IllegalArgumentException("Обратный элемент не существует.");
        }
        return modNormalize(r.y(), mod);
    }

    public static List<Long> primeFactors(long n) {
        List<Long> factors = new ArrayList<>();
        long x = n;
        for (long i = 2; i * i <= x; i++) {
            if (x % i == 0) {
                factors.add(i);
                while (x % i == 0) {
                    x /= i;
                }
            }
        }
        if (x > 1) {
            factors.add(x);
        }
        return factors;
    }

    private static long mulMod(long a, long b, long mod) {
        return Math.floorMod(a * b, mod);
    }

    public static long modNormalize(long v, long mod) {
        long res = v % mod;
        if (res < 0) {
            res += mod;
        }
        return res;
    }

    public record ExtendedGcdResult(long gcd, long x, long y) {
    }
}
