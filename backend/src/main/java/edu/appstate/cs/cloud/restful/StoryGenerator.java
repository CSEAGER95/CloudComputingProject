package edu.appstate.cs.cloud.restful;

import com.google.cloud.vertexai.VertexAI;
import com.google.cloud.vertexai.api.GenerateContentResponse;
import com.google.cloud.vertexai.generativeai.GenerativeModel;
import com.google.cloud.vertexai.generativeai.ResponseHandler;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class StoryGenerator {

  private static final Logger logger = LoggerFactory.getLogger(StoryGenerator.class);
  private VertexAI vertexAi;
  private boolean aiInitialized = false;

  public StoryGenerator() {
    try {
      vertexAi = new VertexAI("teamprojectmccewenseager", "us-east1");
      aiInitialized = true;
      logger.info("VertexAI initialized successfully");
    } catch (Exception e) {
      logger.error("Failed to initialize VertexAI: {}", e.getMessage(), e);
      // Initialize anyway to prevent NPE but mark as not initialized
      aiInitialized = false;
    }
  }

  // Passes the provided text input to the Gemini model and returns the text-only response.
  // For the specified textPrompt, the model returns a list of possible store names.
  public String generate(String textPrompt) throws IOException {
    if (!aiInitialized) {
      logger.warn("VertexAI not properly initialized, returning fallback response");
      return generateFallbackStory(textPrompt);
    }

    try {
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
      logger.info("Successfully generated story from prompt: {}", textPrompt.substring(0, Math.min(50, textPrompt.length())));
      return output;
    } catch (Exception e) {
      logger.error("Error generating story: {}", e.getMessage(), e);
      return generateFallbackStory(textPrompt);
    }
  }
  
  private String generateFallbackStory(String prompt) {
    // Create a basic fallback story when AI generation fails
    String headline = prompt.length() > 50 ? prompt.substring(0, 50) + "..." : prompt;
    
    return "SATIRE NEWS (Fallback Mode)\n\n" +
           "HEADLINE: " + headline + "\n\n" +
           "In what experts are calling an 'unprecedented development,' local citizens expressed " +
           "shock and awe at the recent turn of events. \"I never thought I'd see the day,\" said " +
           "community member Jane Smith, struggling to find words.\n\n" +
           "Officials declined to comment directly, but anonymous sources within the administration " +
           "suggested that 'this is just the beginning.' Markets reacted predictably, with analysts " +
           "forecasting either doom or celebration, depending on who was asked.\n\n" +
           "[Note: This is a fallback story generated due to AI service unavailability. " +
           "Please check Google Cloud Vertex AI service configuration.]";
  }
}