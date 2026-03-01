package cc.unilock.nilcord;

import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class ChatFilter {

    private static final List<Pattern> BLOCKED_PATTERNS = new ArrayList<Pattern>();
    private static File filterFile;

    public static void init(File configDir) {
        filterFile = new File(configDir, "nilcord_chat_filter.txt");

        if (!filterFile.exists()) {
            createDefaultFile();
        }

        loadFromFile();
    }

    private static void createDefaultFile() {
        try {
            filterFile.createNewFile();
            PrintWriter writer = new PrintWriter(new FileWriter(filterFile));
            writer.println("# Nilcord Chat Filter");
            writer.println("# One regex per line");
            writer.println("discord\\.gg");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile() {
        BLOCKED_PATTERNS.clear();

        try {
            BufferedReader reader = new BufferedReader(new FileReader(filterFile));
            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                try {
                    BLOCKED_PATTERNS.add(Pattern.compile(line, Pattern.CASE_INSENSITIVE));
                } catch (Exception e) {
                    System.out.println("Invalid regex in filter: " + line);
                }
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String normalize(String input) {

        if (input == null) return "";

        // Convert to lowercase
        String text = input.toLowerCase(Locale.ROOT);

        // Remove accents (BANNED FROM GITHUB FOR THIS POST)
        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{M}", "");

        // Remove non-alphanumeric except spaces
        text = text.replaceAll("[^a-z0-9 ]", "");

        // Collapse repeated letters (USER WAS REMOVED FOR THIS POST)
        text = text.replaceAll("(.)\\1{2,}", "$1$1");

        //NO MORE NON ENGLISH BULLSHIT
        //text = text.replaceAll("[^a-z0-9 ]", "");
        //oh wait it already did this lel

        return text;
    }
    public static boolean isBlocked(String message) {

        String clean = normalize(message);

        for (Pattern pattern : BLOCKED_PATTERNS) {
            if (pattern.matcher(clean).find()) {
                return true;
            }
        }

        return false;
    }
}
