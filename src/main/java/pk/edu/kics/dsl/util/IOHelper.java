package pk.edu.kics.dsl.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import pk.edu.kics.dsl.entity.InputQuestion;
import pk.edu.kics.dsl.entity.SolrResult;

public class IOHelper {

	public static ArrayList<InputQuestion> ReadQuestions(String[] paths)
			throws FileNotFoundException, IOException, ParseException {
		ArrayList<InputQuestion> questions = new ArrayList<InputQuestion>();
		JSONParser parser = new JSONParser();

		for (String path : paths) {
			JSONObject jsonFile = (JSONObject) parser.parse(new FileReader(path));
			JSONArray jsonQuestions = (JSONArray) jsonFile.get("questions");

			for (int i = 0; i < jsonQuestions.size(); i++) {
				JSONObject jsonQuestion = (JSONObject) jsonQuestions.get(i);
				InputQuestion question = new InputQuestion();

				question.id = jsonQuestion.get("id").toString();
				question.body = jsonQuestion.get("body").toString().replaceAll("\\?", "");
				question.type = jsonQuestion.get("type").toString();

				questions.add(question);
			}
		}

		return questions;
	}

	public static void writeResults(String system_file, String results) throws IOException {
		BufferedWriter writer;

		File f = new File(system_file);

		if (f.exists()) {
			f.delete();
		}

		writer = new BufferedWriter(new FileWriter(system_file));
		writer.write(results);

		writer.flush();
		writer.close();

		System.out.println("Done!");
	}
}
