package edu.appstate.cs.cloud.restful;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import java.io.IOException;

import org.springframework.stereotype.Service;

@Service
public class StoryGenerator {

  private VertexAI vertexAi;

  public StoryGenerator() {
    vertexAi = new VertexAI("teamprojectmccewenseager", "us-east1");
  }

  // Passes the provided text input to the Gemini model and returns the text-only response.
  // For the specified textPrompt, the model returns a list of possible store names.
  public String generate(String textPrompt) throws IOException {

    String modifiedPrompt = """
        Your job is to create satire stories in the style of The Onion. 
        All stories should have elements of a real story which could be made by
        an official news source which can include references to fake sources, quotes, etc.
        The story should be absurd enough to be clear that it's satire, but it should be
        realistic so that someone who is gullible wouldn't know the difference. Here is the prompt
        to generate your story from: 
        """ + textPrompt;

    // Initialize client that will be used to send requests. This client only needs
    // to be created once, and can be reused for multiple requests.
    GenerativeModel model = new GenerativeModel("gemini-2.0-flash", vertexAi);

    GenerateContentResponse response = model.generateContent(modifiedPrompt);
    String output = ResponseHandler.getText(response);
    return output;
  }
}