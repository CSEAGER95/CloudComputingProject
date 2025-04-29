import React from 'react';
import { useForm, SubmitHandler } from 'react-hook-form';
import axios from 'axios';
import Button from './Button';

interface FormInputs {
  message: string;
}

const Form: React.FC = () => {
  const { register, handleSubmit, formState: { errors, isSubmitted }, watch } = useForm<FormInputs>({
    mode: 'onChange',
    defaultValues: {
      message: ''
    }
  });
  
  const onSubmit: SubmitHandler<FormInputs> = async (data) => {
    if (data.message.length < 10) {
      alert('Message must be at least 10 characters long');
      return;
    }

    if (data.message.length > 1000) {
      alert('Message cannot be more than 1000 characters long');
      return;
    }

    try {
      // TODO: replace with actual post message once the endpoint is ready
      const response = await axios.get('https://teamprojectmccewenseager.ue.r.appspot.com/prompt');
      
      // Handle successful response
      console.log('Message sent successfully:', response.data);
    } catch (error) {
      console.error('Error sending message:', error);
    }
  };

  const messageLength = watch('message')?.length || 0;
  const isValid = messageLength >= 10;

  return (
    <div className="form-container" style={{ maxWidth: '500px', margin: '0 auto' }}>
      <form onSubmit={handleSubmit(onSubmit)}>
        <div style={{ marginBottom: '15px' }}>
          <label htmlFor="message" style={{ display: 'block', marginBottom: '5px' }}>Message</label>
          <textarea
            id="message"
            {...register('message', { 
              required: 'Message is required',
              minLength: {
                value: 10,
                message: 'Message must be at least 10 characters long'
              }
            })}
            style={{
              width: '100%',
              padding: '8px',
              borderRadius: '4px',
              border: isSubmitted && errors.message ? '1px solid red' : '1px solid #ccc',
              minHeight: '100px'
            }}
          />
          {isSubmitted && errors.message && (
            <p style={{ color: 'red', fontSize: '14px', margin: '5px 0' }}>
              {errors.message.message}
            </p>
          )}
          <div style={{ fontSize: '12px', color: '#666', marginTop: '5px' }}>
            Characters: {messageLength}/10 minimum
          </div>
        </div>

        <div style={{ marginTop: '20px' }}>
          <Button 
            text="Submit" 
            type="submit"
            disabled={!isValid}
            style={{
              opacity: !isValid ? '0.5' : '1',
              cursor: !isValid ? 'not-allowed' : 'pointer'
            }}
          />
        </div>
      </form>
    </div>
  );
};

export default Form; 