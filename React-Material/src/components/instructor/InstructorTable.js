import React from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button } from '@mui/material';

const InstructorTable = ({ currentItems, handleClickOpen,userRole }) => {
  return (
    <TableContainer>
      <Table sx={{ minWidth: 650 }}>
        <TableHead>
          <TableRow>
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Id</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '25px', whiteSpace: 'nowrap' }}>VenueId</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>FirstName</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>LastName</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>State</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '20px', whiteSpace: 'nowrap' }}>City</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>PhoneNumber</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Email</TableCell>
            {userRole === 'ROLE_ICPIE'  && (
  <>
    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Wage Per Hour</TableCell>
    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Salary Info</TableCell>
    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Total Classes Times</TableCell>
    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Deposit</TableCell>
    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Rent Manikin Numbers</TableCell>
    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Finance</TableCell>
    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Rent Status</TableCell>
  </>
)}
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Fob Key</TableCell>
            <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}></TableCell>
          </TableRow>
        </TableHead>
        <TableBody>
        {Array.isArray(currentItems) && currentItems.length > 0 ? (
            currentItems.map((instructor) => (
              <TableRow key={instructor.id}>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.id}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.venueId}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.firstname}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.lastname}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.state}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.city}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.phoneNumber}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.email}</TableCell>
                {userRole === 'ROLE_ICPIE' && (
      <>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.wageHour}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.salaryInfo}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.totalClassTimes}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.deposit}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.rentManikinNumbers}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.finance}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.rentStatus}</TableCell>
  </>
        )}

                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.fobKey}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>
                  <Button variant="outlined" onClick={() => handleClickOpen(instructor)}>Edit</Button>
                </TableCell>
              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={16} align="center">No data available</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );
};
export default InstructorTable;