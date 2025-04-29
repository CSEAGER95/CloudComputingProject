import axios from 'axios';

// Use the environment variable or fallback to localhost in development
const API_URL = process.env.REACT_APP_API_URL || 'http://localhost:8080';

// Create an axios instance with default config
const apiClient = axios.create({
  baseURL: API_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Type definitions
export interface Story {
  id: string;
  prompt: string;
  story: string;
  upvotes: number;
  downvotes: number;
}

// API methods
export const apiService = {
  // Fetch all stories
  getStories: async (): Promise<Story[]> => {
    const response = await apiClient.get('/prompt');
    return response.data;
  },
  
  // Create a new story
  createStory: async (prompt: string): Promise<Story> => {
    const response = await apiClient.post('/prompt/story', { prompt });
    return response.data;
  },
  
  // Upvote a story
  upvoteStory: async (storyId: string): Promise<Story> => {
    const response = await apiClient.post(`/prompt/upvote/${storyId}`);
    return response.data;
  },
  
  // Downvote a story
  downvoteStory: async (storyId: string): Promise<Story> => {
    const response = await apiClient.post(`/prompt/downvote/${storyId}`);
    return response.data;
  }
};

export default apiService;
