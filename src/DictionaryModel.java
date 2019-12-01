import java.util.List;

public class DictionaryModel 
{
	private String vocabulary;
	private List<String> meaning;
	
	public DictionaryModel() 
	{
	}
	
	public String getVocabulary()
	{
		return this.vocabulary;
	}
	
	public void setVocabulary(String vocabulary)
	{
		this.vocabulary = vocabulary;
	}
	
	public List<String> getMeaning()
	{
		return this.meaning;
	}
	
	public void setMeaning(List<String> meaning)
	{
		this.meaning = meaning;
	}	
}
