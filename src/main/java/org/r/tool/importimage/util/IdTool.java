package org.r.tool.importimage.util;

public class IdTool {

    public static Integer node=1;
    public static final int NODE_SHIFT = 10;
    public static final int SEQ_SHIFT = 12;

    public static final short MAX_SEQUENCE = 4096;

    public static short sequence;
    public static long referenceTime;

    /**
     * Generates a k-ordered unique 64-bit integer. Subsequent invocations of this method will produce
     * increasing integer values.
     *
     * @return The next 64-bit integer.
     */
    public synchronized static long next() {

        long currentTime = System.currentTimeMillis();
        long counter;

        synchronized (IdTool.class) {

            if (currentTime < referenceTime) {
                throw new RuntimeException(String.format("Last referenceTime %s is after reference time %s", referenceTime, currentTime));
            } else if (currentTime > referenceTime) {
                sequence = 0;
            } else {
                if (sequence < MAX_SEQUENCE) {
                    sequence++;
                } else {
                    throw new RuntimeException("Sequence exhausted at " + sequence);
                }
            }
            counter = sequence;
            referenceTime = currentTime;
        }

        return currentTime << NODE_SHIFT << SEQ_SHIFT | node << SEQ_SHIFT | counter;
    }



}
