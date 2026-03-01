package cc.unilock.nilcord;

import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.util.regex.Pattern;

public class ChatFilter {

    private static final List<Pattern> BLOCKED_PATTERNS = new ArrayList<>();
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
            writer.println("# Plain words will be hardened automatically");
            writer.println("# Prefix with regex: to use raw regex");
            writer.println("discord.gg");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void loadFromFile() {
        BLOCKED_PATTERNS.clear();

        try (BufferedReader reader = new BufferedReader(new FileReader(filterFile))) {

            String line;

            while ((line = reader.readLine()) != null) {
                line = line.trim();

                if (line.isEmpty() || line.startsWith("#")) continue;

                try {

                    if (line.startsWith("regex:")) {
                        // Raw regex mode
                        String raw = line.substring(6);
                        BLOCKED_PATTERNS.add(Pattern.compile(raw, Pattern.CASE_INSENSITIVE));
                    } else {
                        // Word mode (auto-hardened)
                        String hardened = regexify(line.toLowerCase(Locale.ROOT));
                        BLOCKED_PATTERNS.add(Pattern.compile(hardened));
                    }

                } catch (Exception e) {
                    System.out.println("Invalid filter entry: " + line);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String regexify(String string) {

        StringBuilder neue = new StringBuilder("(?i)");
        boolean first = true;

        for (char c : string.toCharArray()) {

            if (!first)
                neue.append("[ \\.\\-_@$!#:;&\\(\\)\\-¶,\\.\\?+×÷=%/*€£￦¥¿¡^\\[\\]<>~`§μ¬Г´·\\{\\}©|¤Ωθฯ]{0,3}");

            first = false;

            switch (c) {
                case 'a': neue.append("[aäáàâåǎ]"); break;
                case 'c': neue.append("[cĉčćç]"); break;
                case 'e': neue.append("[eëéèêě]"); break;
                case 'i': neue.append("[iịǐíìîï]"); break;
                case 'j': neue.append("[jĵǰ]"); break;
                case 'm': neue.append("[mṃ]"); break;
                case 'n': neue.append("[nňṇńņ]"); break;
                case 'o': neue.append("[oöóòôǒọ]"); break;
                case 's': neue.append("[sŝšṣśş]"); break;
                case 'u': neue.append("[uüúùûůǔụ]"); break;
                default: neue.append(Pattern.quote(String.valueOf(c)));
            }
        }

        return neue.toString();
    }

    public static String normalize(String input) {

        if (input == null) return "";

        String text = input.toLowerCase(Locale.ROOT);

        text = Normalizer.normalize(text, Normalizer.Form.NFD);
        text = text.replaceAll("\\p{M}", "");

        // Kill non-English characters
        text = text.replaceAll("[^a-z0-9 ]", "");

        text = text.replaceAll("(.)\\1{2,}", "$1$1");

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
