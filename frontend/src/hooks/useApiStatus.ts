// Create useApiStatus.ts hook
import { useState } from 'react';

export const useApiStatus = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  
  const handleApiCall = async <T,>(apiCall: () => Promise<T>): Promise<T | null> => {
    setIsLoading(true);
    setError(null);
    
    try {
      const result = await apiCall();
      return result;
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Unknown error';
      setError(`Error: ${errorMessage}`);
      return null;
    } finally {
      setIsLoading(false);
    }
  };
  
  return { isLoading, error, setError, handleApiCall };
};