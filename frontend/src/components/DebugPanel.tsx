// src/components/DebugPanel.tsx
import React, { useState, useEffect } from 'react';

const DebugPanel: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [debugInfo, setDebugInfo] = useState<any>({});
  const [isChecking, setIsChecking] = useState(false);

  const apiUrl = window.API_URL || process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';

  const runDiagnostics = async () => {
    setIsChecking(true);
    const info: any = {
      timestamp: new Date().toISOString(),
      apiUrl: apiUrl,
      environment: {
        windowApiUrl: window.API_URL,
        envApiUrl: process.env.REACT_APP_API_URL,
        userAgent: navigator.userAgent,
        location: window.location.href
      },
      endpoints: {}
    };

    // Test endpoints
    try {
      const testResponse = await fetch(`${apiUrl}/prompt/test`, {
        method: 'GET',
        mode: 'cors',
        headers: { 'Accept': 'application/json' }
      });
      info.endpoints.test = {
        status: testResponse.status,
        ok: testResponse.ok,
        statusText: testResponse.statusText
      };
    } catch (error) {
      info.endpoints.test = { error: error instanceof Error ? error.message : String(error) };
    }

    try {
      const storiesResponse = await fetch(`${apiUrl}/prompt`, {
        method: 'GET',
        mode: 'cors',
        headers: { 'Accept': 'application/json' }
      });
      info.endpoints.stories = {
        status: storiesResponse.status,
        ok: storiesResponse.ok,
        statusText: storiesResponse.statusText
      };
      
      if (storiesResponse.ok) {
        const data = await storiesResponse.json();
        info.endpoints.stories.count = Array.isArray(data) ? data.length : 'not an array';
        info.endpoints.stories.data = Array.isArray(data) ? data.slice(0, 2) : data;
      }
    } catch (error) {
      info.endpoints.stories = { error: error instanceof Error ? error.message : String(error) };
    }

    setDebugInfo(info);
    setIsChecking(false);
  };

  return (
    <div style={{ position: 'fixed', bottom: '10px', right: '10px', zIndex: 9999 }}>
      {visible ? (
        <div style={{
          width: '400px',
          maxHeight: '400px',
          overflow: 'auto',
          backgroundColor: '#f8f9fa',
          border: '1px solid #ddd',
          borderRadius: '5px',
          padding: '10px'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
            <h3 style={{ margin: 0 }}>API Diagnostics</h3>
            <button onClick={() => setVisible(false)}>✕</button>
          </div>
          
          <button 
            onClick={runDiagnostics}
            disabled={isChecking}
            style={{
              padding: '5px 10px',
              marginBottom: '10px'
            }}
          >
            {isChecking ? 'Checking...' : 'Run Diagnostics'}
          </button>
          
          <pre style={{ margin: 0, fontSize: '12px', whiteSpace: 'pre-wrap' }}>
            {JSON.stringify(debugInfo, null, 2)}
          </pre>
        </div>
      ) : (
        <button 
          onClick={() => setVisible(true)}
          style={{
            backgroundColor: '#0070f3',
            color: 'white',
            border: 'none',
            borderRadius: '5px',
            padding: '5px 10px'
          }}
        >
          Debug API
        </button>
      )}
    </div>
  );
};

export default DebugPanel;