import React,{useState} from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, Button,TextField } from '@mui/material';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
const CourseSearchForm = ({ setVenueId, fromDate, venueId, setFromDate, toDate,setToDate, handleSearch, handleReset,active,processed,setActive,setProcessed,setIcpisManager,icpisManager }) => {

  return (
    <LocalizationProvider dateAdapter={AdapterDayjs}>
      <Box display="flex" alignItems="center" mb={3}>

        <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
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
          </Select>
        </FormControl>

        <TextField
          variant="outlined"
          label="VenueId"
          value={venueId}
          onChange={(e) => setVenueId(e.target.value)}
          sx={{ minWidth: 50, mr: 2 }}
        />

        <FormControl variant="outlined" sx={{ minWidth: 100, mr: 2 }}>
          <InputLabel>IsPublished</InputLabel>
          <Select
            value={active}
            onChange={(e) => setActive(e.target.value)}
            label="IsPublished"
          >
            <MenuItem value=""><em>Please Select</em></MenuItem>
            <MenuItem value="true">true</MenuItem>
            <MenuItem value="false">false</MenuItem>
          </Select>
        </FormControl>

        <FormControl variant="outlined" sx={{ minWidth: 100, mr: 2 }}>
          <InputLabel>IsProcessed</InputLabel>
          <Select
            value={processed}
            onChange={(e) => setProcessed(e.target.value)}
            label="IsProcessed"
          >
            <MenuItem value=""><em>Please Select</em></MenuItem>
            <MenuItem value="true">true</MenuItem>
            <MenuItem value="false">false</MenuItem>
          </Select>
        </FormControl>

        {/* DateTimePicker for 'from' and 'to' time range */}
        <DatePicker
          label="From"
          value={fromDate ? dayjs(fromDate) : null}
          onChange={(newValue) => {
            const formattedDate = dayjs(newValue).format('YYYY-MM-DD');  // 使用 dayjs 格式化日期
            console.log('Formatted From Date:', formattedDate);
            setFromDate(formattedDate);
          }}
          renderInput={(params) => <TextField {...params} sx={{ mr: 2 }} />}
        />

        <DatePicker
          label="To"
          value={toDate ? dayjs(toDate) : null}
          onChange={(newValue) => {
            const tomattedDate = dayjs(newValue).format('YYYY-MM-DD');  // 使用 dayjs 格式化日期
            console.log('Formatted From Date:',tomattedDate);
            setToDate(tomattedDate);
          }}
          renderInput={(params) => <TextField {...params} sx={{ mr: 2 }} />}
        />

        <Button variant="contained" color="primary" onClick={handleSearch} sx={{ mr: 2 }}>
          Search
        </Button>
        <Button variant="outlined" onClick={handleReset}>
          Reset
        </Button>
      </Box>
    </LocalizationProvider>
  );
};

export default CourseSearchForm;