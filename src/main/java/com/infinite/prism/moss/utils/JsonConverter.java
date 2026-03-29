package com.infinite.prism.moss.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

public class JsonConverter {

    private static final ObjectMapper MAPPER = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT); // 可选，美化输出

    /**
     * 将输入字符串转换为有效的JSON字符串。
     * 如果输入是包含Markdown代码块的JSON，则提取并重新序列化。
     * 如果输入是普通文本，则将其转义为JSON字符串值。
     *
     * @param input 原始字符串（可能包含```json标记）
     * @return 一个JSON字符串（可能为JSON对象或字符串值）
     */
    public static String convertToJsonString(String input) {
        if (input == null || input.trim().isEmpty()) {
            return "null"; // 或抛出异常，根据需求
        }

        // 1. 去除可能的Markdown代码块标记
        String cleaned = cleanMarkdownCodeBlock(input);

        // 2. 尝试解析为JSON对象/数组
        try {
            JsonNode jsonNode = MAPPER.readTree(cleaned);
            // 如果解析成功，说明输入本身就是JSON，直接重新序列化（确保格式正确）
            return MAPPER.writeValueAsString(jsonNode);
        } catch (JsonProcessingException e) {
            // 解析失败，说明输入不是合法JSON，将其作为普通字符串转义
            return escapeAsJsonString(cleaned);
        }
    }

    /**
     * 将普通文本转义为JSON字符串值（加上双引号并转义内部特殊字符）。
     */
    private static String escapeAsJsonString(String text) {
        try {
            // 使用Jackson将字符串序列化为JSON值（会自动转义）
            return MAPPER.writeValueAsString(text);
        } catch (JsonProcessingException e) {
            // 理论上不会发生，但若发生则返回最原始的转义（降级）
            return "\"" + text.replace("\\", "\\\\").replace("\"", "\\\"") + "\"";
        }
    }

    /**
     * 清理Markdown代码块标记，例如：
     * 输入：```json\n{"key":"value"}\n```
     * 输出：{"key":"value"}
     */
    private static String cleanMarkdownCodeBlock(String input) {
        String trimmed = input.trim();
        // 匹配以 ```json 开头，可能后面有换行，最后以 ``` 结尾
        if (trimmed.startsWith("```json")) {
            // 去掉开头的 ```json 和换行
            int start = trimmed.indexOf('\n');
            if (start != -1) {
                String afterFirstLine = trimmed.substring(start + 1);
                // 去掉结尾的 ```
                if (afterFirstLine.endsWith("```")) {
                    return afterFirstLine.substring(0, afterFirstLine.length() - 3).trim();
                } else {
                    // 可能没有结尾标记，直接返回
                    return afterFirstLine.trim();
                }
            } else {
                // 只有 ```json 没有换行，可能格式错误，直接去除前缀
                return trimmed.substring(6).trim();
            }
        }
        // 如果不是以 ```json 开头，则返回原字符串
        return trimmed;
    }

    // 测试
    public static void main(String[] args) {
        // 你的原始输入（包含Markdown标记）
        String rawInput = "```json\n" +
                "{\n" +
                "    \"answer\": \"Halo Leo! Aku akan menjelaskan sifat-sifat teman barumu ini dengan bahasa yang mudah dimengerti ya. Dia adalah seorang pria dari Asia Tenggara yang usianya mirip denganmu. Mari kita lihat satu per satu karakteristiknya:\",\n" +
                "    \"personality\": {\n" +
                "        \"content\": \"ramah, pekerja keras\",\n" +
                "        \"reason\": \"Dia sangat ramah karena sejak kecil tinggal di lingkungan kampung yang hangat di Jawa Tengah, di mana semua tetangga saling menyapa dan membantu. Ibunya selalu mengajarkan untuk tersenyum pada orang baru, sehingga sekarang dia mudah berteman dengan siapa saja. Sifat pekerja kerasnya muncul karena ayahnya adalah pedagang keliling yang bangun jam 4 pagi setiap hari - dia melihat langsung bagaimana kerja keras bisa menghidupi keluarga. Saat SMA, dia juga membantu orang tuanya berjualan setelah sekolah, jadi terbiasa tidak mudah menyerah meskipun lelah.\"\n" +
                "    },\n" +
                "    \"wealth\": {\n" +
                "        \"content\": \"cukup\",\n" +
                "        \"reason\": \"Keuangan keluarganya cukup stabil karena ayahnya sekarang sudah memiliki warung kecil di pasar tradisional yang lumayan ramai pembelinya. Meskipun bukan keluarga kaya, mereka bisa memenuhi kebutuhan sehari-hari dengan baik - bisa makan tiga kali sehari, anak-anaknya sekolah sampai SMA, dan bahkan punya tabungan kecil untuk keadaan darurat. Ibunya juga membantu dengan membuat kue untuk dijual di warung, jadi ada dua sumber penghasilan. Mereka hidup sederhana tapi tidak kekurangan, bisa membeli kebutuhan pokok tanpa kesulitan berarti.\"\n" +
                "    },\n" +
                "    \"weakness\": {\n" +
                "        \"content\": \"tidak sabar\",\n" +
                "        \"reason\": \"Sifat tidak sabar ini muncul karena dia terbiasa melihat ayahnya yang selalu bergerak cepat dalam berdagang - harus melayani banyak pembeli dalam waktu bersamaan. Sejak kecil dia diajari bahwa 'waktu adalah uang', sehingga sekarang cenderung ingin segala sesuatu selesai dengan cepat. Contohnya saat mengerjakan tugas kelompok, dia sering merasa kesal jika temannya lambat memahami penjelasan. Kadang dia juga sulit menunggu antrian panjang, lebih memilih mencari tempat lain yang lebih cepat. Ini bukan berarti dia orang jahat, hanya perlu belajar bahwa beberapa hal butuh proses dan kesabaran.\"\n" +
                "    },\n" +
                "    \"meeting_scene\": {\n" +
                "        \"content\": \"taman\",\n" +
                "        \"reason\": \"Kalian akan bertemu di taman kota pada sore hari yang cerah. Dia sedang duduk di bangku sambil membaca buku tentang bisnis kecil-kecilan, karena dia ingin mengembangkan warung orang tuanya. Kebetulan kamu lewat dan melihat bukunya menarik, lalu memulai percakapan tentang isi buku tersebut. Taman ini adalah tempat favoritnya untuk menghabiskan waktu setelah membantu orang tuanya di warung, karena udaranya sejuk dan banyak pepohonan. Dia datang ke taman hampir setiap sore untuk refreshing sekaligus belajar hal baru.\"\n" +
                "    },\n" +
                "    \"conflict\": {\n" +
                "        \"content\": \"25%\",\n" +
                "        \"reason\": \"Kemungkinan konflik antara kalian cukup rendah, sekitar 25%. Ini karena dia pada dasarnya orang yang ramah dan terbuka. Namun kadang-kadang mungkin ada perbedaan pendapat karena sifat tidak sabarnya - misalnya saat merencanakan sesuatu, dia ingin langsung eksekusi sementara kamu mungkin ingin pertimbangkan lebih matang. Tapi dia bukan tipe orang yang suka bertengkar, lebih memilih diskusi dengan kepala dingin. Konflik besar kemungkinan kecil terjadi karena dia menghargai persahabatan dan belajar dari pengalaman bahwa pertengkaran hanya merugikan semua pihak.\"\n" +
                "    },\n" +
                "    \"cheating_risk\": {\n" +
                "        \"content\": \"15%\",\n" +
                "        \"reason\": \"Risiko dia berselingkuh sangat rendah, hanya 15%. Keluarganya sangat menjunjung tinggi nilai kesetiaan - orang tuanya sudah menikah 25 tahun dan tetap harmonis. Dia melihat langsung bagaimana ayah ibunya saling mendukung meskipun hidup sederhana. Dalam pergaulan, dia lebih memilih pertemanan yang tulus daripada banyak teman tapi tidak berarti. Jika sudah berkomitmen pada suatu hubungan, dia akan berusaha menjaga kepercayaan yang diberikan. Namun seperti manusia biasa, godaan pasti ada, tapi nilai-nilai keluarga yang kuat akan membimbingnya membuat keputusan yang benar.\"\n" +
                "    }\n" +
                "}\n" +
                "```";

        String result = convertToJsonString(rawInput);
        System.out.println(result);
    }
}