import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public static char[][] readFiles(File file) throws Exception{

        List<String> lines = new ArrayList<>();

        // Read Files 
        try (BufferedReader buf = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = buf.readLine()) != null) {
                line = line.trim();
                
                // Ignore Blank
                if (!line.isBlank()){
                    lines.add(line);
                }
            }
        }

        if(lines.isEmpty()){
            throw new Exception("Empty File!");
        }

        int N = lines.size();
        char[][] board = new char[N][N];

        for (int i = 0; i < N; i ++){
            String curLine = lines.get(i);

            if (curLine.length() != N){
                throw new Exception(
                    "The input on file .txt is not valid! The board must be square.\n" +
                    "On line-" + (i+1) + " the length is " + curLine.length() +
                    ", it should be " + N 
                );
            }

            if (!curLine.matches("[A-Z]+")){
                throw new Exception("The input on file .txt contains characters non-letters or lower case letters!");
            }

            board[i] = curLine.toCharArray();
        }

        return board;
    }
}
