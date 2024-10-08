import React from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button } from '@mui/material';

const VenueTable = ({ currentItems, handleClickOpen }) => (
  <TableContainer>
    <Table sx={{ minWidth: 650 }}>
      <TableHead>
        <TableRow>
          <TableCell>Actions</TableCell>
          <TableCell>Id</TableCell>
          <TableCell>ICPISManager</TableCell>
          <TableCell>Address</TableCell>
          <TableCell>TimeZone</TableCell>
          <TableCell>State</TableCell>
          <TableCell>Instructor</TableCell>
          <TableCell>City</TableCell>
          <TableCell>CancellationPolicy</TableCell>
          <TableCell>PaymentMode</TableCell>
          <TableCell>NonrefundableFee</TableCell>
          <TableCell>FobKey</TableCell>
          <TableCell>Deposit</TableCell>
          <TableCell>MembershipFee</TableCell>
          <TableCell>UsageFee</TableCell>
          <TableCell>RefundableStatus</TableCell>
          <TableCell>BookMethod</TableCell>
          <TableCell>RegistrationLink</TableCell>
          
        </TableRow>
      </TableHead>
      <TableBody>
      {Array.isArray(currentItems) && currentItems.length > 0 ? (
            currentItems.map((venue) => (
              <TableRow key={venue.id}>
                <TableCell>
                  <Button variant="outlined" onClick={() => handleClickOpen(venue)}>Edit</Button>
                </TableCell>
                <TableCell>{venue.id}</TableCell>
                <TableCell>{venue.icpisManager}</TableCell>
                <TableCell>{venue.address}</TableCell>
                <TableCell>{venue.timeZone}</TableCell>
                <TableCell>{venue.state}</TableCell>
                <TableCell>
                 {venue.instructors.map((instructor, index) => (
                <span key={instructor.id}>
                  {instructor.firstname} {instructor.lastname}
                {index < venue.instructors.length - 1 && ', '}
                </span>
                  ))}
                </TableCell>
                <TableCell>{venue.city}</TableCell>
                <TableCell>{venue.cancellationPolicy}</TableCell>
                <TableCell>{venue.paymentMode}</TableCell>
                <TableCell>{venue.nonrefundableFee}</TableCell>
                <TableCell>{venue.fobKey}</TableCell>
                <TableCell>{venue.deposit}</TableCell>
                <TableCell>{venue.membershipFee}</TableCell>
                <TableCell>{venue.usageFee}</TableCell>
                <TableCell>{venue.refundableStatus}</TableCell>
                <TableCell>{venue.bookMethod}</TableCell>
                <TableCell>{venue.registrationLink}</TableCell>

              </TableRow>
            ))
          ) : (
            <TableRow>
              <TableCell colSpan={17} align="center">No data available</TableCell>
            </TableRow>
          )}
        </TableBody>
      </Table>
    </TableContainer>
  );

export default VenueTable;