// Enhanced StoryList with better error handling and debugging
import React, { useState, useEffect } from 'react';
import Button from './Button';
import apiService, { Story } from '../services/apiService';

const StoryList: React.FC = () => {
  const [stories, setStories] = useState<Story[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedStoryId, setExpandedStoryId] = useState<string | null>(null);
  const [lastFetchTime, setLastFetchTime] = useState<Date | null>(null);

  const fetchStories = async () => {
    try {
      setLoading(true);
      console.log('Fetching stories from backend');
      const data = await apiService.getStories();
      console.log(`Received ${data.length} stories from backend`);
      setStories(data);
      setError(null);
      setLastFetchTime(new Date());
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
      <div style={{ textAlign: 'center', color: 'red', margin: '20px 0', padding: '15px', backgroundColor: '#ffe6e6', borderRadius: '8px' }}>
        <p><strong>Error:</strong> {error}</p>
        <p style={{ fontSize: '14px', marginTop: '10px' }}>
          This could be due to:
          <ul style={{ textAlign: 'left', marginTop: '5px' }}>
            <li>Backend service not running</li>
            <li>Network connectivity issues</li>
            <li>CORS configuration problems</li>
          </ul>
        </p>
        <Button 
          text="Try Again" 
          onClick={fetchStories} 
          style={{ marginTop: '10px', backgroundColor: '#0070f3' }}
        />
      </div>
    );
  }

  if (stories.length === 0) {
    return (
      <div style={{ textAlign: 'center', margin: '20px 0', padding: '20px', backgroundColor: '#f8f9fa', borderRadius: '8px' }}>
        <p>No stories yet. Be the first to submit a prompt!</p>
        <p style={{ fontSize: '14px', marginTop: '10px', color: '#666' }}>
          Last checked: {lastFetchTime ? lastFetchTime.toLocaleTimeString() : 'Never'}
        </p>
        <Button 
          text="Refresh Now" 
          onClick={fetchStories} 
          style={{ marginTop: '10px', backgroundColor: '#0070f3' }}
        />
      </div>
    );
  }

  // Sort stories by popularity (upvotes - downvotes)
  const sortedStories = [...stories].sort((a, b) => 
    (b.upvotes - b.downvotes) - (a.upvotes - a.downvotes)
  );

  return (
    <div className="story-list" style={{ maxWidth: '800px', margin: '20px auto' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2>Generated Satire Articles</h2>
        <div style={{ fontSize: '14px', color: '#666' }}>
          {stories.length} stories loaded • Last updated: {lastFetchTime ? lastFetchTime.toLocaleTimeString() : 'Never'}
          <Button 
            text="Refresh" 
            onClick={fetchStories} 
            style={{ marginLeft: '10px', padding: '5px 10px', fontSize: '12px', backgroundColor: '#6c757d' }}
          />
        </div>
      </div>
      
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
                <div style={{ marginLeft: '20px', fontSize: '12px' }}>
                  Story ID: {story.id.substring(0, 8)}...
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