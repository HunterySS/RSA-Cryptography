import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class RSAService {
    public EncryptionResult encryptFile(Path inputFile, Path outputFile, long p, long q, long d) throws IOException {
        validateEncryptionParams(p, q, d);

        long r = p * q;
        long phi = (p - 1) * (q - 1);
        long e = MathUtils.modInverse(d, phi);

        byte[] input = Files.readAllBytes(inputFile);
        List<Integer> blocks = new ArrayList<>(input.length);

        try (DataOutputStream out = new DataOutputStream(Files.newOutputStream(outputFile))) {
            for (byte value : input) {
                int m = Byte.toUnsignedInt(value);
                int c = (int) MathUtils.modPow(m, e, r);
                out.writeShort(c & 0xFFFF);
                blocks.add(c);
            }
        }

        return new EncryptionResult(e, r, blocks.size(), blocks);
    }

    public DecryptionResult decryptFile(Path inputFile, Path outputFile, long r, long d) throws IOException {
        validateDecryptionParams(r, d);

        List<Byte> bytes = new ArrayList<>();
        List<Integer> encryptedBlocks = new ArrayList<>();

        try (DataInputStream in = new DataInputStream(Files.newInputStream(inputFile))) {
            while (true) {
                try {
                    int c = in.readUnsignedShort();
                    encryptedBlocks.add(c);
                    long m = MathUtils.modPow(c, d, r);
                    if (m < 0 || m > 255) {
                        throw new IllegalArgumentException(
                                "Некорректный расшифрованный байт " + m + ". Проверьте r/Kc и входной файл."
                        );
                    }
                    bytes.add((byte) m);
                } catch (EOFException eof) {
                    break;
                }
            }
        }

        byte[] out = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            out[i] = bytes.get(i);
        }
        Files.write(outputFile, out);

        return new DecryptionResult(out.length, encryptedBlocks);
    }

    private void validateEncryptionParams(long p, long q, long d) {
        if (!MathUtils.isPrime(p) || !MathUtils.isPrime(q)) {
            throw new IllegalArgumentException("p и q должны быть простыми числами.");
        }
        if (p == q) {
            throw new IllegalArgumentException("p и q не должны совпадать.");
        }
        long r = p * q;
        long phi = (p - 1) * (q - 1);

        if (r <= 255) {
            throw new IllegalArgumentException("Требуется p*q > 255, чтобы кодировать байт.");
        }
        if (r > 65535) {
            throw new IllegalArgumentException("Требуется p*q <= 65535, т.к. шифроблок должен быть 16-бит.");
        }
        if (d <= 1 || d >= phi) {
            throw new IllegalArgumentException("Kc (d) должен удовлетворять 1 < d < phi(r).");
        }
        if (MathUtils.gcd(d, phi) != 1) {
            throw new IllegalArgumentException("Kc (d) должен быть взаимно прост с phi(r).");
        }
    }

    private void validateDecryptionParams(long r, long d) {
        if (r <= 255 || r > 65535) {
            throw new IllegalArgumentException("r должен быть в диапазоне [256..65535].");
        }
        if (d <= 1 || d >= r) {
            throw new IllegalArgumentException("Kc (d) должен удовлетворять 1 < d < r.");
        }
    }

    public record EncryptionResult(long e, long r, int blockCount, List<Integer> encryptedBlocks) {
    }
    public record DecryptionResult(int blockCount, List<Integer> encryptedBlocks) {
    }
}
