package animals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

public class FileHandle {
    // Default file name without its extension
    public static String fileName = "animals";
    // Default mapper
    public static ObjectMapper mapper = new JsonMapper();

    public static void configure(String[] args) {
        // If the parameter is not specified, "json" is the default format.
        if (args.length == 0) {
            fileName = fileName + ".json";
            return;
        }

        // Adding the correct extension based on the user given parameter
        String cmd = Arrays.toString(args);
        if (cmd.contains("json")) {
            fileName = fileName + ".json";
        } else if (cmd.contains("xml")) {
            fileName = fileName + ".xml";
            mapper = new XmlMapper();
        } else if (cmd.contains("yaml")) {
            fileName = fileName + ".yaml";
            mapper = new YAMLMapper();
        }
    }

    // Saving the current tree into the memory
    public static void saveTree(BST.Node root) {
        try {
            File file = new File(fileName);
            file.createNewFile();
            mapper.writerWithDefaultPrettyPrinter()
                    .writeValue(file, root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Loading the knowledge tree saved in the memory from the previous session
    public static BST.Node loadTree() {
        File file = new File(fileName);
        if (file.exists()) {
            try {
                return mapper.readValue(file, BST.Node.class);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
