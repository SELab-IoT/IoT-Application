package kr.ac.hanyang.selab.iot_application.utill;

public class Hash {
    final static String TAG = "Hash";
    public static String SHA3_256(String key) {
        String digest = SHA3.digestHex(256, key.getBytes());
        // 만약 Java8로 옮긴다면 저 밑에 SHA3 클래스 지우고 요거 쓸 것.
        // byte[] digest = MessageDigest.getInstance("SHA3-256").digest(key.getBytes(charset));
        return digest;
    }
}

/** Licence of SHA3 class **/
/*************************************************************************************
 * MIT License
 *
 * Copyright (c) 2018 Xwood
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *************************************************************************************/

class SHA3 {

    private static final char[] HEX_DIGITS = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    private static long[] KECCAK_ROUND_CONSTANTS = new long[] {
            0x0000000000000001L, 0x0000000000008082L, 0x800000000000808aL, 0x8000000080008000L,
            0x000000000000808bL, 0x0000000080000001L, 0x8000000080008081L, 0x8000000000008009L,
            0x000000000000008aL, 0x0000000000000088L, 0x0000000080008009L, 0x000000008000000aL,
            0x000000008000808bL, 0x800000000000008bL, 0x8000000000008089L, 0x8000000000008003L,
            0x8000000000008002L, 0x8000000000000080L, 0x000000000000800aL, 0x800000008000000aL,
            0x8000000080008081L, 0x8000000000008080L, 0x0000000080000001L, 0x8000000080008008L
    };

    private static int absorb(
            final byte[] data,
            final int offset,
            final int length,
            final long[] state,
            final byte[] queue,
            final int rate,
            final int queueBits) {

        int bytesInQueue = queueBits >> 3;
        final int rateBytes = rate >> 3;
        int count = 0;
        while (count < length) {
            if (bytesInQueue == 0 && count <= (length - rateBytes)) {
                do {
                    keccakAbsorb(data, offset + count, state, rate);
                    count += rateBytes;
                } while (count <= (length - rateBytes));
            }
            else {
                final int partialBlock = Math.min(rateBytes - bytesInQueue, length - count);
                System.arraycopy(data, offset + count, queue, bytesInQueue, partialBlock);
                bytesInQueue += partialBlock;
                count += partialBlock;
                if (bytesInQueue == rateBytes) {
                    keccakAbsorb(queue, 0, state, rate);
                    bytesInQueue = 0;
                }
            }
        }
        return (bytesInQueue << 3);
    }

    private static int absorbBits(
            final int data,
            final int bits,
            final byte[] queue,
            final int queueBits) {

        final int mask = (1 << bits) - 1;
        queue[queueBits >> 3] = (byte) (data & mask);
        return queueBits + bits;
    }

    public static byte[] digest(final int bits, final byte[] input) {
        return digest(bits, input, 0, input.length);
    }

    public static byte[] digest(final int bits, final byte[] input, final int inputOffset, final int inputLength) {
        final byte[] out = new byte[bits / 8];
        digest(bits, input, inputOffset, inputLength, out, 0);
        return out;
    }

    public static void digest(
            final int bits,
            final byte[] input,
            final int inputOffset,
            final int inputLength,
            final byte[] output,
            final int outputOffset) {

        final int rate = rate(bits);
        final long[] state = new long[25];
        final byte[] queue = new byte[192];
        int queueBits = absorb(input, inputOffset, inputLength, state, queue, rate, 0);
        queueBits = absorbBits(0x02, 2, queue, queueBits);
        squeezePad(state, rate, queue, queueBits);
        squeeze(state, rate, bits, queue, output, outputOffset);
    }

    public static String digestHex(final int bits, final byte[] data) {
        return hex(digest(bits, data, 0, data.length));
    }

