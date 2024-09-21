import React,{useState} from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, Button,TextField } from '@mui/material';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';

const ADSearchForm=({handleReset,handleSearch,venues,setCurrentvenue})=>{
    const [selectedVenue, setSelectedVenue] = useState('');

    // 处理场地选择变化
    const handleVenueChange = (event) => {
      const selectedId = event.target.value;
      setSelectedVenue(selectedId); // 更新本地状态
      setCurrentvenue(venues.find(venue => venue.id === selectedId)); // 更新父组件中的当前场地
    };
    return (
        <LocalizationProvider dateAdapter={AdapterDayjs}>
          <Box display="flex" alignItems="center" mb={3}>
    
              {/* 场地选择下拉菜单 */}
        <FormControl sx={{ mr: 2, minWidth: 900 }}>
          <InputLabel id="venue-select-label">Select Venue</InputLabel>
          <Select
            labelId="venue-select-label"
            value={selectedVenue}
            label="Select Venue"
            onChange={handleVenueChange}
          >
            {venues.map((venue) => (
              <MenuItem key={venue.id} value={venue.id}>
                {venue.name || `${venue.address}`}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

    
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

export default ADSearchForm;