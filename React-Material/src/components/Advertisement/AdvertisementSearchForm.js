import React from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, Button } from '@mui/material';

const AdvertisementSearchForm = ({ timeZone, state, instructor, setTimeZone, setState, setInstructor, handleSearch, handleReset }) => (
  <Box display="flex" alignItems="center" mb={3}>
    <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
      <InputLabel>Time Zone</InputLabel>
      <Select
        value={timeZone}
        onChange={(e) => setTimeZone(e.target.value)}
        label="Time Zone"
      >
        <MenuItem value=""><em>Please Select</em></MenuItem>
        <MenuItem value="PST">PST</MenuItem>
        <MenuItem value="CST">CST</MenuItem>
        <MenuItem value="MST">MST</MenuItem>
        <MenuItem value="EST">EST</MenuItem>
      </Select>
    </FormControl>
    <FormControl variant="outlined" sx={{ minWidth: 200, mr: 60 }}>
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
   
    <Button variant="contained" color="primary" onClick={handleSearch} sx={{ mr: 2 }}>
      Search
    </Button>
    <Button variant="outlined" onClick={handleReset}>
      Reset
    </Button>
  </Box>
);

export default AdvertisementSearchForm;