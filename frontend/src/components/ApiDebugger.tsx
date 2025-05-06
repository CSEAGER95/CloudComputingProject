// src/components/ApiDebugger.tsx
import React, { useState } from 'react';
import apiService from '../services/apiService';

const ApiDebugger: React.FC = () => {
  const [visible, setVisible] = useState(false);
  const [debugInfo, setDebugInfo] = useState<any>(null);
  const [loading, setLoading] = useState(false);
  
  const runDiagnostics = async () => {
    setLoading(true);
    try {
      const info = await apiService.getDebugInfo();
      setDebugInfo(info);
    } catch (error) {
      setDebugInfo({ error: error instanceof Error ? error.message : 'Unknown error' });
    } finally {
      setLoading(false);
    }
  };
  
  if (!visible) {
    return (
      <button 
        onClick={() => setVisible(true)}
        style={{
          position: 'fixed',
          bottom: '10px',
          right: '10px',
          padding: '8px 12px',
          backgroundColor: '#333',
          color: 'white',
          border: 'none',
          borderRadius: '4px',
          cursor: 'pointer',
          fontSize: '12px',
          zIndex: 1000
        }}
      >
        Debug API
      </button>
    );
  }
  
  return (
    <div style={{
      position: 'fixed',
      bottom: '10px',
      right: '10px',
      width: '400px',
      backgroundColor: '#f5f5f5',
      border: '1px solid #ddd',
      borderRadius: '4px',
      padding: '10px',
      boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
      zIndex: 1000,
      fontFamily: 'monospace',
      fontSize: '12px'
    }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', marginBottom: '10px' }}>
        <h3 style={{ margin: 0 }}>API Diagnostics</h3>
        <button 
          onClick={() => setVisible(false)}
          style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: '16px' }}
        >
          ✕
        </button>
      </div>
      
      <div>
        <button 
          onClick={runDiagnostics}
          disabled={loading}
          style={{
            padding: '5px 10px',
            backgroundColor: '#0070f3',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
            opacity: loading ? 0.7 : 1
          }}
        >
          {loading ? 'Running...' : 'Run Diagnostics'}
        </button>
      </div>
      
      {debugInfo && (
        <div style={{ marginTop: '10px', maxHeight: '300px', overflow: 'auto' }}>
          <pre style={{ margin: 0, whiteSpace: 'pre-wrap' }}>
            {JSON.stringify(debugInfo, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
};

export default ApiDebugger;