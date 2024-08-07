import React , { useState, useEffect } from 'react';
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, FormControl, InputLabel, Select, MenuItem, Button } from '@mui/material';
import VenueScheduleCalendar from '../calendar/VenueScheduleCalendar';
import axiosInstance from '../AxiosInstance';

const VenueDialog = ({ open, handleClose, isEditMode, currentVenue, handleChange, handleSave, handleInsert, handleDelete, timeZones }) => {

  const [instructors, setInstructors] = useState([]);


  useEffect(() => {
    if (open) {
      axiosInstance.get('/instructor/instructorname')
        .then(response => {
          //console.log(response.data); 
          setInstructors(response.data);
        })
        .catch(error => {
          console.error('Error fetching instructors:', error);
        });
    }
  }, [open]);


  return(
  <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
    <DialogTitle>{isEditMode ? "Edit Venue" : "Add a Venue"}</DialogTitle>
    <DialogContent>
      <DialogContentText>
        {isEditMode ? "Edit the information of the venue." : "Add the information of the venue"}
      </DialogContentText>
      <TextField
        margin="dense"
        label="Address"
        type="text"
        fullWidth
        name="address"
        value={currentVenue.address}
        onChange={handleChange}
      />
      <FormControl margin="dense" fullWidth>
        <InputLabel>Time Zone</InputLabel>
        <Select
          name="timeZone"
          value={currentVenue.timeZone}
          onChange={handleChange}
        >
          {timeZones.map((tz) => (
            <MenuItem key={tz} value={tz}>
              {tz}
            </MenuItem>
          ))}
        </Select>
      </FormControl>
      <TextField
        margin="dense"
        label="State"
        type="text"
        fullWidth
        name="state"
        value={currentVenue.state}
        onChange={handleChange}
      />
      <FormControl margin="dense" fullWidth>
          <InputLabel>Instructor</InputLabel>
          <Select
            name="instructor"
            value={currentVenue.instructor || ''}
            onChange={handleChange}
          >
            {instructors.map(instructor => (
              <MenuItem key={instructor.id} value={instructor.id}>
                {instructor.fullname}
              </MenuItem>
            ))}
          </Select>
        </FormControl>
      <TextField
        margin="dense"
        label="City"
        type="text"
        fullWidth
        name="city"
        value={currentVenue.city}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Cancellation Policy"
        type="text"
        fullWidth
        name="cancellationPolicy"
        value={currentVenue.cancellationPolicy}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Payment Mode"
        type="text"
        fullWidth
        name="paymentMode"
        value={currentVenue.paymentMode}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Nonrefundable Fee"
        type="text"
        fullWidth
        name="nonrefundableFee"
        value={currentVenue.nonrefundableFee}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Fob Key"
        type="text"
        fullWidth
        name="fobKey"
        value={currentVenue.fobKey}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Deposit"
        type="text"
        fullWidth
        name="deposit"
        value={currentVenue.deposit}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Membership Fee"
        type="text"
        fullWidth
        name="membershipFee"
        value={currentVenue.membershipFee}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Usage Fee"
        type="text"
        fullWidth
        name="usageFee"
        value={currentVenue.usageFee}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Refundable Status"
        type="text"
        fullWidth
        name="refundableStatus"
        value={currentVenue.refundableStatus}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Book Method"
        type="text"
        fullWidth
        name="bookMethod"
        value={currentVenue.bookMethod}
        onChange={handleChange}
      />
      <TextField
        margin="dense"
        label="Registration Link"
        type="text"
        fullWidth
        name="registrationLink"
        value={currentVenue.registrationLink}
        onChange={handleChange}
      />
        {isEditMode && (
        <VenueScheduleCalendar venueId={currentVenue.id} />
      )}
    </DialogContent>
    <DialogActions>
      <Button onClick={handleClose} color="primary">
        Cancel
      </Button>
      <Button onClick={isEditMode ? handleSave : handleInsert}>
        {isEditMode ? "Save Changes" : "Add Venue"}
      </Button>
      {isEditMode && <Button onClick={handleDelete} color="primary">DELETE</Button>}
    </DialogActions>
  </Dialog>
  );
};

export default VenueDialog;