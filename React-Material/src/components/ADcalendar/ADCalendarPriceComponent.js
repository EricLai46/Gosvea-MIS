import React, { useState, useEffect } from 'react';
import { Box, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Paper,Typography  } from '@mui/material';
import { LocalizationProvider, DatePicker } from '@mui/x-date-pickers';
import { AdapterDayjs } from '@mui/x-date-pickers/AdapterDayjs';
import axiosInstance from '../AxiosInstance';
import dayjs from 'dayjs';
import { useNotification } from '../NotificationContext';
const ADCalendarPriceComponent=({selectedVenue})=>{
    const { showNotification } = useNotification();
    const [venue,setVenue]=useState({});
    
    useEffect(() => {
        if (!selectedVenue) return;
        const token = localStorage.getItem('token');
        //("Stored JWT in localStorage:", token);
        
        const authHeader = 'Bearer ' + token;
          axiosInstance.get('/venue/singlevenue', {params: { venueId: selectedVenue },headers: {
            'Authorization': authHeader}  // 传递 Bearer token
          })
            .then(response => {
              if (response.data) {
                //console.log(response.data);
                setVenue(response.data); // 将返回的数据存储到 venues 状态中
                console.log(response.data);
              } else {
                showNotification('Failed to load venues!', 'error');
              }
            })
            .catch(error => {
              //console.error('Error fetching venues:', error);
              showNotification('Error fetching venues!', 'error');
            });
        }, [selectedVenue]); 
       
    // 表格数据
    const data = [
        { label: 'CPR Class', value: venue?.data?.cprPrice ?? 'N/A' },
        { label: 'Adult Class', value: venue?.data?.cpradultPrice ?? 'N/A' },
        { label: 'BLS Class', value: venue?.data?.blsPrice ?? 'N/A' },
        { label: 'CPR Instructor Class', value: venue?.data?.cprinstructorPrice ?? 'N/A' }
    ];

return (
    <Box display="flex" alignItems="center" flexDirection="column" mb={3}>
        <TableContainer component={Paper}>
            <Table>
                <TableHead>
                    <TableRow>
                    <TableCell>
                                <Typography variant="subtitle1" fontWeight="bold" fontFamily="Arial">
                                    Class Type
                                </Typography>
                            </TableCell>
                            <TableCell>
                                <Typography variant="subtitle1" fontWeight="bold" fontFamily="Arial">
                                    Long Time Price
                                </Typography>
                            </TableCell>
                    </TableRow>
                </TableHead>
                <TableBody>
                    {data.map((row, index) => (
                        <TableRow key={index}>
                            <TableCell>{row.label}</TableCell>
                            <TableCell>{row.value ?? 'N/A'}</TableCell>
                        </TableRow>
                    ))}
                </TableBody>
            </Table>
        </TableContainer>
    </Box>
);
};

export default ADCalendarPriceComponent;