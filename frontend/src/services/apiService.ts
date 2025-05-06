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

// Use the environment variable or fallback to the hardcoded URL
const API_URL = process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';

console.log('API URL configured as:', API_URL);

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
    return config;
  },
  error => {
    console.error('Request error:', error);
    return Promise.reject(error);
  }
);

// Debug interceptor for responses
apiClient.interceptors.response.use(
  response => {
    console.log(`Response from ${response.config.url}:`, response.status, response.data);
    return response;
  },
  error => {
    if (axios.isAxiosError(error)) {
      console.error('API Error:', {
        url: error.config?.url,
        status: error.response?.status,
        statusText: error.response?.statusText,
        data: error.response?.data,
        message: error.message
      });
    } else {
      console.error('Unknown error:', error);
    }
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
  // Test connection to backend
  testConnection: async (): Promise<any> => {
    try {
      console.log('Testing backend connection');
      const response = await apiClient.get('/prompt/test');
      console.log('Backend connection successful');
      return response.data;
    } catch (error) {
      console.error('Backend connection failed:', error);
      throw error;
    }
  },

  // Fetch all stories with retry
  getStories: async (): Promise<Story[]> => {
    console.log('Fetching stories');
    return withRetry(async () => {
      try {
        const response = await apiClient.get('/prompt');
        console.log(`Retrieved ${response.data.length} stories`);
        return response.data;
      } catch (error) {
        console.error('Failed to fetch stories:', error);
        throw error;
      }
    });
  },
  
  // Create a new story with better error handling
  createStory: async (prompt: string): Promise<Story> => {
    console.log('Creating story with prompt:', prompt.substring(0, 30) + '...');
    return withRetry(async () => {
      try {
        // Send as proper JSON
        const response = await apiClient.post('/prompt/story', { prompt });
        console.log('Story created successfully with ID:', response.data.id);
        return response.data;
      } catch (error) {
        console.error('Failed to create story:', error);
        // Try alternative format if first attempt failed
        if (axios.isAxiosError(error) && error.response?.status === 400) {
          console.log('Trying alternative request format...');
          const altResponse = await apiClient.post('/prompt/story', JSON.stringify({ prompt }));
          return altResponse.data;
        }
        throw error;
      }
    });
  },
  
  // Upvote a story
  upvoteStory: async (storyId: string): Promise<Story> => {
    console.log('Upvoting story:', storyId);
    return withRetry(async () => {
      const response = await apiClient.post(`/prompt/upvote/${storyId}`);
      console.log('Story upvoted successfully');
      return response.data;
    });
  },
  
  // Downvote a story
  downvoteStory: async (storyId: string): Promise<Story> => {
    console.log('Downvoting story:', storyId);
    return withRetry(async () => {
      const response = await apiClient.post(`/prompt/downvote/${storyId}`);
      console.log('Story downvoted successfully');
      return response.data;
    });
  },
  
  // Get API debug info
  getDebugInfo: async (): Promise<Record<string, any>> => {
    try {
      return {
        apiUrl: API_URL,
        timestamp: new Date().toISOString(),
        endpoints: {
          test: await apiClient.get('/prompt/test').then(() => 'OK').catch(e => e.message),
          stories: await apiClient.get('/prompt').then(r => `OK (${r.data.length} stories)`).catch(e => e.message),
          testdatastore: await apiClient.get('/prompt/testdatastore').then(() => 'OK').catch(e => e.message)
        }
      };
    } catch (error) {
      return {
        apiUrl: API_URL,
        timestamp: new Date().toISOString(),
        error: error instanceof Error ? error.message : 'Unknown error'
      };
    }
  }
};

export default apiService;