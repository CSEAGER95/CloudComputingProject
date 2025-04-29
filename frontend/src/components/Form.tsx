import React, { useState } from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import Button from './Button';
import apiService from '../services/apiService';

interface FormInputs {
  prompt: string;
}

const Form: React.FC = () => {
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submissionResult, setSubmissionResult] = useState<string | null>(null);
  
  const { register, handleSubmit, formState: { errors }, watch, reset } = useForm<FormInputs>({
    mode: 'onChange',
    defaultValues: {
      prompt: ''
    }
  });
  
  const onSubmit: SubmitHandler<FormInputs> = async (data) => {
    if (data.prompt.length < 10) {
      alert('Prompt must be at least 10 characters long');
      return;
    }

    if (data.prompt.length > 1000) {
      alert('Prompt cannot be more than 1000 characters long');
      return;
    }

    setIsSubmitting(true);
    setSubmissionResult(null);

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
      setSubmissionResult('There was an error submitting your prompt. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const promptLength = watch('prompt')?.length || 0;
  const isValid = promptLength >= 10;

  return (
    <div className="form-container" style={{ maxWidth: '500px', margin: '0 auto' }}>
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
            disabled={!isValid || isSubmitting}
            style={{
              opacity: (!isValid || isSubmitting) ? '0.5' : '1',
              cursor: (!isValid || isSubmitting) ? 'not-allowed' : 'pointer',
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
        </div>
      )}
    </div>
  );
};

export default Form;
