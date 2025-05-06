// src/services/api.ts
import axios from 'axios';

// Define Story interface
export interface Story {
  id: string;
  prompt: string;
  story: string;
  upvotes: number;
  downvotes: number;
}

// Create axios instance with proper CORS configuration
const apiUrl = window.API_URL || process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';

const apiClient = axios.create({
  baseURL: apiUrl,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 15000,
  withCredentials: false
});

// Log configuration for debugging
console.log('API client initialized with URL:', apiUrl);

// API service with all endpoints
export const api = {
  // Health check
  checkConnection: async (): Promise<boolean> => {
    try {
      const response = await apiClient.get('/prompt/test');
      return response.status === 200;
    } catch (error) {
      console.error('API connection test failed:', error);
      return false;
    }
  },

  // Get all stories
  getStories: async (): Promise<Story[]> => {
    try {
      console.log('Fetching stories...');
      const response = await apiClient.get('/prompt');
      console.log('Stories received:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error fetching stories:', error);
      throw error;
    }
  },

  // Create a new story
  createStory: async (prompt: string): Promise<Story> => {
    try {
      console.log('Creating story with prompt:', prompt);
      const response = await apiClient.post('/prompt/story', { prompt });
      console.log('Story created:', response.data);
      return response.data;
    } catch (error) {
      console.error('Error creating story:', error);
      throw error;
    }
  },

  // Vote on a story
  upvoteStory: async (storyId: string): Promise<Story> => {
    try {
      const response = await apiClient.post(`/prompt/upvote/${storyId}`);
      return response.data;
    } catch (error) {
      console.error('Error upvoting story:', error);
      throw error;
    }
  },

  downvoteStory: async (storyId: string): Promise<Story> => {
    try {
      const response = await apiClient.post(`/prompt/downvote/${storyId}`);
      return response.data;
    } catch (error) {
      console.error('Error downvoting story:', error);
      throw error;
    }
  },

  // Debug endpoint for diagnostics
  getDiagnostics: async (): Promise<any> => {
    try {
      // Try multiple endpoints for thorough debugging
      const testResponse = await apiClient.get('/prompt/test');
      const datastoreResponse = await apiClient.get('/prompt/testdatastore');
      
      return {
        apiUrl,
        test: {
          status: testResponse.status,
          data: testResponse.data
        },
        datastore: {
          status: datastoreResponse.status,
          data: datastoreResponse.data
        },
        environment: {
          userAgent: navigator.userAgent,
          timestamp: new Date().toISOString()
        }
      };
    } catch (error) {
      console.error('Diagnostics error:', error);
      return { error: error instanceof Error ? error.message : String(error) };
    }
  }
};

export default api;