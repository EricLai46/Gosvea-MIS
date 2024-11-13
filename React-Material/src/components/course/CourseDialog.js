import React, { useState, useEffect } from 'react';
import { Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, Grid, Checkbox, FormControlLabel, Button } from '@mui/material';
import axiosInstance from '../AxiosInstance';

const CourseDialog = ({ open, handleClose, isEditMode, currentCourse, handleChange, handleSave, handleInsert, handleDelete,isChecked,setIsChecked,isEnrollwareChecked,setIsEnrollwareChecked }) => {

  const [instructors, setInstructors] = useState([]);
  // const [isChecked, setIsChecked] = useState(currentCourse?.active || false);
  // const [isEnrollwareChecked, setIsEnrollwareChecked] = useState(!!currentCourse.isEnrollwareAdded);
  const [comments, setComments] = useState(currentCourse.comments || ''); 
  useEffect(() => {
    // 当 open 或 currentCourse 发生变化时更新 isChecked
    if (open && currentCourse) {
      setIsChecked(currentCourse.active ); 
      setIsEnrollwareChecked(currentCourse.enrollwareAdded); 
      setComments(currentCourse.comments || ''); // 1 表示 true
    }
    
  }, [open, currentCourse]);

  // 当用户点击 Checkbox 时，更新状态
  const handleCheckboxChange = (event) => {
    const { name, checked } = event.target;
    if (name === 'active') {
      setIsChecked(checked);
      handleChange({
        target: {
          name: 'active',
          value: checked ? 1 : 0  
        }
      });
    } else if (name === 'enrollwareAdded') {
      setIsEnrollwareChecked(checked);
      handleChange({
        target: {
          name: 'enrollwareAdded',
          value: checked ? 1 : 0  
        }
      });
    }
  };
  const handleCommentsChange = (event) => {
    const { value } = event.target;
    setComments(value);
    handleChange({
      target: {
        name: 'comments',
        value
      }
    });
  };
  useEffect(() => {
    if (open && currentCourse) {
      // 将 currentCourse 中的 0 和 1 转换为布尔值
      const processedCourse = {
        ...currentCourse,
        active: currentCourse.active === 1, // 1 转换为 true，0 转换为 false
        enrollwareAdded: currentCourse.enrollwareAdded === 1, // 同理
      };
  
      // 请求 instructor 数据
      axiosInstance
        .get('/instructor/instructorid', {
          params: { instructor: processedCourse.instructorId },
        })
        .then((response) => {
          setInstructors(response.data);
        })
        .catch((error) => {
          console.error('Error fetching instructors:', error);
        });
    }
  }, [open, currentCourse]);

  return (
    <Dialog open={open} onClose={handleClose} maxWidth="md" fullWidth>
      <DialogTitle>{isEditMode ? "Edit Venue" : "Add a Venue"}</DialogTitle>
      <DialogContent>
        <DialogContentText>
          {isEditMode ? "Edit the information of the venue." : "Add the information of the venue"}
        </DialogContentText>
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="AD ID"
              type="text"
              fullWidth
              name="id"
              value={currentCourse.id}
              onChange={handleChange}
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>
          <Grid item xs={12}>
            <TextField
              margin="dense"
              label="Address"
              type="text"
              fullWidth
              name="address"
              value={currentCourse.address}
              onChange={handleChange}
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="courseTitle"
              type="text"
              fullWidth
              name="courseTitle"
              value={currentCourse.courseTitle}
              onChange={handleChange}
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="instructor"
              type="text"
              fullWidth
              name="instructorId"
              value={`${instructors.firstname || ''} ${instructors.lastname || ''}`}
              onChange={handleChange}
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="startTime"
              type="text"
              fullWidth
              name="startTime"
              value={currentCourse.startTime}
              onChange={handleChange}
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="endTime"
              type="text"
              fullWidth
              name="endTime"
              value={currentCourse.endTime}
              onChange={handleChange}
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="date"
              type="text"
              fullWidth
              name="date"
              value={currentCourse.date}
              onChange={handleChange}
              InputProps={{
                readOnly: true,
              }}
            />
          </Grid>
          <Grid item xs={6}>
            <TextField
              margin="dense"
              label="Price"
              type="text"
              fullWidth
              name="price"
              value={currentCourse.price}
              onChange={handleChange}
              
            />
          </Grid>
          <Grid item xs={6}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={currentCourse?.active??false}
                  onChange={handleCheckboxChange}
                  name="active"
                  color="primary"
                />
              }
              label="AD Published?" 
            />
          </Grid>
          <Grid item xs={6}>
            <FormControlLabel
              control={
                <Checkbox
                  checked={currentCourse?.enrollwareAdded??false} 
                  onChange={handleCheckboxChange}
                  name="enrollwareAdded" 
                  color="primary"
                />
              }
              label="Added on Enrollware?" 
            />
          </Grid>
             <Grid item xs={12}>
            <TextField
              label="Comments"
              name="comments"
              value={currentCourse.comments}
              onChange={handleCommentsChange}
              multiline
              rows={4} 
              fullWidth
              variant="outlined" // 样式
            />
          </Grid>
        </Grid>
      </DialogContent>
      <DialogActions>
        <Button onClick={handleClose} color="primary">
          Cancel
        </Button>
        <Button onClick={handleSave}>
          {"Save Changes"}
        </Button>
        {isEditMode && <Button onClick={handleDelete} color="primary">DELETE</Button>}
      </DialogActions>
    </Dialog>
  );
};

export default CourseDialog;