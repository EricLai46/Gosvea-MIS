import React from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, Button } from '@mui/material';

const CourseSearchForm = ({ timeZone, state, instructor, setTimeZone, setState, setInstructor, handleSearch, handleReset,isActive,isProcessed,setIsActive,setIsProcessed }) => (
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
    <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
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
    {/* <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
      <InputLabel>Instructor</InputLabel>
      <Select
        value={instructor}
        onChange={(e) => setInstructor(e.target.value)}
        label="Instructor"
      >
        <MenuItem value=""><em>Please Select</em></MenuItem>
        <MenuItem value="1256">1</MenuItem>
      </Select>
    </FormControl> */}
    <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
      <InputLabel>IsPublished</InputLabel>
      <Select
        value={isActive}
        onChange={(e) => setIsActive(e.target.value)}
        label="isPublisded"
      >
        <MenuItem value=""><em>Please Select</em></MenuItem>
        <MenuItem value="true">true</MenuItem>
        <MenuItem value="false">false</MenuItem>
      </Select>
    </FormControl>
    <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
      <InputLabel>IsProcessed</InputLabel>
      <Select
        value={isProcessed}
        onChange={(e) => setIsProcessed(e.target.value)}
        label="isProcessed"
      >
        <MenuItem value=""><em>Please Select</em></MenuItem>
        <MenuItem value="true">true</MenuItem>
        <MenuItem value="false">false</MenuItem>
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

export default CourseSearchForm;