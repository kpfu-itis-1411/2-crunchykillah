package service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RussianWordsExtractor {
    public static void main(String[] args) {
        String inputFilePath = "C:\\crunchy_typing_game\\typing_game_client\\src\\main\\resources\\100000-russian-words.txt";
        String outputFilePath = "C:\\crunchy_typing_game\\typing_game_client\\src\\main\\resources\\russian.txt";

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFilePath), StandardCharsets.UTF_8));
             PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFilePath), StandardCharsets.UTF_16))) {

            String line;
            Pattern pattern = Pattern.compile("[а-яА-ЯёЁ]+");
            Set<String> uniqueWords = new HashSet<>();

            while ((line = reader.readLine()) != null) {
                Matcher matcher = pattern.matcher(line);
                while (matcher.find()) {
                    uniqueWords.add(matcher.group());
                }
            }

            for (String word : uniqueWords) {
                writer.println(word);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
