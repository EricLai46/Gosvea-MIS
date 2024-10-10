import React,{useState} from 'react';
import { Box, FormControl, InputLabel, Select, MenuItem, Button,TextField } from '@mui/material';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import dayjs from 'dayjs';
const ReportSearchForm=({date,setDate,handleSearch})=>{
   

   return(
    <LocalizationProvider dateAdapter={AdapterDayjs}>
    <Box display="flex" alignItems="center" mb={3}>

    

      {/* DateTimePicker for 'from' and 'to' time range */}
      <DatePicker
        label="AD Publish Date"
        value={date?dayjs(date):null}
        onChange={(newValue) => {
          const formattedDate = dayjs(newValue).format('YYYY-MM-DD');  // 使用 dayjs 格式化日期
          console.log('Formatted From Date:', formattedDate);
          setDate(formattedDate);
        }}
        shouldDisableDate={(date) => {
            // 定义基准日期
            const baseDate = dayjs('2024-09-13');
            
            // 计算选择的日期与基准日期的差异（以天为单位）
            const diffInDays = dayjs(date).diff(baseDate, 'day');
        
            // 如果差异是14的倍数，则日期可选，否则禁用
            return diffInDays % 14 !== 0;
          }}
        renderInput={(params) => <TextField {...params} sx={{ mr: 2 }} />}
      />

      

      <Button variant="contained" color="primary" onClick={handleSearch} sx={{ mr: 2 }}>
        Export
      </Button>
      
    </Box>
  </LocalizationProvider>
   );
};

export default ReportSearchForm;