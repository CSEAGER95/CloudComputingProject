import React, { useEffect } from 'react';
import './App.css';
import Form from './components/Form';
import StoryList from './components/StoryList';

// Explicitly set the API URL in a global variable
window.API_URL = process.env.REACT_APP_API_URL || 'https://teamprojectmccewenseager.ue.r.appspot.com';
console.log('Setting global API URL:', window.API_URL);

function App() {
  // Log API URL on component mount for debugging
  useEffect(() => {
    console.log('App mounted - API URL:', window.API_URL);
    // Test connection to the API
    fetch(`${window.API_URL}/prompt/test`, { mode: 'cors' })
      .then(response => {
        console.log('Backend connection test:', response.ok ? 'SUCCESS' : 'FAILED');
      })
      .catch(error => {
        console.error('Backend connection test failed:', error);
      });
  }, []);

  return (
    <div className="App">
      <header style={{ padding: '20px', backgroundColor: '#f8f9fa', marginBottom: '20px' }}>
        <h1>McSeager Satirical News Generator</h1>
        <p>Generate hilarious satire news stories in the style of The Onion</p>
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
        <p>McSeager Spring 2025 • Cloud Computing Project</p>
      </footer>
      
      {/* API URL Display for debugging */}
      <div style={{position: 'fixed', bottom: '5px', left: '5px', fontSize: '12px', color: '#999'}}>
        API: {window.API_URL || 'not set'}
      </div>
    </div>
  );
}

// Add this to TypeScript declaration to avoid errors
declare global {
  interface Window {
    API_URL: string;
  }
}

export default App;