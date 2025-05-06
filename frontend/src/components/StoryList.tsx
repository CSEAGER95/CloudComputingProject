// src/components/StoryList.tsx
import React, { useState, useEffect } from 'react';
import Button from './Button';

interface Story {
  id: string;
  prompt: string;
  story: string;
  upvotes: number;
  downvotes: number;
}

const StoryList: React.FC = () => {
  const [stories, setStories] = useState<Story[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedStoryId, setExpandedStoryId] = useState<string | null>(null);

  const apiUrl = window.API_URL;
  console.log('StoryList using API URL:', apiUrl);
  
  const fetchStories = async () => {
    setLoading(true);
    try {
      // Log the full URL for debugging
      console.log('Fetching stories from:', `${apiUrl}/prompt`);
      
      const response = await fetch(`${apiUrl}/prompt`, {
        method: 'GET',
        headers: {
          'Accept': 'application/json'
        },
        mode: 'cors'
      });
      
      console.log('Response status:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Retrieved stories:', data);
      
      if (Array.isArray(data)) {
        setStories(data);
        setError(null);
      } else {
        throw new Error('Invalid data format received');
      }
    } catch (err) {
      console.error('Failed to fetch stories:', err);
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(`Failed to load stories: ${errorMessage}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    console.log('StoryList component mounted');
    fetchStories();
  }, []);

  const toggleExpandStory = (storyId: string) => {
    setExpandedStoryId(expandedStoryId === storyId ? null : storyId);
  };

  return (
    <div className="story-list">
      <h2>Generated Satire Articles</h2>
      
      <Button text="Refresh" onClick={fetchStories} />
      
      {loading && <p>Loading stories...</p>}
      
      {error && (
        <div style={{ backgroundColor: '#ffe6e6', padding: '10px', margin: '10px 0', borderRadius: '5px' }}>
          <strong>Error:</strong> {error}
          <Button 
            text="Try Again" 
            onClick={fetchStories} 
            style={{ marginLeft: '10px', backgroundColor: '#0070f3' }}
          />
        </div>
      )}
      
      {!loading && !error && stories.length === 0 && (
        <p>No stories found. Create your first story above!</p>
      )}
      
      {stories.map(story => (
        <div key={story.id} style={{ border: '1px solid #ddd', padding: '15px', margin: '15px 0', borderRadius: '5px' }}>
          <h3>{story.prompt}</h3>
          
          <div style={{ marginTop: '10px' }}>
            {expandedStoryId === story.id ? (
              <div style={{ whiteSpace: 'pre-wrap' }}>{story.story}</div>
            ) : (
              <div>{story.story && story.story.substring(0, 150)}...</div>
            )}
          </div>
          
          <div style={{ marginTop: '10px', display: 'flex', justifyContent: 'space-between' }}>
            <div>
              <span style={{ marginRight: '15px' }}>👍 {story.upvotes || 0}</span>
              <span>👎 {story.downvotes || 0}</span>
            </div>
            
            <Button
              text={expandedStoryId === story.id ? "Show Less" : "Read Full Article"}
              onClick={() => toggleExpandStory(story.id)}
            />
          </div>
        </div>
      ))}
    </div>
  );
};

export default StoryList;