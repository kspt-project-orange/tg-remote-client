package kspt.orange.tg_remote_client.api.util;

import org.jetbrains.annotations.NotNull;

import java.security.SecureRandom;
import java.util.Random;

public final class TokenGenerator {
    @NotNull
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    @NotNull
    private static final String LOWER = UPPER.toLowerCase();
    @NotNull
    private static final String DIGITS = "0123456789";
    @NotNull
    private static final String SPECIAL_SYMBOLS = "!@#$%^&*-_+=,.:;\"'(){}[]<>~`\\/|";
    private final int length;
    @NotNull
    private final Random random = new SecureRandom();
    @NotNull
    private final char[] symbols;

    public TokenGenerator(final int vocabulary, final int length) {
        if (length < 1 || (vocabulary & (Mode.LETTERS | Mode.DIGITS | Mode.SPECIAL_SYMBOLS)) == 0) {
            throw new IllegalArgumentException();
        }

        this.length = length;

        var symbols = "";
        if ((vocabulary & Mode.LETTERS) != 0) {
            symbols += (UPPER + LOWER);
        }
        if ((vocabulary & Mode.DIGITS) != 0) {
            symbols += DIGITS;
        }
        if ((vocabulary & Mode.SPECIAL_SYMBOLS) != 0) {
            symbols += SPECIAL_SYMBOLS;
        }

        this.symbols = symbols.toCharArray();
    }

    @NotNull
    public String nextToken() {
        final var buf = new char[length];
        for (int idx = 0; idx < buf.length; idx++) {
            buf[idx] = symbols[random.nextInt(symbols.length)];
        }

        return new String(buf);
    }

    public static final class Mode {
        public static final byte LETTERS         = 0b001;
        public static final byte DIGITS          = 0b010;
        public static final byte SPECIAL_SYMBOLS = 0b100;
        public static final byte ALL_SYMBOLS     = LETTERS | DIGITS | SPECIAL_SYMBOLS;
    }
}
