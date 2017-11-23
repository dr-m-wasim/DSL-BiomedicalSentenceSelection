package pk.edu.kics.dsl.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.json.simple.parser.JSONParser;

public class HttpHelper {
	public static String getResponse(String urlLink, String parameters) throws IOException {
		byte[] postData = parameters.getBytes( StandardCharsets.UTF_8 );
		int postDataLength = postData.length;
		URL url = new URL(urlLink);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setDoOutput( true );
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		conn.setRequestProperty( "charset", "utf-8");
		conn.setRequestProperty( "Content-Length", Integer.toString( postDataLength ));
		conn.setUseCaches( false );
		conn.setRequestProperty("Accept", "application/json");
		try( DataOutputStream wr = new DataOutputStream( conn.getOutputStream())) {
			wr.write( postData );
		}

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		JSONParser parser = new JSONParser();

		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}

		return sb.toString();
	}
}
