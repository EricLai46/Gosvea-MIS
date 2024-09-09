import React from 'react';
import { Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Button, Tab } from '@mui/material';

const CourseTable = ({ currentItems, handleClickOpen }) => (
  <TableContainer>
    <Table sx={{ minWidth: 650 }}>
      <TableHead>
        <TableRow>
          <TableCell>AD ID</TableCell>
          <TableCell>ICPIS Manager</TableCell>
          <TableCell>AD Address</TableCell>
          <TableCell>Time Zone</TableCell>
          <TableCell>Course Title</TableCell>
          <TableCell>Course Instructor</TableCell>
          <TableCell>Course Date</TableCell>
          <TableCell>Course Start Time</TableCell>
          <TableCell>Course End Time</TableCell>
          <TableCell>Course Price</TableCell>
          <TableCell>Course registrationLink</TableCell>
          <TableCell>AD Published</TableCell>
          <TableCell>Course Processed</TableCell>
          <TableCell>Added on Enrollware</TableCell>
          <TableCell>Actions</TableCell>
        </TableRow>
      </TableHead>
      <TableBody>
        {Array.isArray(currentItems) && currentItems.length > 0 ? (
          currentItems.map((course) => (
            <TableRow key={course.id}>
              <TableCell>{course.id}</TableCell>
              <TableCell>{course.icpisManager}</TableCell>
              <TableCell>{course.address}</TableCell>
              <TableCell>{course.timeZone}</TableCell>
              <TableCell>{course.courseTitle}</TableCell>
              <TableCell>{course.instructorId}</TableCell>
              <TableCell>{course.date}</TableCell>
              <TableCell>{course.startTime}</TableCell>
              <TableCell>{course.endTime}</TableCell>
              <TableCell>{course.price}</TableCell>
              <TableCell>{course.registrationLink}</TableCell>
              <TableCell>{String(course.active)}</TableCell>
              <TableCell>{String(course.processed)}</TableCell>
              <TableCell>{String(course.enrollwareAdded)}</TableCell>
              <TableCell>
                <Button variant="outlined" onClick={() => handleClickOpen(course)}>Edit</Button>
              </TableCell>
            </TableRow>
          ))
        ) : (
          <TableRow>
            <TableCell colSpan={9} align="center">No data available</TableCell>
          </TableRow>
        )}
      </TableBody>
    </Table>
  </TableContainer>
);


export default CourseTable;