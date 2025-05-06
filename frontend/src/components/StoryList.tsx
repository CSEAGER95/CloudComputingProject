// src/components/StoryList.tsx
import React, { useState, useEffect } from 'react';
import { api, Story } from '../services/api';
import Button from './Button';

const StoryList: React.FC = () => {
  const [stories, setStories] = useState<Story[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);
  const [expandedStoryId, setExpandedStoryId] = useState<string | null>(null);

  const fetchStories = async () => {
    setLoading(true);
    setError(null);
    
    try {
      const fetchedStories = await api.getStories();
      
      if (Array.isArray(fetchedStories)) {
        setStories(fetchedStories);
      } else {
        throw new Error('Invalid data format received');
      }
    } catch (err) {
      console.error('Error fetching stories:', err);
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(`Unable to load stories: ${errorMessage}`);
    } finally {
      setLoading(false);
    }
  };

  // Initial load
  useEffect(() => {
    fetchStories();
    
    // Listen for story creation events
    const handleStoryCreated = () => {
      fetchStories();
    };
    
    document.addEventListener('storyCreated', handleStoryCreated);
    
    // Cleanup listener on unmount
    return () => {
      document.removeEventListener('storyCreated', handleStoryCreated);
    };
  }, []);

  const toggleExpandStory = (storyId: string) => {
    setExpandedStoryId(expandedStoryId === storyId ? null : storyId);
  };

  const handleVote = async (storyId: string, isUpvote: boolean) => {
    try {
      const updatedStory = isUpvote 
        ? await api.upvoteStory(storyId)
        : await api.downvoteStory(storyId);
      
      // Update the story in the local state
      setStories(stories.map(story => 
        story.id === storyId ? updatedStory : story
      ));
    } catch (err) {
      console.error(`Error ${isUpvote ? 'upvoting' : 'downvoting'} story:`, err);
    }
  };

  // Format the story text properly
  const formatStoryText = (text: string) => {
    if (!text) return '';
    
    // Replace newlines with proper breaks for display
    return text.split('\n').map((paragraph, index) => (
      <p key={index} style={{ marginBottom: '0.8em' }}>{paragraph}</p>
    ));
  };

  return (
    <div className="story-list">
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
        <h2 style={{ margin: 0 }}>Generated Satire Articles</h2>
        <Button 
          text="Refresh Stories" 
          onClick={fetchStories} 
          disabled={loading}
          style={{ backgroundColor: '#4caf50' }}
        />
      </div>
      
      {loading && (
        <div style={{ textAlign: 'center', padding: '40px 0' }}>
          <div style={{ fontSize: '18px', color: '#666' }}>Loading stories...</div>
        </div>
      )}
      
      {error && (
        <div style={{ 
          backgroundColor: '#ffebee', 
          padding: '15px', 
          margin: '20px 0', 
          borderRadius: '5px',
          borderLeft: '4px solid #d32f2f'
        }}>
          <div style={{ color: '#d32f2f', fontWeight: 'bold', marginBottom: '10px' }}>Error</div>
          <div>{error}</div>
          <Button 
            text="Try Again" 
            onClick={fetchStories} 
            style={{ 
              marginTop: '10px', 
              backgroundColor: '#d32f2f',
              color: 'white' 
            }}
          />
        </div>
      )}
      
      {!loading && !error && stories.length === 0 && (
        <div style={{ 
          padding: '30px', 
          textAlign: 'center', 
          backgroundColor: '#f5f5f5', 
          borderRadius: '5px'
        }}>
          <p style={{ fontSize: '18px', color: '#666' }}>
            No stories found. Create your first satire article above!
          </p>
        </div>
      )}
      
      <div style={{ display: 'grid', gap: '20px' }}>
        {stories.map(story => (
          <div 
            key={story.id} 
            style={{ 
              border: '1px solid #e0e0e0', 
              borderRadius: '8px', 
              padding: '20px',
              boxShadow: '0 2px 4px rgba(0,0,0,0.05)',
              backgroundColor: 'white'
            }}
          >
            <h3 style={{ margin: '0 0 15px 0', color: '#333' }}>{story.prompt}</h3>
            
            <div style={{ 
              margin: '15px 0',
              backgroundColor: '#f9f9f9', 
              padding: '15px',
              borderRadius: '5px',
              fontSize: '16px',
              lineHeight: 1.5
            }}>
              {expandedStoryId === story.id 
                ? formatStoryText(story.story)
                : (
                  <div>
                    {story.story && formatStoryText(story.story.substring(0, 150) + '...')}
                  </div>
                )
              }
            </div>
            
            <div style={{ 
              marginTop: '15px', 
              display: 'flex', 
              justifyContent: 'space-between',
              alignItems: 'center'
            }}>
              <div>
                <button 
                  onClick={() => handleVote(story.id, true)}
                  style={{
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    fontSize: '18px',
                    padding: '5px 10px',
                    marginRight: '15px'
                  }}
                >
                  👍 {story.upvotes || 0}
                </button>
                <button
                  onClick={() => handleVote(story.id, false)}
                  style={{
                    background: 'none',
                    border: 'none',
                    cursor: 'pointer',
                    fontSize: '18px',
                    padding: '5px 10px'
                  }}
                >
                  👎 {story.downvotes || 0}
                </button>
              </div>
              
              <Button
                text={expandedStoryId === story.id ? "Show Less" : "Read Full Article"}
                onClick={() => toggleExpandStory(story.id)}
                style={{
                  backgroundColor: '#0070f3',
                  padding: '8px 16px',
                  fontSize: '14px'
                }}
              />
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default StoryList;