    private static String hex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        int i = 0;
        int j = 0;
        while (i < l) {
            out[j++] = HEX_DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = HEX_DIGITS[0x0F & data[i]];
            i++;
        }
        return new String(out);
    }

    public static void keccakAbsorb(final byte[] data, final int offset, final long[] state, final int rate) {

        final int count = rate >> 6;
        int i = 0;
        int cursor = offset;
        while (i < count) {
            state[i] ^= leToLong(data, cursor);
            cursor += 8;
            i++;
        }
        keccakPermutation(state);
    }

    private static void keccakExtract(final long[] state, final int rate, final byte[] queue) {
        leFromLongs(state, 0, queue, 0, rate >> 6);
    }

    public static void keccakPermutation(final long[] a) {
        final long c[] = new long[5];
        final long d[] = new long[5];
        int round = 0;
        while (round < 24) {
            c[0] = a[0] ^ a[5] ^ a[10] ^ a[15] ^ a[20];
            c[1] = a[1] ^ a[6] ^ a[11] ^ a[16] ^ a[21];
            c[2] = a[2] ^ a[7] ^ a[12] ^ a[17] ^ a[22];
            c[3] = a[3] ^ a[8] ^ a[13] ^ a[18] ^ a[23];
            c[4] = a[4] ^ a[9] ^ a[14] ^ a[19] ^ a[24];
            d[1] = (c[1] << 1 | c[1] >>> -1) ^ c[4];
            d[2] = (c[2] << 1 | c[2] >>> -1) ^ c[0];
            d[3] = (c[3] << 1 | c[3] >>> -1) ^ c[1];
            d[4] = (c[4] << 1 | c[4] >>> -1) ^ c[2];
            d[0] = (c[0] << 1 | c[0] >>> -1) ^ c[3];
            a[0] ^= d[1];
            a[5] ^= d[1];
            a[10] ^= d[1];
            a[15] ^= d[1];
            a[20] ^= d[1];
            a[1] ^= d[2];
            a[6] ^= d[2];
            a[11] ^= d[2];
            a[16] ^= d[2];
            a[21] ^= d[2];
            a[2] ^= d[3];
            a[7] ^= d[3];
            a[12] ^= d[3];
            a[17] ^= d[3];
            a[22] ^= d[3];
            a[3] ^= d[4];
            a[8] ^= d[4];
            a[13] ^= d[4];
            a[18] ^= d[4];
            a[23] ^= d[4];
            a[4] ^= d[0];
            a[9] ^= d[0];
            a[14] ^= d[0];
            a[19] ^= d[0];
            a[24] ^= d[0];
            c[1] = a[1] << 1 | a[1] >>> 63;
            a[1] = a[6] << 44 | a[6] >>> 20;
            a[6] = a[9] << 20 | a[9] >>> 44;
            a[9] = a[22] << 61 | a[22] >>> 3;
            a[22] = a[14] << 39 | a[14] >>> 25;
            a[14] = a[20] << 18 | a[20] >>> 46;
            a[20] = a[2] << 62 | a[2] >>> 2;
            a[2] = a[12] << 43 | a[12] >>> 21;
            a[12] = a[13] << 25 | a[13] >>> 39;
            a[13] = a[19] << 8 | a[19] >>> 56;
            a[19] = a[23] << 56 | a[23] >>> 8;
            a[23] = a[15] << 41 | a[15] >>> 23;
            a[15] = a[4] << 27 | a[4] >>> 37;
            a[4] = a[24] << 14 | a[24] >>> 50;
            a[24] = a[21] << 2 | a[21] >>> 62;
            a[21] = a[8] << 55 | a[8] >>> 9;
            a[8] = a[16] << 45 | a[16] >>> 19;
            a[16] = a[5] << 36 | a[5] >>> 28;
            a[5] = a[3] << 28 | a[3] >>> 36;
            a[3] = a[18] << 21 | a[18] >>> 43;
            a[18] = a[17] << 15 | a[17] >>> 49;
            a[17] = a[11] << 10 | a[11] >>> 54;
            a[11] = a[7] << 6 | a[7] >>> 58;
            a[7] = a[10] << 3 | a[10] >>> 61;
            a[10] = c[1];
            c[0] = a[0] ^ (~a[1] & a[2]);
            c[1] = a[1] ^ (~a[2] & a[3]);
            a[2] ^= ~a[3] & a[4];
            a[3] ^= ~a[4] & a[0];
            a[4] ^= ~a[0] & a[1];
            a[0] = c[0];
            a[1] = c[1];
            c[0] = a[5] ^ (~a[6] & a[7]);
            c[1] = a[6] ^ (~a[7] & a[8]);
            a[7] ^= ~a[8] & a[9];
            a[8] ^= ~a[9] & a[5];
            a[9] ^= ~a[5] & a[6];
            a[5] = c[0];
            a[6] = c[1];
            c[0] = a[10] ^ (~a[11] & a[12]);
            c[1] = a[11] ^ (~a[12] & a[13]);
            a[12] ^= ~a[13] & a[14];
            a[13] ^= ~a[14] & a[10];
            a[14] ^= ~a[10] & a[11];
            a[10] = c[0];
            a[11] = c[1];
            c[0] = a[15] ^ (~a[16] & a[17]);
            c[1] = a[16] ^ (~a[17] & a[18]);
            a[17] ^= ~a[18] & a[19];
            a[18] ^= ~a[19] & a[15];
            a[19] ^= ~a[15] & a[16];
            a[15] = c[0];
            a[16] = c[1];
            c[0] = a[20] ^ (~a[21] & a[22]);
            c[1] = a[21] ^ (~a[22] & a[23]);
            a[22] ^= ~a[23] & a[24];
            a[23] ^= ~a[24] & a[20];
            a[24] ^= ~a[20] & a[21];
            a[20] = c[0];
            a[21] = c[1];
            a[0] ^= KECCAK_ROUND_CONSTANTS[round];
            round++;
        }
    }

    private static void leFromInt(final int src, final byte[] dest, final int destOffset) {
        dest[destOffset + 0] = (byte) (src);
        dest[destOffset + 1] = (byte) ((src >> 8));
        dest[destOffset + 2] = (byte) ((src >> 16));
        dest[destOffset + 3] = (byte) ((src >> 24));
    }

    private static void leFromLongs(
            final long[] src,
            final int srcOffset,
            final byte[] dest,
            final int destOffset,
            final int limit) {

        int cursor = destOffset;
        int i = 0;
        while (i < limit) {
            final long val = src[srcOffset + i];
            leFromInt((int) (val & 0xffffffffL), dest, cursor);
            leFromInt((int) (val >>> 32), dest, cursor + 4);
            cursor += 8;
            i++;
        }
    }

    private static long leToLong(final byte[] src, final int srcOffset) {
        final long lo = leToToInt(src, srcOffset);
        final long hi = leToToInt(src, srcOffset + 4);
        return ((hi & 0xffffffffL) << 32) | lo & 0xffffffffL;
    }

    private static int leToToInt(final byte[] value, final int offset) {
        return (value[offset + 0] & 0xff)
                | ((value[offset + 1] & 0xff) << 8)
                | ((value[offset + 2] & 0xff) << 16)
                | ((value[offset + 3] & 0xff) << 24);
    }

    private static int rate(final int bits) {
        return Math.max(576, 1600 - (bits << 1));
    }

    private static void squeeze(
            final long[] state,
            final int rate,
            final int bits,
            final byte[] queue,
            final byte[] out,
            final int outOffset) {

        int queueBits = rate;
        long i = 0;
        while (i < bits) {
            if (queueBits == 0) {
                keccakPermutation(state);
                keccakExtract(state, rate, queue);
                queueBits = rate;
            }
            final int partialBlock = (int) Math.min(queueBits, bits - i);
            System.arraycopy(queue, (rate - queueBits) / 8, out, outOffset + (int) (i / 8), partialBlock / 8);
            queueBits -= partialBlock;
            i += partialBlock;
        }
    }

    private static void squeezePad(final long[] state, final int rate, final byte[] queue, final int queueBits) {

        int cursor = queueBits;
        final byte c = (byte) (1L << (cursor & 7));
        queue[cursor >> 3] |= c;
        if (++cursor == rate) {
            keccakAbsorb(queue, 0, state, rate);
            cursor = 0;
        }
        final int full = cursor >> 6;
        final int partial = cursor & 63;
        int off = 0;
        int i = 0;
        while (i < full) {
            state[i] ^= leToLong(queue, off);
            off += 8;
            i++;
        }
        if (partial > 0) {
            final long mask = (1L << partial) - 1L;
            state[full] ^= leToLong(queue, off) & mask;
        }
        state[(rate - 1) >> 6] ^= (1L << 63);
        keccakPermutation(state);
        keccakExtract(state, rate, queue);
    }

}