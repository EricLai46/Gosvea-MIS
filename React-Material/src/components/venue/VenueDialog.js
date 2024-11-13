import React, { useState, useEffect } from 'react';
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, FormControl, InputLabel, Select, MenuItem, Button, Grid } from '@mui/material';
import VenueScheduleCalendar from '../calendar/VenueScheduleCalendar';
import axiosInstance from '../AxiosInstance';

const VenueDialog = ({ open, handleClose, isEditMode, currentVenue, handleChange, handleSave, handleInsert, handleDelete, timeZones,instructors,setInstructors,icpisManager,userRole,icpisname }) => {
  const [isChecking, setIsChecking] = useState(false);
  const [error, setError] = useState('');
  const [idValid, setIdValid] = useState(true); // State for ID validation
  const checkIdExists = async () => {
    try {
      setIsChecking(true);
      const response = await axiosInstance.get(`/venue/checkIdExists/${currentVenue.id}`);
      setIsChecking(false);
      return response.data.exists; // Assumes API returns { exists: true/false }
    } catch (error) {
      setIsChecking(false);
      console.error("Error checking venue ID", error);
      setError("Error checking venue ID");
      return false;
    }
  };
  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>{isEditMode ? "Edit Venue" : "Add a Venue"}</DialogTitle>
      <DialogContent>
        <DialogContentText>
          {isEditMode ? "Edit the information of the venue." : "Add the information of the venue"}
        </DialogContentText>
        
        <Grid container spacing={-5}></Grid>
        <Grid item xs={6}>
            <TextField
              margin="dense"
              label="ID"
              type="text"
              fullWidth
              name="id"
              value={currentVenue.id}
              onChange={handleChange}
              InputProps={{
                readOnly: isEditMode, // 如果 editMode 为 true，则设置为只读
              }}
            />
       </Grid>
        <Grid container spacing={2}>
          <Grid item xs={12}>
            <TextField
              margin="dense"
              label="Address"
              type="text"
              fullWidth
              name="address"
              value={currentVenue.address}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
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
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="State"
              type="text"
              fullWidth
              name="state"
              value={currentVenue.state}
              onChange={handleChange}
            />
          </Grid>
          <Grid item xs={6}>
  <FormControl margin="dense" fullWidth>
    <InputLabel>Instructors</InputLabel>
    <Select
      name="instructors"
      multiple
      value={Array.isArray(currentVenue.instructors) ? currentVenue.instructors.map(i => i.id) : []}
      onChange={handleChange}
      renderValue={(selected) => selected.map(id => {
        const instructor = instructors.find(i => i.id === id);
        //console.log("current venues instructors", currentVenue.instructors);
        //console.log('Selected ID:', id);
        //console.log('Found instructor:', instructor);

        return instructor ? instructor.fullname : '';  // 如果找到对应的instructor，显示其fullname
      }).join(', ')}  // 使用逗号分隔多个instructor的fullname
    >
       {/* 对 instructors 按 fullname 从A到Z排序 */}
    {instructors
      .slice() // 复制数组，避免修改原始数据
      .sort((a, b) => a.fullname.localeCompare(b.fullname)) // 按 fullname 排序
      .map(instructor => (
        <MenuItem key={instructor.id} value={instructor.id}>
          {instructor.fullname}  
        </MenuItem>
      ))}
    </Select>
  </FormControl>
</Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="City"
              type="text"
              fullWidth
              name="city"
              value={currentVenue.city}
              onChange={handleChange}
            />
          </Grid>
          <Grid item xs={6}>
          <FormControl margin="dense" fullWidth>
      <InputLabel>ICPIS Manager</InputLabel>
      <Select
        name="icpisManager"
        value={currentVenue.icpisManager}
        onChange={handleChange}
        disabled={userRole !== 'ROLE_ICPIE'}  // 如果不是 ROLE_ICPIE 则禁用 Select
      >
        {userRole === 'ROLE_ICPIE' ? (
          // ROLE_ICPIE 用户的完整选项
          [
            <MenuItem value="Fisher">Fisher</MenuItem>,
            <MenuItem value="Jurin">Jurin</MenuItem>,
            <MenuItem value="Andy">Andy</MenuItem>,
            <MenuItem value="Kenny">Kenny</MenuItem>,
            <MenuItem value="Daniel">Daniel</MenuItem>,
            <MenuItem value="Mia">Mia</MenuItem>,
            <MenuItem value="Daya">Daya</MenuItem>,
            <MenuItem value="Jianan">Jianan</MenuItem>,
            <MenuItem value="Matilda">Matilda</MenuItem>
          ]
        ) : (
          // 非 ROLE_ICPIE 用户，只能选择 icpisname
          <MenuItem value={icpisname}>{icpisname}</MenuItem>
        )}
      </Select>
    </FormControl>
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Cancellation Policy"
              type="text"
              fullWidth
              name="cancellationPolicy"
              value={currentVenue.cancellationPolicy}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Payment Mode"
              type="text"
              fullWidth
              name="paymentMode"
              value={currentVenue.paymentMode}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Nonrefundable Fee"
              type="text"
              fullWidth
              name="nonrefundableFee"
              value={currentVenue.nonrefundableFee}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Fob Key"
              type="text"
              fullWidth
              name="fobKey"
              value={currentVenue.fobKey}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Deposit"
              type="text"
              fullWidth
              name="deposit"
              value={currentVenue.deposit}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Membership Fee"
              type="text"
              fullWidth
              name="membershipFee"
              value={currentVenue.membershipFee}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Usage Fee"
              type="text"
              fullWidth
              name="usageFee"
              value={currentVenue.usageFee}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Refundable Status"
              type="text"
              fullWidth
              name="refundableStatus"
              value={currentVenue.refundableStatus}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Book Method"
              type="text"
              fullWidth
              name="bookMethod"
              value={currentVenue.bookMethod}
              onChange={handleChange}
            />
          </Grid>

          <Grid item xs={12}>
            <TextField
              margin="dense"
              label="Registration Link"
              type="text"
              fullWidth
              name="registrationLink"
              value={currentVenue.registrationLink}
              onChange={handleChange}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="CPR Price"
              type="text"
              fullWidth
              name="cprPrice"
              value={currentVenue.cprPrice}
              onChange={handleChange}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="BLS Price"
              type="text"
              fullWidth
              name="blsPrice"
              value={currentVenue.blsPrice}
              onChange={handleChange}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="CPR Adult Price"
              type="text"
              fullWidth
              name="cpradultPrice"
              value={currentVenue.cpradultPrice}
              onChange={handleChange}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="CPR Instructor Price"
              type="text"
              fullWidth
              name="cprinstructorPrice"
              value={currentVenue.cprinstructorPrice}
              onChange={handleChange}
            />
          </Grid>
          <Grid item xs={6}>
            <FormControl margin="dense" fullWidth>
              <InputLabel>Status</InputLabel>
              <Select
                labelId="select-label"
                value={currentVenue.venueStatus}
                onChange={handleChange}
                label="Select Option"
                name="venueStatus"
              >
                <MenuItem value="NORMAL">NORMAL</MenuItem>
                <MenuItem value="INSTRUCTORISSUE">INSTRUCTOR ISSUE</MenuItem>
                <MenuItem value="VENUEISSUE">VENUE ISSUE</MenuItem>
                <MenuItem value="CLOSED">CLOSED</MenuItem>
                <MenuItem value="INVESTIGATION">INVESTIGATION</MenuItem>
              </Select>
            </FormControl>
          </Grid>
        </Grid>

        {isEditMode && (
          <VenueScheduleCalendar venueId={currentVenue.id}
          currentVenue={currentVenue} />
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