package org.example;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LocationSequence {
    private static final Pattern PREPROCESS_PATTERN = Pattern.compile("(?:.|\\n)*?_source\": (\\{\\n(?:.|\\n)*?)(?=\"sort\")");
    private static final Pattern ID_PATTERN = Pattern.compile("\"id\": \"([a-zA-Z0-9]{8,32})\"");

    private final String id;
    private String current;
    private Integer totalDups = 0;
    private final List<Integer> sequenceList = new ArrayList<>();

    public LocationSequence(final String line) {
        current = line;
        id = extractId(line);
        sequenceList.add(1);
    }

    public void updateLine(final String line) {
        if (current.equals(line)) {
            totalDups++;

            final Integer currentSequence = sequenceList.get(sequenceList.size() - 1);
            sequenceList.set(sequenceList.size() - 1, currentSequence + 1);
        } else {
            sequenceList.add(1);
        }

        current = line;
    }

    public boolean isProcessable() {
        return null != id;
    }

    public String getId() {
        return id;
    }

    public String getCurrent() {
        return current;
    }

    public Integer getTotalDups() {
        return totalDups;
    }

    public List<Integer> getSequenceList() {
        return sequenceList;
    }

    private static String extractId(final String line) {
        final Matcher idMatcher = ID_PATTERN.matcher(line);

        if (idMatcher.find()) {
            return idMatcher.group(1);
        }

        return null;
    }
}
