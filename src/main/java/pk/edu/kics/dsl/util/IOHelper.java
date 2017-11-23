package pk.edu.kics.dsl.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.entity.Question;

public class IOHelper {
	
	public static ArrayList<Question> ReadQuestions(String[] paths) throws FileNotFoundException, IOException, ParseException{
		ArrayList<Question> questions = new ArrayList<Question>();
		JSONParser parser = new JSONParser();
		
		for (String path : paths) {
			JSONObject jsonFile = (JSONObject) parser.parse(new FileReader(path));
			JSONArray jsonQuestions = (JSONArray) jsonFile.get("questions");
			
			for (int i = 0; i < jsonQuestions.size(); i++) {
				JSONObject jsonQuestion = (JSONObject) jsonQuestions.get(i);
				Question question = new Question();
				
				question.id = jsonQuestion.get("id").toString();
				question.body = jsonQuestion.get("body").toString().replaceAll("\\?", "");
				question.type = jsonQuestion.get("type").toString();
				
				questions.add(question);
			}
		}
		
		return questions;
	}
	
}
