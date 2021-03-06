/**
 * This is a simple implementation of in-place merge sort
 * at the expense of fixed-size buffer in addition to the 
 * original data storage and extra data moves required.
 * If the data is in Linked List format, there's no extra overhead.
 */
public class MergeSort {

    /**
     * Main method which tests the implementation
     * over 500 iterations on randomly generated numbers
     * of size ~110k
     */
    private static final int size = 109874;
    private static final int sizeBuffer = 500;

    public static void main(String[] args) {

        final double[] data = new double[size];
        final double[] buffer = new double[sizeBuffer];

        for (int i = 0; i < 500; ++i) {
            test(data, buffer);
            if ((i + 1) % 10 == 0) {
                System.out.println("Finished " + (i + 1) + " tests");
            }
        }

    }

    private static long count = 0;

    private static void test(final double[] data, final double[] buffer) {
        double sum = 0;
        double sqr = 0;
        for (int j = 0; j < size; ++j) {
            final double temp = Math.random() * size;
            data[j] = temp;
            sum += temp;
            sqr += temp * temp;
        }
        count = 0;
        long extraMoves = mergeSort(data, 0, size, buffer, sizeBuffer);
        final double logN = Math.log(size) / Math.log(2);
        System.out.println("Count%= " + (100 * (count / size) / logN) + ", Extra moves= " + extraMoves);

        double testSum = 0;
        double testSqr = 0;
        for (int j = 0; j < size; ++j) {
            if (j + 1 < size && data[j] > data[j + 1]) {
                System.out.println("WRONG!!!");
                printData(data, 0, size);
                System.out.println("\n\n");
                break;
            }
            final double val = data[j];
            testSum += val;
            testSqr += val * val;
        }
        final double sumDiff = (sum - testSum) / size;
        final double sqrDiff = (sqr - testSqr) / size;
        if (Math.abs(sumDiff) > 0.0001 || Math.abs(sqrDiff) > 0.01) {
            System.out.println("Sum diff= " + sumDiff + " , Sqr diff= " + sqrDiff);
        }
    }


    private static void printData(final double[] data, final int start, final int end) {
        System.out.print("Start: " + start + ", End: " + (end - 1) + "  :  ");
        for (int j = 0; j < size; ++j) {
            if (j == start) {
                System.out.print(" [ ");
            }
            if (j == end) {
                System.out.print(" ] ");
            }
            System.out.print((int) (data[j]) + " ");
        }
        System.out.println();
    }

    /**
     * Below is the implementation of
     * in-place merge sort.
     */

    private static long mergeSort(final double[] data, final int startIdx, final int endIdx,
                                  final double[] buffer, final int bufferSize) {

        final int diff = endIdx - startIdx;
        if (diff < 4) {
            // when partition size is smaller than 4, sort it manually
            switch (diff) {
            case 2:
                if (data[startIdx] > data[startIdx + 1]) {
                    final double temp = data[startIdx];
                    data[startIdx] = data[startIdx + 1];
                    data[startIdx + 1] = temp;
                }
                return 0;
            case 3:
                final double a = data[startIdx];
                final double b = data[startIdx + 1];
                final double c = data[startIdx + 2];
                if (a > b) {
                    if (a > c) {
                        if (b > c) {
                            data[startIdx] = c;
                            data[startIdx + 1] = b;
                        } else {
                            data[startIdx] = b;
                            data[startIdx + 1] = c;
                        }
                        data[startIdx + 2] = a;
                    } else {
                        data[startIdx] = b;
                        data[startIdx + 1] = a;
                    }
                } else {
                    if (a < c) {
                        if (b > c) {
                            data[startIdx + 1] = c;
                            data[startIdx + 2] = b;
                        }
                    } else {
                        data[startIdx] = c;
                        data[startIdx + 1] = a;
                        data[startIdx + 2] = b;
                    }
                }
                return 0;
            default:
                return 0;
            }
        }

        // divide part is same as standard merge sort
        int middleIdx = (startIdx + endIdx) / 2;
        long extraLeft = mergeSort(data, startIdx, middleIdx, buffer, bufferSize);
        long extraRight = mergeSort(data, middleIdx, endIdx, buffer, bufferSize);

        // merge part is where it differs from standard algorithm
        int start = startIdx;
        final int end = endIdx;
        int middle = (start + end) / 2;
        long extraMoves = extraLeft + extraRight;
        while (start < middle) {

            int right = middle;
            int augptr = middle;  // auxilary pointer to keep track
            int ptr = start;
            boolean aug = false;

            while (ptr < middle && right < end) {

                int left = aug ? augptr : ptr;
                final double lval = data[left];
                final double rval = data[right];
                if (lval <= rval) {
                    if (aug) {
                        data[augptr] = data[ptr];
                        ++augptr;
                        if (augptr == right) {
                            augptr = middle;
                        }
                        data[ptr] = lval;
                    }
                } else {
                    if (aug) {
                        final double newVal = data[ptr];
                        if (augptr == middle) {
                            data[right] = newVal;
                        } else {
                            final int sizeL = augptr - middle;
                            final int sizeR = right - augptr;
                            if (sizeL < sizeR) {
                                extraMoves += augptr - middle;
                                data[right] = data[middle];
                                final int replaceIdx = augptr - 1;
                                for (int j = middle; j < replaceIdx; ++j) {
                                    data[j] = data[j + 1];
                                }
                                count += replaceIdx - middle;
                                data[replaceIdx] = newVal;
                            } else {
                                extraMoves += right - augptr + 1;
                                for (int j = right; j > augptr; --j) {
                                    data[j] = data[j - 1];
                                }
                                count += right - augptr;
                                data[augptr] = newVal;
                                augptr++;
                            }
                        }
                    } else {
                        data[right] = lval;
                    }
                    data[ptr] = rval;
                    ++right;
                    aug = true;
                }
                ++ptr;
            }

            // post-process circular array
            while (augptr > middle) {
                final int moveSize = (middle + bufferSize <= augptr) ? bufferSize : augptr - middle;
                extraMoves += moveSize + right - middle;
                for (int j = 0; j < moveSize; ++j) {
                    buffer[j] = data[j + middle];
                }
                for (int j = middle + moveSize; j < right; ++j) {
                    data[j - moveSize] = data[j];
                }
                final int refIdx = right - moveSize;
                for (int j = 0; j < moveSize; ++j) {
                    data[refIdx + j] = buffer[j];
                }
                count += moveSize + right - middle;
                augptr -= moveSize;
            }
            if (right == end) {
                int midCopy = middle;
                while (midCopy > ptr) {
                    final int moveSize = ptr + bufferSize <= midCopy ? bufferSize : midCopy - ptr;
                    extraMoves += moveSize + end - ptr;
                    for (int j = 0; j < moveSize; ++j) {
                        buffer[j] = data[j + ptr];
                    }
                    for (int j = ptr + moveSize; j < end; ++j) {
                        data[j - moveSize] = data[j];
                    }
                    final int refIdx = end - moveSize;
                    for (int j = 0; j < moveSize; ++j) {
                        data[j + refIdx] = buffer[j];
                    }
                    count += moveSize + end - ptr;
                    midCopy -= moveSize;
                }
            }
            // next subarray
            start = middle;
            middle = right;
        }
        return extraMoves;
    }

}
