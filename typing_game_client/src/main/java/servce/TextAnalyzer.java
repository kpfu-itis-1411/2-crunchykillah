package servce;

public class TextAnalyzer {
    private String originalText;
    private String userInput;

    public TextAnalyzer(String originalText, String userInput) {
        this.originalText = originalText;
        this.userInput = userInput;
    }

    public double getCorrectness() {
        String[] originalWords = originalText.split("\\s+");
        String[] userInputWords = userInput.split("\\s+");

        int minLen = Math.min(originalWords.length, userInputWords.length);
        int sameCharCount = 0;
        int totalCharCount = 0;
        for (int i = 0; i < minLen; i++) {
            String originalWord = originalWords[i];
            String userInputWord = userInputWords[i];
            for (int j = 0; j < Math.min(originalWord.length(), userInputWord.length()); j++) {
                if (originalWord.charAt(j) == userInputWord.charAt(j)) {
                    sameCharCount++;
                }
                totalCharCount++;
            }
        }
        return (double) sameCharCount / totalCharCount * 100;
    }

    public int getSpeed() {
        return userInput.length() / 30 * 60;
    }
}