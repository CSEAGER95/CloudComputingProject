import React, { useState, useEffect } from 'react';
import Button from './Button';
import apiService, { Story } from '../services/apiService';

const StoryList: React.FC = () => {
  const [stories, setStories] = useState<Story[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedStoryId, setExpandedStoryId] = useState<string | null>(null);

  const fetchStories = async () => {
    try {
      setLoading(true);
      const data = await apiService.getStories();
      setStories(data);
      setError(null);
    } catch (err) {
      console.error('Error fetching stories:', err);
      setError('Failed to load stories. Please try again later.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchStories();
    // Set up a polling interval to check for new stories every 30 seconds
    const interval = setInterval(fetchStories, 30000);
    
    // Clean up the interval on component unmount
    return () => clearInterval(interval);
  }, []);

  const handleUpvote = async (storyId: string) => {
    try {
      const updatedStory = await apiService.upvoteStory(storyId);
      // Update the story in the local state
      setStories(prevStories => 
        prevStories.map(story => 
          story.id === storyId 
            ? updatedStory
            : story
        )
      );
    } catch (err) {
      console.error('Error upvoting story:', err);
      alert('Failed to upvote. Please try again.');
    }
  };

  const handleDownvote = async (storyId: string) => {
    try {
      const updatedStory = await apiService.downvoteStory(storyId);
      // Update the story in the local state
      setStories(prevStories => 
        prevStories.map(story => 
          story.id === storyId 
            ? updatedStory
            : story
        )
      );
    } catch (err) {
      console.error('Error downvoting story:', err);
      alert('Failed to downvote. Please try again.');
    }
  };

  const toggleExpandStory = (storyId: string) => {
    if (expandedStoryId === storyId) {
      setExpandedStoryId(null);
    } else {
      setExpandedStoryId(storyId);
    }
  };

  if (loading && stories.length === 0) {
    return <div>Loading stories...</div>;
  }

  if (error && stories.length === 0) {
    return (
      <div style={{ textAlign: 'center', color: 'red', margin: '20px 0' }}>
        {error}
        <Button 
          text="Try Again" 
          onClick={fetchStories} 
          style={{ marginTop: '10px' }}
        />
      </div>
    );
  }

  if (stories.length === 0) {
    return <div style={{ textAlign: 'center', margin: '20px 0' }}>No stories yet. Be the first to submit a prompt!</div>;
  }

  // Sort stories by popularity (upvotes - downvotes)
  const sortedStories = [...stories].sort((a, b) => 
    (b.upvotes - b.downvotes) - (a.upvotes - a.downvotes)
  );

  return (
    <div className="story-list" style={{ maxWidth: '800px', margin: '20px auto' }}>
      <h2>Generated Satire Articles</h2>
      
      {loading && <div style={{ textAlign: 'center', margin: '10px 0' }}>Refreshing stories...</div>}
      
      {sortedStories.map(story => (
        <div 
          key={story.id} 
          style={{ 
            border: '1px solid #ddd', 
            borderRadius: '8px', 
            padding: '15px', 
            marginBottom: '20px',
            boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
          }}
        >
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
            <div>
              <h3 style={{ margin: '0 0 10px 0' }}>
                {story.prompt.length > 100 
                  ? `${story.prompt.substring(0, 100)}...` 
                  : story.prompt}
              </h3>
              
              <div style={{ 
                margin: '10px 0', 
                fontSize: '14px', 
                color: '#666',
                display: 'flex',
                alignItems: 'center'
              }}>
                <div style={{ marginRight: '20px' }}>
                  <span style={{ fontWeight: 'bold', color: 'green' }}>+{story.upvotes}</span> upvotes
                </div>
                <div>
                  <span style={{ fontWeight: 'bold', color: 'red' }}>-{story.downvotes}</span> downvotes
                </div>
              </div>
            </div>
            
            <div style={{ display: 'flex', gap: '10px' }}>
              <Button 
                text="👍" 
                onClick={() => handleUpvote(story.id)}
                style={{
                  fontSize: '18px',
                  padding: '5px 12px',
                  backgroundColor: '#4CAF50'
                }}
              />
              <Button 
                text="👎" 
                onClick={() => handleDownvote(story.id)}
                style={{
                  fontSize: '18px',
                  padding: '5px 12px',
                  backgroundColor: '#f44336'
                }}
              />
            </div>
          </div>
          
          <div style={{ margin: '15px 0' }}>
            {expandedStoryId === story.id ? (
              <div style={{ whiteSpace: 'pre-wrap' }}>
                {story.story}
              </div>
            ) : (
              <div>
                {story.story.substring(0, 150)}...
              </div>
            )}
          </div>
          
          <Button 
            text={expandedStoryId === story.id ? "Show Less" : "Read Full Article"} 
            onClick={() => toggleExpandStory(story.id)}
            style={{
              backgroundColor: '#0070f3',
              padding: '8px 15px',
              fontSize: '14px'
            }}
          />
        </div>
      ))}
    </div>
  );
};

export default StoryList;
