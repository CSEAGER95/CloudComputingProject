// Create ApiContext.tsx
import React, { createContext, useContext, ReactNode } from 'react';

type ApiContextType = {
  apiUrl: string;
};

const ApiContext = createContext<ApiContextType | undefined>(undefined);

export const ApiProvider: React.FC<{children: ReactNode}> = ({ children }) => {
  const apiUrl = process.env.REACT_APP_API_URL || 
                window.API_URL || 
                'https://teamprojectmccewenseager.ue.r.appspot.com';
  
  return (
    <ApiContext.Provider value={{ apiUrl }}>
      {children}
    </ApiContext.Provider>
  );
};

export const useApi = (): ApiContextType => {
  const context = useContext(ApiContext);
  if (context === undefined) {
    throw new Error('useApi must be used within an ApiProvider');
  }
  return context;
};