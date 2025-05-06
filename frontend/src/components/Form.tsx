// Enhanced Form.tsx with better error handling
import React, { useState, useEffect } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import axios from 'axios';
import Button from './Button';
import apiService from '../services/apiService';

interface FormInputs {
  prompt: string;
}

const Form: React.FC = () => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState<string | null>(null);
  const [errorDetails, setErrorDetails] = useState<string | null>(null);
  const [backendStatus, setBackendStatus] = useState<'loading' | 'connected' | 'error'>('loading');
  
  const { register, handleSubmit, formState: { errors }, watch, reset } = useForm<FormInputs>({
    mode: 'onChange',
    defaultValues: {
      prompt: ''
    }
  });

  // Test backend connection on component mount
  useEffect(() => {
    const testConnection = async () => {
      try {
        await apiService.testConnection();
        setBackendStatus('connected');
      } catch (error) {
        console.error('Backend connection test failed:', error);
        setBackendStatus('error');
      }
    };
    
    testConnection();
  }, []);
  
  const onSubmit: SubmitHandler<FormInputs> = async (data) => {
    if (data.prompt.length < 10) {
      alert('Prompt must be at least 10 characters long');
      return;
    }

    setIsSubmitting(true);
    setSubmissionResult(null);
    setErrorDetails(null);

    try {
      // Use the API service to create a new story
      const createdStory = await apiService.createStory(data.prompt);
      
      // Handle successful response
      console.log('Prompt sent successfully:', createdStory);
      setSubmissionResult('Your prompt was submitted successfully! Check the stories list to see it once it generates.');
      
      // Reset the form
      reset();
    } catch (error) {
      console.error('Error sending prompt:', error);
      
      // Provide detailed error message
      let errorMessage = 'There was an error submitting your prompt. Please try again.';
      let details = '';
      
      if (axios.isAxiosError(error)) {
        if (error.response) {
          errorMessage += ` (Status: ${error.response.status})`;
          details = JSON.stringify(error.response.data, null, 2);
        } else if (error.request) {
          errorMessage += ' (No response received from server)';
          details = 'The request was made but no response was received. Check network connectivity.';
        } else {
          errorMessage += ` (${error.message})`;
        }
      }
      
      setSubmissionResult(errorMessage);
      setErrorDetails(details);
    } finally {
      setIsSubmitting(false);
    }
  };

  const promptLength = watch('prompt')?.length || 0;
  const isValid = promptLength >= 10;

  return (
    <div className="form-container" style={{ maxWidth: '500px', margin: '0 auto' }}>
      {/* Backend connection status */}
      {backendStatus === 'loading' && (
        <div style={{ marginBottom: '15px', color: 'blue', textAlign: 'center' }}>
          Checking connection to backend...
        </div>
      )}
      
      {backendStatus === 'error' && (
        <div style={{ marginBottom: '15px', color: 'red', textAlign: 'center', padding: '10px', backgroundColor: '#ffe6e6', borderRadius: '4px' }}>
          Warning: Could not connect to the backend service. Form submissions may fail.
        </div>
      )}
      
      {backendStatus === 'connected' && (
        <div style={{ marginBottom: '15px', color: 'green', textAlign: 'center' }}>
          Connected to backend service ✓
        </div>
      )}
      
      <form onSubmit={handleSubmit(onSubmit)}>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="prompt" style={{ display: 'block', marginBottom: '5px', fontWeight: 'bold' }}>
            Enter a news headline or topic for satire generation
          </label>
          <textarea
            id="prompt"
            {...register('prompt', { 
              required: 'Prompt is required',
              minLength: {
                value: 10,
                message: 'Prompt must be at least 10 characters long'
              }
            })}
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: errors.prompt ? '1px solid red' : '1px solid #ccc',
              minHeight: '100px'
            }}
          />
          {errors.prompt && (
            <p style={{ color: 'red', fontSize: '14px', margin: '5px 0' }}>
              {errors.prompt.message}
            </p>
          )}
          <div style={{ fontSize: '12px', color: '#666', marginTop: '5px' }}>
            Characters: {promptLength}/10 minimum
          </div>
        </div>

        <div style={{ marginTop: '20px' }}>
          <Button 
            text={isSubmitting ? "Submitting..." : "Generate Satire Article"} 
            type="submit"
            disabled={!isValid || isSubmitting || backendStatus === 'error'}
            style={{
              opacity: (!isValid || isSubmitting || backendStatus === 'error') ? '0.5' : '1',
              cursor: (!isValid || isSubmitting || backendStatus === 'error') ? 'not-allowed' : 'pointer',
              backgroundColor: '#0070f3',
              width: '100%'
            }}
          />
        </div>
      </form>
      
      {submissionResult && (
        <div 
          style={{ 
            marginTop: '20px', 
            padding: '10px', 
            backgroundColor: submissionResult.includes('error') ? '#ffe6e6' : '#e6ffe6',
            borderRadius: '4px'
          }}
        >
          {submissionResult}
          
          {errorDetails && (
            <details style={{ marginTop: '10px', fontSize: '12px' }}>
              <summary>Error Details</summary>
              <pre style={{ whiteSpace: 'pre-wrap', marginTop: '5px' }}>
                {errorDetails}
              </pre>
            </details>
          )}
        </div>
      )}
    </div>
  );
};

export default Form;