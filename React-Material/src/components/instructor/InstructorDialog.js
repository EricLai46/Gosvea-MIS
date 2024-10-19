import React from 'react';
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, FormControl, InputLabel, Select, MenuItem, Button } from '@mui/material';
import axiosInstance from '../AxiosInstance';
import InstructorScheduleCalendar from '../calendar/InstructorScheduleCalendar';

const InstructorDialog = ({ open, handleClose, isEditMode, currentInstructor, handleChange, handleSave, handleInsert, handleDelete,userRole }) => {
  const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST'];

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>{isEditMode ? "Edit Instructor" : "Add an Instructor"}</DialogTitle>
      <DialogContent>
        <DialogContentText>
          {isEditMode ? "Edit the information of the instructor." : "Add the information of the instructor."}
        </DialogContentText>
        <TextField
          margin="dense"
          label="Id"
          type="text"
          fullWidth
          name="id"
          value={currentInstructor.id}
          onChange={handleChange}
          InputProps={{
            readOnly: isEditMode, 
          }}
        />
        <TextField
          margin="dense"
          label="FirstName"
          type="text"
          fullWidth
          name="firstname"
          value={currentInstructor.firstname}
          onChange={handleChange}
        />
        <TextField
          margin="dense"
          label="LastName"
          type="text"
          fullWidth
          name="lastname"
          value={currentInstructor.lastname}
          onChange={handleChange}
        />
        <TextField
          margin="dense"
          label="State"
          type="text"
          fullWidth
          name="state"
          value={currentInstructor.state}
          onChange={handleChange}
        />
        <TextField
          margin="dense"
          label="City"
          type="text"
          fullWidth
          name="city"
          value={currentInstructor.city}
          onChange={handleChange}
        />
        <TextField
          margin="dense"
          label="PhoneNumber"
          type="text"
          fullWidth
          name="phoneNumber"
          value={currentInstructor.phoneNumber}
          onChange={handleChange}
        />
        <TextField
          margin="dense"
          label="Email"
          type="text"
          fullWidth
          name="email"
          value={currentInstructor.email}
          onChange={handleChange}
        />
       {userRole === 'ROLE_ICPIE' && (
  <>
    <TextField
      margin="dense"
      label="Wage Per Hour"
      type="text"
      fullWidth
      name="wageHour"
      value={currentInstructor.wageHour}
      onChange={handleChange}
    />
    <TextField
      margin="dense"
      label="Total Class Times"
      type="text"
      fullWidth
      name="totalClassTimes"
      value={currentInstructor.totalClassTimes}
      onChange={handleChange}
    />
    <TextField
      margin="dense"
      label="Deposit"
      type="text"
      fullWidth
      name="deposit"
      value={currentInstructor.deposit}
      onChange={handleChange}
    />
    <TextField
      margin="dense"
      label="Rent Manikin Numbers"
      type="text"
      fullWidth
      name="rentManikinNumbers"
      value={currentInstructor.rentManikinNumbers}
      onChange={handleChange}
    />
    <TextField
      margin="dense"
      label="Finance"
      type="text"
      fullWidth
      name="finance"
      value={currentInstructor.finance}
      onChange={handleChange}
    />
    <TextField
      margin="dense"
      label="Rent Status"
      type="text"
      fullWidth
      name="rentStatus"
      value={currentInstructor.rentStatus}
      onChange={handleChange}
    />
  </>
)}
        <TextField
          margin="dense"
          label="Fob Key"
          type="text"
          fullWidth
          name="fobKey"
          value={currentInstructor.fobKey}
          onChange={handleChange}
        />
        {isEditMode && (
        <InstructorScheduleCalendar instructorId={currentInstructor.id} />
      )}
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Cancel
        </Button>
        <Button onClick={isEditMode ? handleSave : handleInsert}>
          {isEditMode ? "Save Changes" : "Add Instructor"}
        </Button>
        {isEditMode && <Button onClick={handleDelete} color="primary">DELETE</Button>}
      </DialogActions>
    </Dialog>
  );
};

export default InstructorDialog;