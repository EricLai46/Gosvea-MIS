import React from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, TextField, Button } from '@mui/material';

const InstructorSearchForm = ({  state, setState, email, setEmail, firstname, setFirstname, lastname, setLastname, phonenumber, setPhonenumber, handleSearch, handleReset }) => {
  const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST'];

  return (
    <Box display="flex" alignItems="center" mb={3}>

      <FormControl variant="outlined" sx={{ minWidth: 100, mr: 2 }}>
        <InputLabel>State</InputLabel>
        <Select
          value={state}
          onChange={(e) => setState(e.target.value)}
          label="State"
        >
          <MenuItem value=""><em>Please Select</em></MenuItem>
          <MenuItem value="ca">CA</MenuItem>
          <MenuItem value="wa">WA</MenuItem>
          <MenuItem value="az">AZ</MenuItem>
        </Select>
      </FormControl>
      <TextField
        variant="outlined"
        label="Email"
        value={email}
        onChange={(e) => setEmail(e.target.value)}
        sx={{ minWidth: 50, mr: 2 }}
      />
      <TextField
        variant="outlined"
        label="LastName"
        value={lastname}
        onChange={(e) => setLastname(e.target.value)}
        sx={{ minWidth: 50, mr: 2 }}
      />
      <TextField
        variant="outlined"
        label="FirstName"
        value={firstname}
        onChange={(e) => setFirstname(e.target.value)}
        sx={{ minWidth: 50, mr: 2 }}
      />
      <TextField
        variant="outlined"
        label="PhoneNumber"
        value={phonenumber}
        onChange={(e) => setPhonenumber(e.target.value)}
        sx={{ minWidth: 50, mr: 2 }}
      />
      <Button variant="contained" color="primary" onClick={handleSearch} sx={{ mr: 2 }}>
        Search
      </Button>
      <Button variant="outlined" onClick={handleReset}>
        Reset
      </Button>
      <Box sx={{ flexGrow: 1 }} />
    </Box>
  );
};

export default InstructorSearchForm;


