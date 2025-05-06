// src/components/ApiDebugTool.jsx
import React, { useState } from 'react';

const ApiDebugTool = () => {
  const [result, setResult] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);
  const apiUrl = process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';

  const testApi = async (endpoint) => {
    setLoading(true);
    setError(null);
    setResult(null);
    
    try {
      console.log(`Testing API endpoint: ${apiUrl}${endpoint}`);
      const response = await fetch(`${apiUrl}${endpoint}`, {
        method: 'GET',
        headers: { 'Accept': 'application/json' },
        mode: 'cors',
        credentials: 'omit'
      });
      
      console.log('Response status:', response.status);
      
      if (!response.ok) {
        throw new Error(`HTTP error ${response.status}`);
      }
      
      const data = await response.json();
      console.log('Response data:', data);
      setResult(data);
    } catch (err) {
      console.error('API test failed:', err);
      setError(err.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ 
      position: 'fixed', 
      bottom: '10px', 
      left: '10px', 
      backgroundColor: '#f8f9fa', 
      padding: '10px',
      border: '1px solid #ddd',
      borderRadius: '5px',
      zIndex: 1000,
      maxWidth: '500px',
      maxHeight: '300px',
      overflow: 'auto'
    }}>
      <h3>API Debug Tool</h3>
      <div style={{ display: 'flex', gap: '5px', marginBottom: '10px' }}>
        <button onClick={() => testApi('/prompt')} disabled={loading}>
          Test /prompt
        </button>
        <button onClick={() => testApi('/prompt/test')} disabled={loading}>
          Test /prompt/test
        </button>
        <button onClick={() => testApi('/prompt/testdatastore')} disabled={loading}>
          Test Datastore
        </button>
      </div>
      
      {loading && <div>Loading...</div>}
      
      {error && (
        <div style={{ color: 'red', marginTop: '10px' }}>
          <strong>Error:</strong> {error}
        </div>
      )}
      
      {result && (
        <div style={{ marginTop: '10px' }}>
          <strong>Result:</strong>
          <pre style={{ 
            backgroundColor: '#eee', 
            padding: '5px', 
            overflowX: 'auto',
            fontSize: '12px' 
          }}>
            {JSON.stringify(result, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
};

export default ApiDebugTool;