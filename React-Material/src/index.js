import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import { NotificationProvider } from './components/NotificationContext';

const container = document.getElementById('root');


const root = ReactDOM.createRoot(container);


root.render(
  
  <React.StrictMode>
  <NotificationProvider>
    <App />
  </NotificationProvider>
  </React.StrictMode>
 
);