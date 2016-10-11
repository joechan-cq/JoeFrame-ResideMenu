package joe.frame.utils;

/**
 * Description
 * Created by chenqiao on 2016/9/27.
 */
public class JsonUtils {

    public static String formatJsonString(String jsonStr) {
        StringBuilder builder = new StringBuilder();
        boolean inValue = false;
        char temp;
        int tab = 0;
        for (int i = 0; i < jsonStr.length(); i++) {
            temp = jsonStr.charAt(i);
            switch (temp) {
                case '{':
                case '[':
                    builder.append(temp).append("\n");
                    builder.append(getTabs(++tab));
                    break;
                case ',':
                    builder.append(temp);
                    if (!inValue) {
                        builder.append("\n").append(getTabs(tab));
                    }
                    break;
                case ' ':
                case '\t':
                case '\r':
                case '\n':
                    if (inValue) {
                        builder.append(temp);
                    }
                    break;
                case '"':
                    inValue = !inValue;
                    builder.append(temp);
                    break;
                case '}':
                case ']':
                    builder.append("\n").append(getTabs(--tab)).append(temp);
                    break;
                default:
                    builder.append(temp);
                    break;

            }
        }
        return builder.toString();
    }

    private static String getTabs(int tabs) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < tabs; i++) {
            builder.append("\t");
        }
        return builder.toString();
    }
}