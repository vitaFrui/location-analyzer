package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Main {
    public static Map<String, LocationSequence> LOCATION_SEQ_DUP_OCCURRENCE = new HashMap<>();
    public static List<Pattern> IGNORE_PATTERNS = new ArrayList<>();

    public static void main(final String[] args) {

        try (BufferedReader reader = new BufferedReader(new FileReader(""))) {
            List.of("last_updated").forEach(attr -> IGNORE_PATTERNS.add(Pattern.compile("\"(" + attr + ")\"" + ": \"[^\"]*\"")));

            String line = reader.readLine();
            int count = 0;

            while (line != null) {
                count++;
                processLineForDups(line);

                line = reader.readLine();
            }

            final Integer totalDups = LOCATION_SEQ_DUP_OCCURRENCE.values().stream().map(LocationSequence::getTotalDups).reduce(Integer::sum).orElse(0);

            System.out.println(count + " updates across " + LOCATION_SEQ_DUP_OCCURRENCE.keySet().size() + " locations");
            System.out.println(totalDups + " duplicates; " + (double) totalDups / count * 100 + "% rate");
            LOCATION_SEQ_DUP_OCCURRENCE.forEach((id, sequence) -> {
                System.out.println(id + ";" + sequence.getTotalDups() + "; " + sequence.getSequenceList().stream().map(String::valueOf).collect(Collectors.joining(",")));
        });
        } catch (final IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void processLineForDups(final String line) {
        String preprocessedLine = line;

        for (Pattern attr : IGNORE_PATTERNS) {
            preprocessedLine = attr.matcher(preprocessedLine).replaceAll("\"$1\" : \"IGNORED\"");
        }

        final LocationSequence sequence = new LocationSequence(preprocessedLine);

        if (!sequence.isProcessable()) {
            return;
        }

        Optional.ofNullable(LOCATION_SEQ_DUP_OCCURRENCE.get(sequence.getId())).ifPresentOrElse(
                existingSequence -> existingSequence.updateLine(sequence.getCurrent()),
                () -> LOCATION_SEQ_DUP_OCCURRENCE.put(sequence.getId(), sequence));
    }
}