import java.io.*;

public class ScoreManager {
    private static final String FILE_NAME = "snake_scores.txt";

    public static int loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            return Integer.parseInt(reader.readLine());
        } catch (Exception e) {
            return 0;
        }
    }

    public static void saveHighScore(int score) {
        int high = loadHighScore();
        if (score > high) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
                writer.write(String.valueOf(score));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
