import React, { createContext, useState, useContext,useEffect } from 'react';
import { Snackbar, Alert } from '@mui/material';

const NotificationContext = createContext();

export const useNotification = () => useContext(NotificationContext);

export const NotificationProvider = ({ children }) => {
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');
  const [notifications, setNotifications] = useState(0);
  const [notificationQueue, setNotificationQueue] = useState([]);

  const showNotification = (message, severity = 'success') => {
    console.log(`Notification - Severity: ${severity}, Message: ${message}`);
    setNotificationQueue(prevQueue => [...prevQueue, { message, severity }]);
  };

  useEffect(() => {
    if (notificationQueue.length > 0 && !snackbarOpen) {
      const { message, severity } = notificationQueue[0];
      setSnackbarMessage(message);
      setSnackbarSeverity(severity);
      setSnackbarOpen(true);
    }
  }, [notificationQueue, snackbarOpen]);

  const handleSnackbarClose = (event, reason) => {
    if (reason === 'clickaway') {
      return;
    }
    setSnackbarOpen(false);
    setNotificationQueue(prevQueue => prevQueue.slice(1));
  };

  return (
    <NotificationContext.Provider value={{ showNotification }}>
      {children}
      <Snackbar
        open={snackbarOpen}
        autoHideDuration={6000}
        onClose={handleSnackbarClose}
      >
        <Alert onClose={handleSnackbarClose} severity={snackbarSeverity}>
          {snackbarMessage}
        </Alert>
      </Snackbar>
    </NotificationContext.Provider>
  );
};