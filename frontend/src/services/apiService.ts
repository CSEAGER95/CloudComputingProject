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
  },  // <-- You need this comma here
  
  // Add this to the apiService object
  getDebugInfo: async (): Promise<any> => {
    try {
      // Basic test of API connectivity
      const connectionTest = await fetch(`${API_URL}/prompt/test`, {
        method: 'GET',
        mode: 'cors'
      });
     
      // Get environment info
      return {
        apiUrl: API_URL,
        connectionTest: {
          status: connectionTest.status,
          ok: connectionTest.ok
        },
        environment: {
          userAgent: navigator.userAgent,
          timestamp: new Date().toISOString()
        }
      };
    } catch (error) {
      console.error('Error in getDebugInfo:', error);
      throw error;
    }
  }
};