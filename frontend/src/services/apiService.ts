// src/services/apiService.ts
import axios from 'axios';

// Define interface for Story data
export interface Story {
  id: string;
  prompt: string;
  story: string;
  upvotes: number;
  downvotes: number;
}

// Use the global API_URL set in App.tsx
const API_URL = window.API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';

console.log('apiService initialized with API URL:', API_URL);

// Create an axios instance with improved configuration
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 15000,
  withCredentials: false
});

// Debug interceptor for requests
apiClient.interceptors.request.use(
  config => {
    console.log(`Request: ${config.method?.toUpperCase()} ${config.url}`, config.data);
    console.log('Full URL:', `${API_URL}${config.url}`); // Log the full URL for debugging
    return config;
  },
  error => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Helper function for retrying failed requests
const withRetry = async <T>(fn: () => Promise<T>, maxRetries = 3): Promise<T> => {
  let lastError: any;
  for (let attempt = 1; attempt <= maxRetries; attempt++) {
    try {
      return await fn();
    } catch (error) {
      console.warn(`Attempt ${attempt} failed, ${maxRetries - attempt} retries left`);
      lastError = error;
      // Add exponential backoff if not the last attempt
      if (attempt < maxRetries) {
        const delay = Math.min(Math.pow(2, attempt) * 1000, 10000);
        await new Promise(resolve => setTimeout(resolve, delay));
      }
    }
  }
  throw lastError;
};

// API methods with improved error handling and retry
export const apiService = {
  // Fetch all stories with retry
  getStories: async (): Promise<Story[]> => {
    console.log('Fetching stories from:', `${API_URL}/prompt`);
    try {
      const response = await fetch(`${API_URL}/prompt`, {
        method: 'GET',
        mode: 'cors',
        headers: {
          'Accept': 'application/json'
        }
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Stories fetched successfully:', data);
      return data;
    } catch (error) {
      console.error('Error fetching stories:', error);
      throw error;
    }
  },
  
  // Create a new story
  createStory: async (prompt: string): Promise<Story> => {
    console.log('Creating story at:', `${API_URL}/prompt/story`);
    try {
      const response = await fetch(`${API_URL}/prompt/story`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Accept': 'application/json'
        },
        mode: 'cors',
        body: JSON.stringify({ prompt })
      });
      
      if (!response.ok) {
        throw new Error(`HTTP error: ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Story created successfully:', data);
      return data;
    } catch (error) {
      console.error('Error creating story:', error);
      throw error;
    }
  }
};

export default apiService;