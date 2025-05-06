// src/App.tsx
import React, { useState, useEffect } from 'react';
import './App.css';
import Form from './components/Form';
import StoryList from './components/StoryList';
import { api } from './services/api';

// Set the global API URL
window.API_URL = process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';

function App() {
  const [apiStatus, setApiStatus] = useState<'loading' | 'connected' | 'error'>('loading');
  const [connectionError, setConnectionError] = useState<string | null>(null);

  useEffect(() => {
    // Test connection to the API
    const checkConnection = async () => {
      try {
        console.log('Testing connection to API:', window.API_URL);
        const isConnected = await api.checkConnection();
        setApiStatus(isConnected ? 'connected' : 'error');
        if (!isConnected) {
          setConnectionError('Could not connect to the backend API');
        }
      } catch (error) {
        console.error('Connection error:', error);
        setApiStatus('error');
        setConnectionError(error instanceof Error ? error.message : 'Unknown connection error');
      }
    };

    checkConnection();
  }, []);

  // Retry connection
  const retryConnection = () => {
    setApiStatus('loading');
    setConnectionError(null);
    
    // Wait a moment before retrying
    setTimeout(() => {
      window.location.reload();
    }, 1000);
  };

  return (
    <div className="App">
      <header style={{ 
        padding: '30px 20px', 
        backgroundColor: '#f8f9fa', 
        marginBottom: '30px',
        borderBottom: '1px solid #e5e5e5'
      }}>
        <h1 style={{ margin: '0 0 10px 0', color: '#333' }}>McSeager Satirical News Generator</h1>
        <p style={{ margin: 0, color: '#666', fontSize: '18px' }}>
          Generate hilarious satire news stories in the style of The Onion
        </p>
        
        {/* API Status Indicator */}
        <div style={{
          display: 'inline-block',
          marginTop: '15px',
          padding: '5px 10px',
          borderRadius: '20px',
          fontSize: '14px',
          backgroundColor: apiStatus === 'connected' ? '#e8f5e9' : 
                          apiStatus === 'loading' ? '#fff8e1' : '#ffebee',
          color: apiStatus === 'connected' ? '#4caf50' : 
                apiStatus === 'loading' ? '#ff9800' : '#d32f2f'
        }}>
          {apiStatus === 'connected' ? '✓ Connected to API' : 
           apiStatus === 'loading' ? '⟳ Connecting...' : '✗ API Connection Error'}
        </div>
      </header>
      
      {/* Main Content */}
      <main style={{ 
        padding: '0 20px',
        maxWidth: '1000px',
        margin: '0 auto'
      }}>
        {apiStatus === 'error' ? (
          <div style={{
            backgroundColor: '#ffebee',
            padding: '20px',
            borderRadius: '8px',
            marginBottom: '30px',
            textAlign: 'center'
          }}>
            <h2 style={{ color: '#d32f2f', marginTop: 0 }}>Connection Error</h2>
            <p>
              Could not connect to the backend API. This might be due to CORS restrictions or the backend service being unavailable.
            </p>
            <p style={{ fontWeight: 'bold' }}>
              Error details: {connectionError}
            </p>
            <button 
              onClick={retryConnection}
              style={{
                backgroundColor: '#d32f2f',
                color: 'white',
                border: 'none',
                padding: '10px 20px',
                borderRadius: '5px',
                cursor: 'pointer',
                fontSize: '16px'
              }}
            >
              Retry Connection
            </button>
          </div>
        ) : (
          <>
            <section style={{ marginBottom: '40px' }}>
              <h2 style={{ color: '#333', marginBottom: '20px' }}>Create New Satire</h2>
              <Form />
            </section>
            
            <hr style={{ margin: '40px 0', border: '0', borderTop: '1px solid #eee' }} />
            
            <section>
              <StoryList />
            </section>
          </>
        )}
      </main>
      
      <footer style={{
        padding: '30px 20px',
        backgroundColor: '#f8f9fa',
        marginTop: '60px',
        fontSize: '14px',
        color: '#6c757d',
        textAlign: 'center',
        borderTop: '1px solid #e5e5e5'
      }}>
        <p style={{ margin: 0 }}>McSeager Spring 2025 • Cloud Computing Project</p>
        <p style={{ margin: '5px 0 0 0', fontSize: '12px' }}>
          API: {window.API_URL}
        </p>
      </footer>
    </div>
  );
}

// Add this to TypeScript declaration
declare global {
  interface Window {
    API_URL: string;
  }
}

export default App;