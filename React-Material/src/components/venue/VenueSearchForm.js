import React from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, Button,TextField } from '@mui/material';

const VenueSearchForm = ({ venueId, state, icpisManager, setVenueId, setState, setInstructor, handleSearch, handleReset, handleExport,handleUpload, handleFileChange,selectedFile,setIcpisManager,userRole,city,setCity}) => (
  <Box display="flex" alignItems="center" mb={3}>
    {/* <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
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
    </FormControl> */}
    <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
      <InputLabel>State</InputLabel>
      <Select
        value={state}
        onChange={(e) => setState(e.target.value)}
        label="State"
      >
        <MenuItem value=""><em>Please Select</em></MenuItem>
        <MenuItem value="California">CA</MenuItem>
          <MenuItem value="Washington">WA</MenuItem>
          <MenuItem value="Arizona">AZ</MenuItem>
          <MenuItem value="Florida">FL</MenuItem>
          <MenuItem value="Georgia">GA</MenuItem>
          <MenuItem value="Indiana">IN</MenuItem>
          <MenuItem value="Maryland">MD</MenuItem>
          <MenuItem value="Michigan">MI</MenuItem>
          <MenuItem value="Missoiri">MO</MenuItem>
          <MenuItem value="New York">NY</MenuItem>
          <MenuItem value="North Carolina">NC</MenuItem>
          <MenuItem value="Ohio">OH</MenuItem>
          <MenuItem value="Pennsylvania">PA</MenuItem>
          <MenuItem value="Texas">TX</MenuItem>
          <MenuItem value="Virginia">VA</MenuItem>
      </Select>
    </FormControl>
    <TextField
        variant="outlined"
        label="City"
        value={city}
        onChange={(e) => setCity(e.target.value)}
        sx={{ minWidth: 50, mr: 2 }}
      />
    {userRole === 'ROLE_ICPIE' && (<FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
      <InputLabel>ICPISManager</InputLabel>
      <Select
        value={icpisManager}
        onChange={(e) => setIcpisManager(e.target.value)}
        label="ICPISManager"
      >
        <MenuItem value=""><em>Please Select</em></MenuItem>
        <MenuItem value="Andy">Andy</MenuItem>
        <MenuItem value="Fisher">Fisher</MenuItem>
        <MenuItem value="Jurin">Jurin</MenuItem>
        <MenuItem value="Kenny">Kenny</MenuItem>
        <MenuItem value="Daniel">Daniel</MenuItem>
        <MenuItem value="Mia">Mia</MenuItem>
      </Select>
    </FormControl>)}
    <TextField
        variant="outlined"
        label="venueId"
        value={venueId}
        onChange={(e) => setVenueId(e.target.value)}
        sx={{ minWidth: 50, mr: 2 }}
      />
    <Button variant="contained" color="primary" onClick={handleSearch} sx={{ mr: 2 }}>
      Search
    </Button>
    <Button variant="outlined" onClick={handleReset} sx={{ mr: 15 }}>
      Reset
    </Button>
    <input
      accept=".xlsx, .xls"
      type="file"
      onChange={(e)=>{handleFileChange(e);
          handleUpload();
      }}
      style={{ display: 'none' }}
      id="upload-file"
    />
    {/* <label htmlFor="upload-file">
      <Button variant="outlined" component="span" sx={{ mr: 2 }}>
        Upload
      </Button>
    </label>

    <Button variant="outlined" onClick={handleExport}>
      Export
    </Button> */}
  </Box>
);

export default VenueSearchForm;