package protocol;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Protocol {
    public static final int REGISTER = 0;
    public static final int OK_REG = 1;
    public static final int ERR_REG = 2;
    public static final int LOGIN = 3;
    public static final int OK_LOG = 4;
    public static final int ERR_LOG = 5;
    public static final int START_GAME = 6;
    public static final int WORDS = 7;
    public static final int SUBMIT_RESULT = 8;
    public static final int OK_RES = 9;
    public static final int REQUEST_RANKING = 10;
    public static final int RATING = 11;
    public static final int CLOSE_GAME = 12;
    public static final int NEW_DATA = 13;

    public byte[] createCommand(int commandType, String... args) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        System.out.println(commandType + " create command");
        output.write(commandType);
        for (String arg : args) {
            output.write(arg.getBytes(Charset.forName("UTF-8")));
            output.write(0);  // Null byte to separate arguments
        }
        output.write('\n');  // New line at the end of the command
        return output.toByteArray();
    }

    public Map<Integer, List<String>> parseCommand(byte[] command) throws IOException {
        try (ByteArrayInputStream input = new ByteArrayInputStream(command)) {
            int commandType = input.read();
            if (commandType < 0 || commandType > 13) {
                throw new IllegalArgumentException("Unknown command type: " + commandType);
            }
            List<String> args = new ArrayList<>();
            ByteArrayOutputStream argOutput = new ByteArrayOutputStream();
            int b;
            while ((b = input.read()) != -1) {
                if (b == 0) {
                    args.add(argOutput.toString(Charset.forName("UTF-8")));
                    argOutput.reset();
                } else if (b == '\n') {
                    break;
                } else {
                    argOutput.write(b);
                }
            }
            if (argOutput.size() > 0) {
                args.add(argOutput.toString(Charset.forName("UTF-8")));
            }
            Map<Integer, List<String>> result = new HashMap<>();
            result.put(commandType, args);
            return result;
        }
    }
}
