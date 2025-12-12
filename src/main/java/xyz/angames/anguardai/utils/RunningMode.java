package xyz.angames.anguardai.utils;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Map;

public class RunningMode {
    private final Deque<Double> samples = new ArrayDeque<>();
    private final int limit;

    public RunningMode(int limit) {
        this.limit = limit;
    }

    public void add(double number) {
        double rounded = Math.round(number * 10000.0) / 10000.0;
        samples.add(rounded);
        if (samples.size() > limit) {
            samples.removeFirst();
        }
    }

    public int size() {
        return samples.size();
    }

    public Pair<Double, Integer> getMode() {
        Map<Double, Integer> frequencies = new HashMap<>();
        double bestMode = -1.0;
        int maxCount = 0;

        for (double val : samples) {
            int count = frequencies.getOrDefault(val, 0) + 1;
            frequencies.put(val, count);

            if (count > maxCount) {
                maxCount = count;
                bestMode = val;
            }
        }
        return new Pair<>(bestMode, maxCount);
    }
}