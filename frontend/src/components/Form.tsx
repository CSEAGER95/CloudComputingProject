// src/components/Form.tsx
import React, { useState } from 'react';
import { api } from '../services/api';
import Button from './Button';

const Form: React.FC = () => {
  const [prompt, setPrompt] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);
  
  // Custom event for story creation
  const createCustomEvent = () => {
    const event = new CustomEvent('storyCreated', { detail: { timestamp: new Date() } });
    document.dispatchEvent(event);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (prompt.trim().length < 10) {
      setError('Please enter at least 10 characters');
      return;
    }
    
    setIsSubmitting(true);
    setError(null);
    
    try {
      // Use the api service instead of direct fetch
      await api.createStory(prompt);
      
      // Clear form and show success
      setSuccess(true);
      setPrompt('');
      
      // Emit event for other components to refresh
      createCustomEvent();
      
      // Hide success message after 5 seconds
      setTimeout(() => setSuccess(false), 5000);
    } catch (err) {
      console.error('Error in form submission:', err);
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(`Error: ${errorMessage}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label htmlFor="prompt" style={{ display: 'block', marginBottom: '10px', fontWeight: 'bold' }}>
          Enter a news headline or topic for satire generation:
        </label>
        <textarea
          id="prompt"
          value={prompt}
          onChange={(e) => setPrompt(e.target.value)}
          style={{ 
            width: '100%', 
            padding: '10px', 
            borderRadius: '5px', 
            border: '1px solid #ccc',
            minHeight: '100px',
            fontSize: '16px'
          }}
          placeholder="Example: Local man discovers broccoli tastes better with cheese"
          required
        />
        <div style={{ fontSize: '14px', marginTop: '5px', color: prompt.length < 10 ? '#d32f2f' : '#666' }}>
          Characters: {prompt.length}/10 minimum
        </div>
      </div>
      
      {error && (
        <div style={{ 
          color: 'white', 
          backgroundColor: '#d32f2f', 
          padding: '10px', 
          borderRadius: '5px', 
          marginTop: '10px' 
        }}>
          {error}
        </div>
      )}
      
      {success && (
        <div style={{ 
          color: 'white', 
          backgroundColor: '#4caf50', 
          padding: '10px', 
          borderRadius: '5px', 
          marginTop: '10px' 
        }}>
          Your satire article was created successfully! Check the stories list below.
        </div>
      )}
      
      <div style={{ marginTop: '20px' }}>
        <Button
          text={isSubmitting ? "Generating..." : "Generate Satire Article"}
          type="submit"
          disabled={isSubmitting || prompt.length < 10}
          style={{
            backgroundColor: '#0070f3',
            opacity: isSubmitting || prompt.length < 10 ? 0.7 : 1,
            padding: '12px 24px',
            fontSize: '16px'
          }}
        />
      </div>
    </form>
  );
};

export default Form;