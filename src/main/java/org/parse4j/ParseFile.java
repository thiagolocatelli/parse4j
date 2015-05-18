package org.parse4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;
import org.parse4j.callback.GetDataCallback;
import org.parse4j.callback.ProgressCallback;
import org.parse4j.callback.SaveCallback;
import org.parse4j.command.ParseResponse;
import org.parse4j.command.ParseUploadCommand;
import org.parse4j.util.MimeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("deprecation")
public class ParseFile {	
	
	private static Logger LOGGER = LoggerFactory.getLogger(ParseFile.class);

	private String endPoint;
	private boolean uplodated = false;
	private boolean dirty = false;
	private String name = null;
	private String url = null;
	private String contentType = null;
	byte[] data;

	public ParseFile(String name, byte[] data, String contentType) {
		if (data.length > ParseConstants.MAX_PARSE_FILE_SIZE) {
			LOGGER.error(String.format(
							"ParseFile must be less than %i bytes, current %i",
							new Object[] {
									Integer.valueOf(ParseConstants.MAX_PARSE_FILE_SIZE),
									data.length }));
			throw new IllegalArgumentException(
					String.format(
							"ParseFile must be less than %i bytes, current %i",
							new Object[] {
									Integer.valueOf(ParseConstants.MAX_PARSE_FILE_SIZE),
									data.length }));
		}

		this.endPoint = "files/" + name;
		this.name = name;
		this.data = data;
		this.contentType = contentType;
		this.dirty = true;
	}

	public ParseFile(byte[] data) {
		this(null, data, null);
	}

	public ParseFile(String name, byte[] data) {
		this(name, data, null);
	}

	public ParseFile(byte[] data, String contentType) {
		this(null, data, contentType);
	}
	
	public ParseFile(String name, String url) {
		this.name = name;
		this.url = url;
	}

	public boolean isDirty() {
		return dirty;
	}

	public void setDirty(boolean dirty) {
		this.dirty = dirty;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public void setData(byte[] data) {
		this.data = data;
	}
	
	protected String getEndPoint() {
		return this.endPoint;
	}
	
	public boolean isUploaded() {
		return uplodated;
	}

	public void save() throws ParseException {
		save(null, null);
	}

	public void save(SaveCallback saveCallback) throws ParseException {
		save(saveCallback, null);
	}

	public void save(ProgressCallback progressCallback) throws ParseException {
		save(null, progressCallback);
	}

	public void save(SaveCallback saveCallback,
			ProgressCallback progressCallback) throws ParseException {
		
		if(!isDirty() || data == null) return;
		
		ParseUploadCommand command = new ParseUploadCommand(getEndPoint());
		command.setProgressCallback(progressCallback);
		command.setData(data);
		if(getContentType() == null) {
			String fileExtension = MimeType.getFileExtension(getName());
			contentType = MimeType.getMimeType(fileExtension);
			command.setContentType(contentType);
		}
		else {
			command.setContentType(getContentType());
		}
		ParseResponse response = command.perform();
		if(!response.isFailed()) {
			JSONObject jsonResponse = response.getJsonObject();
			if (jsonResponse == null) {
				LOGGER.error("Empty response.");
				throw response.getException();
			}
			LOGGER.info(jsonResponse.toString());
			
			this.name = jsonResponse.getString("name");
			this.url = jsonResponse.getString("url");
			this.dirty = false;
			this.uplodated = true;
			
		}
		else {
			LOGGER.error("Request failed.");
			throw response.getException();
		}

	}

	public void saveInBackground() {
		saveInBackground(null, null);
	}

	public void saveInBackground(SaveCallback saveCallback) {
		saveInBackground(saveCallback, null);
	}

	public void saveInBackground(ProgressCallback progressCallback) {
		saveInBackground(null, progressCallback);
	}

	public void saveInBackground(SaveCallback saveCallback,
			ProgressCallback progressCallback) {

		SaveInBackgroundThread task = new SaveInBackgroundThread(saveCallback,
				progressCallback);
		ParseExecutor.runInBackground(task);

	}
	
	public byte[] getData() throws ParseException {
		
		HttpGet get = new HttpGet(url);
		HttpClient client = new DefaultHttpClient();
		
		HttpResponse response;
		try {
			response = client.execute(get);
			int totalSize = -1;
			Header[] contentLengthHeader = response
					.getHeaders("Content-Length");
			if (contentLengthHeader.length > 0) {
				totalSize = Integer.parseInt(contentLengthHeader[0].getValue());
	             LOGGER.debug("File size: {}", totalSize);
			}
			int downloadedSize = 0;
            InputStream responseStream = response.getEntity().getContent();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			byte[] data = new byte[32768];
            int nRead;
            while ((nRead = responseStream.read(data, 0, data.length)) != -1) {
              buffer.write(data, 0, nRead);
              downloadedSize += nRead;
              LOGGER.debug("Downloaded: {}", downloadedSize);
            }
            
            responseStream.close();
            buffer.close();
            return buffer.toByteArray();			
		}
		catch(ClientProtocolException cpe) {
			throw new ParseException(100, "bad protocol: " + cpe.getClass().getName() + ": " + cpe.getMessage());
		} catch (IOException e) {
			throw new ParseException(100, "i/o failure: " + e.getClass().getName() + ": " + e.getMessage());
		}
		finally {
			//client.close();
		}
		
	}
	
	public void getData(GetDataCallback dataCallback) throws ParseException {
		
		try {
			byte[] result = getData();
			dataCallback.done(result, null);
		}
		catch(ParseException pe) {
			dataCallback.done(null, pe);
		}
		
	}	

	public void getDataInBackground() {
		getDataInBackground(null);
	}	

	public void getDataInBackground(GetDataCallback dataCallback) {
		GetDataInBackgroundThread task = new GetDataInBackgroundThread(this.data, dataCallback);
		ParseExecutor.runInBackground(task);
	}

	class SaveInBackgroundThread extends Thread {
		SaveCallback saveCallback;
		ProgressCallback progressCallback;

		public SaveInBackgroundThread(SaveCallback saveCallback,
				ProgressCallback progressCallback) {
			this.saveCallback = saveCallback;
			this.progressCallback = progressCallback;
		}

		public void run() {
			ParseException exception = null;
			try {
				save(saveCallback, progressCallback);
			} catch (ParseException e) {
				LOGGER.debug("Request failed {}", e.getMessage());
				exception = e;
			}
			if (saveCallback != null) {
				saveCallback.done(exception);
			}
		}
	}
	
	class GetDataInBackgroundThread extends Thread {
		GetDataCallback getDataCallback;
		byte[] data;

		public GetDataInBackgroundThread(byte[] data, GetDataCallback getDataCallback) {
			this.getDataCallback = getDataCallback;
			this.data = data;
		}

		public void run() {
			ParseException exception = null;
			try {
				getData(getDataCallback);
			} catch (ParseException e) {
				LOGGER.debug("Request failed {}", e.getMessage());
				exception = e;
			}
			if (getDataCallback != null) {
				getDataCallback.done(data, exception);
			}			
		}
	}	

}
