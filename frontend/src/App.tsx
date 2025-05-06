// App.tsx with debugging info
import React, { useState, useEffect } from 'react';
import './App.css';
import Form from './components/Form';
import StoryList from './components/StoryList';
import apiService from './services/apiService';
import ApiDebugger from './components/ApiDebugger';
import ApiDebugTool from './components/ApiDebugTool';

function App() {
  const [debugMode, setDebugMode] = useState(false);
  const [apiStatus, setApiStatus] = useState<'checking' | 'connected' | 'error'>('checking');
  const [apiError, setApiError] = useState<string | null>(null);

  useEffect(() => {
    // Check connection to backend
    const checkConnection = async () => {
      try {
        await apiService.testConnection();
        setApiStatus('connected');
      } catch (error) {
        setApiStatus('error');
        if (error instanceof Error) {
          setApiError(error.message);
        } else {
          setApiError('Unknown error');
        }
      }
    };
    
    checkConnection();
  }, []);

  // Toggle debug mode with keyboard shortcut (Ctrl+Shift+D)
  useEffect(() => {
    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.ctrlKey && e.shiftKey && e.key === 'D') {
        setDebugMode(prevMode => !prevMode);
      }
    };
    
    window.addEventListener('keydown', handleKeyDown);
    return () => window.removeEventListener('keydown', handleKeyDown);
  }, []);

  return (
    <div className="App">
      <header style={{ padding: '20px', backgroundColor: '#f8f9fa', marginBottom: '20px' }}>
        <h1>McSeager Satirical News Generator</h1>
        <p>Generate hilarious satire news stories in the style of The Onion</p>
        
        {/* Debug mode indicator */}
        {debugMode && (
          <div style={{
            marginTop: '10px',
            padding: '10px',
            backgroundColor: '#e9ecef',
            borderRadius: '4px',
            fontSize: '14px',
            textAlign: 'left'
          }}>
            <div><strong>API URL:</strong> {process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com'}</div>
            <div><strong>API Status:</strong> {apiStatus === 'checking' ? 'Checking...' :
                                             apiStatus === 'connected' ? '✅ Connected' :
                                             '❌ Error connecting'}</div>
            {apiError && <div><strong>Error:</strong> {apiError}</div>}
            <div><strong>Debug Mode:</strong> Enabled (Ctrl+Shift+D to toggle)</div>
          </div>
        )}
      </header>
      
      <main style={{ padding: '0 20px' }}>
        <section>
          <h2>Create New Satire</h2>
          <Form />
        </section>
        
        <hr style={{ margin: '40px 0', border: '0', borderTop: '1px solid #eee' }} />
        
        <section>
          <StoryList />
        </section>
      </main>
      
      <footer style={{
        padding: '20px',
        backgroundColor: '#f8f9fa',
        marginTop: '40px',
        fontSize: '14px',
        color: '#6c757d'
      }}>
        <p>
          McSeager Spring 2025 • Cloud Computing Project
          {!debugMode && (
            <span style={{ marginLeft: '10px', fontSize: '12px', color: '#adb5bd' }}>
              (Press Ctrl+Shift+D for debug mode)
            </span>
          )}
        </p>
      </footer>
      <ApiDebugTool />
      <ApiDebugger />
    </div>
  );
}

export default App;