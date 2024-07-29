import React from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button } from '@mui/material';

const CourseTable = ({ currentItems, handleClickOpen }) => (
  <TableContainer>
    <Table sx={{ minWidth: 650 }}>
      <TableHead>
        <TableRow>
          <TableCell>Course ID</TableCell>
          <TableCell>Course Address</TableCell>
          <TableCell>Course Instructor</TableCell>
          <TableCell>Course Date</TableCell>
          <TableCell>Course Start Time</TableCell>
          <TableCell>Course End Time</TableCell>
          <TableCell>Actions</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {/* {currentItems.map((course) => (
          <TableRow key={course.courseid}>
            <TableCell>{course.courseid}</TableCell>
            <TableCell>{course['course address']}</TableCell>
            <TableCell>{course['course instructor']}</TableCell>
            <TableCell>{course['course date']}</TableCell>
            <TableCell>{course['course start time']}</TableCell>
            <TableCell>{course['course end time']}</TableCell>
            <TableCell>
              <Button variant="outlined" onClick={() => handleClickOpen(course)}>Edit</Button>
            </TableCell>
          </TableRow>
        ))} */}
      </TableBody>
    </Table>
  </TableContainer>
);

export default CourseTable;