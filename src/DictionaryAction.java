import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DictionaryAction {

    private volatile List<DictionaryModel> Data;
    private File file = new File("data.json");

    public DictionaryAction() {
        Data = new ArrayList<>();
        this.getDataFromFile();
    }

    public DictionaryModel getDictionary(String word) {
        for (DictionaryModel dict : Data) {
            if (dict.getVocabulary().equalsIgnoreCase(word)) return dict;
        }
        return null;
    }

    public Boolean addDictionary(DictionaryModel dict) {
    	System.out.println(dict.getVocabulary() + " ");
        DictionaryModel exist = this.getDictionary(dict.getVocabulary());
        if (exist != null) return false;
        this.Data.add(dict);
        this.saveDataToFile();
        return true;
    }

    public Boolean deleteDictionary(String word) {
        DictionaryModel dict = this.getDictionary(word);
        if (dict == null) return false;
        this.Data.remove(dict);
        this.saveDataToFile();
        return true;
    }

    private void getDataFromFile() {
        try {
            if (!file.exists()) file.createNewFile();
            if (file.length() == 0) return;

            ObjectMapper mapper = new ObjectMapper();
            JsonNode node = mapper.readValue(file, JsonNode.class);
            this.Data = mapper.convertValue(node, new TypeReference<List<DictionaryModel>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveDataToFile() {
        try{
            if(file.exists()) file.delete();
            file.createNewFile();

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(file, this.Data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}