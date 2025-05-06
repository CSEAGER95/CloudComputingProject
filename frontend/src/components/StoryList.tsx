// src/components/StoryList.tsx
import React, { useState, useEffect } from 'react';
import Button from './Button';
import apiService from '../services/apiService';

const StoryList = () => {
  const [stories, setStories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const [expandedStoryId, setExpandedStoryId] = useState(null);
  
  const apiUrl = process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';

  const fetchStories = async () => {
    setLoading(true);
    try {
      // Use the correct endpoint - /prompt/stories
      console.log('Fetching stories from: ' + apiUrl + '/prompt/stories');
      const response = await fetch(`${apiUrl}/prompt/stories`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' },
        mode: 'cors',
        credentials: 'omit'
      });
      
      console.log('Response status:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Retrieved stories:', data);
      setStories(data);
      setError(null);
    } catch (err) {
      console.error('Failed to fetch stories:', err);
      setError(`Error loading stories: ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStories();
  }, []);

  const toggleExpandStory = (storyId) => {
    if (expandedStoryId === storyId) {
      setExpandedStoryId(null);
    } else {
      setExpandedStoryId(storyId);
    }
  };

  return (
    <div className="story-list" style={{ maxWidth: '800px', margin: '0 auto' }}>
      <h2>Generated Satire Articles</h2>
      
      <Button text="Refresh" onClick={fetchStories} />
      
      {loading && <p>Loading stories...</p>}
      
      {error && (
        <div style={{ backgroundColor: '#ffe6e6', padding: '10px', margin: '10px 0', borderRadius: '5px' }}>
          <strong>Error:</strong> {error}
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