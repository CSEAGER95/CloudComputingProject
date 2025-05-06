package edu.appstate.cs.cloud.restful;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Template-based story generator that creates satirical news stories
 * without requiring external AI services.
 */
@Service
public class StoryGenerator {
    private static final Logger logger = LoggerFactory.getLogger(StoryGenerator.class);
    private final Random random = new Random();
    
    // Template collections for story generation
    private final String[] HEADLINES = {
        "Local %s Declared 'Too Successful' By Critics",
        "Scientists Discover %s Actually Improves Quality of Life",
        "Government Announces New %s Tax, Citizens Surprisingly Supportive",
        "Study Shows %s Prevents Common Cold, Doctors Baffled",
        "World Leaders Gather to Discuss the Importance of %s",
        "Breaking: %s Shortage Causes Nationwide Panic",
        "Survey Reveals 90%% of People Secretly Enjoy %s",
        "Experts Warn: %s Addiction Reaching Epidemic Proportions"
    };
    
    private final String[] INTRO_TEMPLATES = {
        "In a development that has shocked absolutely no one, %s has become the center of attention in what experts are calling an 'unprecedented situation.'",
        "Local residents expressed both shock and awe yesterday when %s suddenly became the topic on everyone's lips.",
        "In what many are describing as 'the least surprising news of the year,' %s has officially been recognized as a matter of national importance.",
        "Breaking news today as %s has finally gained the recognition that insiders have long predicted."
    };
    
    private final String[] QUOTE_TEMPLATES = {
        "'I never thought I'd see the day,' said local resident Jane Smith, who has been following the %s situation for years. 'It's about time people started paying attention.'",
        "'We've been saying this would happen for decades,' claimed industry expert Dr. Robert Johnson. 'The %s phenomenon was inevitable, but nobody listened to our warnings.'",
        "'This changes everything we thought we knew about %s,' explained Professor Maria Garcia, who has published numerous studies on the subject. 'We're going to need to rewrite the textbooks.'",
        "'I, for one, welcome our new %s overlords,' joked community member Tom Williams, before adding more seriously, 'But honestly, we should have seen this coming.'"
    };
    
    private final String[] DEVELOPMENT_TEMPLATES = {
        "Officials have declined to comment directly, but anonymous sources within the administration suggested that '%s is just the beginning.' Markets reacted predictably, with analysts forecasting either doom or celebration, depending on who was asked.",
        "Meanwhile, social media has exploded with %s memes, while traditional media outlets scramble to explain the complexities to an increasingly confused public. 'Nobody really understands %s,' admitted one TV news anchor, off the record.",
        "Scientists are reportedly working around the clock to determine how %s will affect daily life, though preliminary findings suggest that most people won't notice any difference whatsoever.",
        "Retail stores have reported a 500%% increase in %s-related purchases, despite there being no clear connection between consumer goods and the recent developments."
    };
    
    private final String[] CONCLUSION_TEMPLATES = {
        "As of press time, three separate GoFundMe campaigns related to %s have raised over $10,000 each, though none have specified exactly how the money will be used.",
        "At this point, experts agree on only one thing: %s is here to stay, whether we're ready for it or not.",
        "The only certainty in this %s situation is that nothing is certain, except possibly the inevitable merchandising opportunities that entrepreneurs are already exploring.",
        "When asked for comment, the average person on the street responded, 'What's %s?' suggesting that perhaps the media frenzy is somewhat overblown."
    };

    public StoryGenerator() {
        logger.info("Initializing template-based StoryGenerator");
    }

    public String generate(String textPrompt) throws IOException {
        logger.info("Generating template-based story for prompt: {}", 
                  textPrompt.substring(0, Math.min(50, textPrompt.length())));
        
        try {
            // Generate a unique ID for tracking
            String storyId = UUID.randomUUID().toString().substring(0, 8);
            logger.info("Creating story with tracking ID: {}", storyId);
            
            // Clean and prepare the prompt
            String cleanPrompt = cleanPrompt(textPrompt);
            
            // Generate headline
            String headlineTemplate = HEADLINES[random.nextInt(HEADLINES.length)];
            String headline = String.format(headlineTemplate, cleanPrompt);
            
            // Generate story sections
            String intro = String.format(INTRO_TEMPLATES[random.nextInt(INTRO_TEMPLATES.length)], cleanPrompt);
            String quote = String.format(QUOTE_TEMPLATES[random.nextInt(QUOTE_TEMPLATES.length)], cleanPrompt);
            String development = String.format(DEVELOPMENT_TEMPLATES[random.nextInt(DEVELOPMENT_TEMPLATES.length)], 
                                            cleanPrompt, cleanPrompt);
            String conclusion = String.format(CONCLUSION_TEMPLATES[random.nextInt(CONCLUSION_TEMPLATES.length)], 
                                           cleanPrompt);
            
            // Combine all sections
            StringBuilder storyBuilder = new StringBuilder();
            storyBuilder.append("THE DAILY SATIRE\n\n");
            storyBuilder.append("HEADLINE: ").append(headline).append("\n\n");
            storyBuilder.append(intro).append("\n\n");
            storyBuilder.append(quote).append("\n\n");
            storyBuilder.append(development).append("\n\n");
            storyBuilder.append(conclusion);
            
            logger.info("Successfully generated template story for prompt: '{}' (ID: {})", 
                      cleanPrompt, storyId);
            
            return storyBuilder.toString();
        } catch (Exception e) {
            logger.error("Error generating story: {}", e.getMessage(), e);
            return generateFallbackStory(textPrompt);
        }
    }
    
    /**
     * Clean and prepare the prompt for insertion into templates
     */
    private String cleanPrompt(String prompt) {
        // Basic cleaning to ensure prompt fits well in templates
        String cleaned = prompt.trim();
        
        // Remove trailing punctuation
        if (cleaned.endsWith(".") || cleaned.endsWith("!") || cleaned.endsWith("?")) {
            cleaned = cleaned.substring(0, cleaned.length() - 1);
        }
        
        // Ensure first letter is not capitalized if it's in the middle of a sentence
        if (cleaned.length() > 1 && Character.isUpperCase(cleaned.charAt(0))) {
            cleaned = Character.toLowerCase(cleaned.charAt(0)) + cleaned.substring(1);
        }
        
        return cleaned;
    }
    
    private String generateFallbackStory(String prompt) {
        // Create a basic fallback story when template generation fails
        String headline = prompt.length() > 50 ? prompt.substring(0, 50) + "..." : prompt;
        
        return "SATIRE NEWS (Fallback Mode)\n\n" +
               "HEADLINE: " + headline + "\n\n" +
               "In what experts are calling an 'unprecedented development,' local citizens expressed " +
               "shock and awe at the recent turn of events. \"I never thought I'd see the day,\" said " +
               "community member Jane Smith, struggling to find words.\n\n" +
               "Officials declined to comment directly, but anonymous sources within the administration " +
               "suggested that 'this is just the beginning.' Markets reacted predictably, with analysts " +
               "forecasting either doom or celebration, depending on who was asked.\n\n" +
               "[Note: This is a fallback story generated via templates.]";
    }
}