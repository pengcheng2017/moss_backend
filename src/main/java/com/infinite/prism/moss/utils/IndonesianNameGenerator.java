package com.infinite.prism.moss.utils;

import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class IndonesianNameGenerator {

    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH = 12;
    private static final int DEFAULT_COUNT = 10;

    private static final String[] FEMALE_PREFIXES = {
            "Ayu", "Ani", "Adi", "Ami", "Anti", "Arum", "Asta", "Ayu", "Bella", "Budi",
            "Citra", "Cici", "Dewi", "Diah", "Dina", "Dita", "Eka", "Ella", "Eris", "Esti",
            "Fitri", "Gita", "Hani", "Hilda", "Ika", "Indah", "Ira", "Irma", "Jasmine", "Kartika",
            "Lina", "Lulu", "Maya", "Mia", "Nadia", "Nisa", "Nita", "Nurul", "Putri", "Puspita",
            "Rani", "Rina", "Risna", "Rita", "Sari", "Siti", "Sinta", "Sri", "Susan", "Tika",
            "Titi", "Toni", "Uma", "Vina", "Wati", "Yanti", "Yuni", "Yusuf", "Zahra", "Zulaika"
    };

    private static final String[] MALE_PREFIXES = {
            "Adi", "Agus", "Ahmad", "Andi", "Anwar", "Ari", "Budi", "Dedi", "Dicky", "Doni",
            "Eko", "Fajar", "Feri", "Gede", "Hadi", "Hendra", "Heri", "Iwan", "Joko", "Joni",
            "Kurnia", "Lukman", "Made", "Mahendra", "Nico", "Nugroho", "Putra", "Raden", "Rahmat",
            "Rendra", "Ricky", "Rizal", "Rizki", "Sandi", "Sigit", "Steven", "Sulaeman", "Tomi",
            "Tony", "Wahyu", "Wawan", "Wibi", "Yogi", "Yusuf", "Zainal", "Zaki"
    };

    private static final String[] COMMON_SUFFIXES = {
            "a", "i", "u", "e", "o", "an", "in", "un", "en", "ang", "ung", "ong",
            "awan", "iwan", "owan", "wati", "outi", "hati", "sari", "puri", "dewi", "ayu",
            "cantika", "princess", "shine", "star", "bright", "flower", "butterfly"
    };

    private static final String[] MEANINGFUL_WORDS = {
            "bunga", "cantik", "senang", "bahagia", "indah", "ceria", "bersinar",
            "harum", "manis", "sejuk", "hangat", "damai", "selamat", "berkah",
            "suci", "mulia", "jaya", "sukses", "bersyukur", "cinta", "sayang",
            "persahabatan", "keadilan", "kebijaksanaan", "keberanian", "perseverance"
    };

    private static final String[] NEUTRAL_WORDS = {
            "angkasa", "bintang", "matahari", "bulan", " Laut", "gunung", "hutan",
            "safira", "topaz", "amber", "crystal", "diamond", "emerald", "gold",
            "silver", "platinum", "rainbow", "sunset", "sunrise", "dream", "hope",
            "faith", "love", "peace", "joy", "grace", "blessing", "miracle"
    };

    private static final Set<String> SENSITIVE_WORDS = new HashSet<>(Arrays.asList(
            "sex", "porn", "fuck", "shit", "damn", "bitch", "ass", "hell", "crap",
            "drug", "kill", "death", "murder", "hate", "war", "bomb", "terror",
            "gay", "lesbian", "bisex", "orgy", "xxx", "18+", "adult"
    ));

    private final Random random = new Random();

    public List<String> generateNames(int count) {
        return generateNames(count, null);
    }

    public List<String> generateNames(int count, String gender) {
        List<String> names = new ArrayList<>();
        Set<String> uniqueNames = new HashSet<>();

        while (uniqueNames.size() < count) {
            String name = generateSingleName(gender);
            if (isValidName(name) && !uniqueNames.contains(name)) {
                uniqueNames.add(name);
            }
        }

        names.addAll(uniqueNames);
        return names;
    }

    public String generateSingleName(String gender) {
        String prefix;
        if (gender != null && gender.equalsIgnoreCase("male")) {
            prefix = MALE_PREFIXES[random.nextInt(MALE_PREFIXES.length)];
        } else if (gender != null && gender.equalsIgnoreCase("female")) {
            prefix = FEMALE_PREFIXES[random.nextInt(FEMALE_PREFIXES.length)];
        } else {
            prefix = random.nextBoolean() ?
                    FEMALE_PREFIXES[random.nextInt(FEMALE_PREFIXES.length)] :
                    MALE_PREFIXES[random.nextInt(MALE_PREFIXES.length)];
        }

        int nameType = random.nextInt(4);
        String suffix;

        switch (nameType) {
            case 0:
                suffix = COMMON_SUFFIXES[random.nextInt(COMMON_SUFFIXES.length)];
                break;
            case 1:
                suffix = MEANINGFUL_WORDS[random.nextInt(MEANINGFUL_WORDS.length)];
                break;
            case 2:
                suffix = NEUTRAL_WORDS[random.nextInt(NEUTRAL_WORDS.length)];
                break;
            default:
                suffix = "";
        }

        String name = prefix + capitalizeFirst(suffix);
        if (name.length() > MAX_LENGTH) {
            name = prefix.substring(0, Math.min(prefix.length(), MAX_LENGTH - 2));
            name = name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
        }

        return name;
    }

    private String capitalizeFirst(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1).toLowerCase();
    }

    private boolean isValidName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        int length = name.length();
        if (length < MIN_LENGTH || length > MAX_LENGTH) {
            return false;
        }

        String lowerName = name.toLowerCase();
        for (String sensitive : SENSITIVE_WORDS) {
            if (lowerName.contains(sensitive.toLowerCase())) {
                return false;
            }
        }

        if (!name.matches("^[a-zA-Z][a-zA-Z0-9]*$")) {
            return false;
        }

        return true;
    }

    public boolean isNameAvailable(String name) {
        return isValidName(name);
    }

    public String generateDefaultUsername(String email) {
        if (email == null || !email.contains("@")) {
            return generateSingleName(null);
        }

        String prefix = email.substring(0, email.indexOf("@"));
        prefix = prefix.replaceAll("[^a-zA-Z0-9]", "");

        if (prefix.length() < MIN_LENGTH) {
            String suffix = COMMON_SUFFIXES[random.nextInt(COMMON_SUFFIXES.length)];
            prefix = prefix + suffix;
        }

        if (prefix.length() > MAX_LENGTH) {
            prefix = prefix.substring(0, MAX_LENGTH);
        }

        return prefix.substring(0, 1).toUpperCase() + prefix.substring(1).toLowerCase();
    }
}
