import React from 'react';
import logo from './logo.svg';
import './App.css';
import Booking from './components/Booking';

function App() {
  return (
    <div className="App">
      <header className="App-header">
        <div>
          <h1>Booking App</h1>
          <Booking />
        </div>
      </header>
    </div>
  );
}

export default App;
