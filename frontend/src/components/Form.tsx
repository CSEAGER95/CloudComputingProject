// src/components/Form.tsx
import React, { useState } from 'react';
import Button from './Button';

const Form: React.FC = () => {
  const [prompt, setPrompt] = useState<string>('');
  const [isSubmitting, setIsSubmitting] = useState<boolean>(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<boolean>(false);

  const apiUrl = window.API_URL;
  console.log('Form using API URL:', apiUrl);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    
    if (prompt.trim().length < 10) {
      setError('Please enter at least 10 characters');
      return;
    }
    
    setIsSubmitting(true);
    setError(null);
    
    try {
      console.log('Submitting prompt to:', `${apiUrl}/prompt/story`);
      const response = await fetch(`${apiUrl}/prompt/story`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify({ prompt: prompt })
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Story created successfully:', data);
      setSuccess(true);
      setPrompt('');
      
      // Hide success message after 5 seconds
      setTimeout(() => setSuccess(false), 5000);
    } catch (err) {
      console.error('Error creating story:', err);
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(`Error: ${errorMessage}`);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      <div>
        <label htmlFor="prompt" style={{ display: 'block', marginBottom: '10px' }}>
          Enter a news headline or topic for satire generation
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
            minHeight: '100px'
          }}
          required
        />
        <div style={{ fontSize: '14px', marginTop: '5px' }}>
          Characters: {prompt.length}/10 minimum
        </div>
      </div>
      
      {error && (
        <div style={{ color: 'red', marginTop: '10px' }}>
          {error}
        </div>
      )}
      
      {success && (
        <div style={{ color: 'green', marginTop: '10px' }}>
          Your prompt was submitted successfully! Check the stories list below.
        </div>
      )}
      
      <div style={{ marginTop: '20px' }}>
        <Button
          text={isSubmitting ? "Generating..." : "Generate Satire Article"}
          type="submit"
          disabled={isSubmitting || prompt.length < 10}
          style={{
            backgroundColor: '#0070f3',
            opacity: isSubmitting || prompt.length < 10 ? 0.7 : 1
          }}
        />
      </div>
    </form>
  );
};

export default Form;