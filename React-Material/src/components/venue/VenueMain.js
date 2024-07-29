import React, { useState, useEffect } from 'react';
import axiosInstance from '../AxiosInstance';
import SidebarLayout from '../SidebarLayout';
import VenueSearchForm from './VenueSearchForm';
import VenueTable from './VenueTable';
import VenuePagination from './VenuePagination';
import VenueDialog from './VenueDialog';
import { Container, Button } from '@mui/material';
import { useNotification } from '../NotificationContext';

const VenueMain = () => {
  const [timeZone, setTimeZone] = useState('');
  const [state, setState] = useState('');
  const [instructor, setInstructor] = useState('');
  const [venues, setVenues] = useState([]);
  const [schedules, setSchedules] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [currentVenue, setCurrentVenue] = useState({});
  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST'];
  const { showNotification } = useNotification();

  useEffect(() => {
    // Fetch venues and schedules data when the component mounts
    fetchVenues();
    //fetchSchedules();
  }, []);

  useEffect(() => {
    // Automatically check venues and instructors after fetching data
    if (venues.length > 0 && schedules.length > 0) {
      checkVenuesAndInstructors();
    }
  }, [venues, schedules]);

  const fetchVenues = async () => {
    try {
      const response = await axiosInstance.get('/venue');
      if (response.data.message === "success") {
        setVenues(response.data.data.items);
      } else {
        showNotification('Failed to fetch venues!', 'error');
      }
    } catch (error) {
      console.error('Error fetching venues:', error);
    }
  };

  // const fetchSchedules = async () => {
  //   try {
  //     const response = await axiosInstance.get('/schedule');
  //     if (response.data.message === "success") {
  //       setSchedules(response.data.data.items);
  //     } else {
  //       showNotification('Failed to fetch schedules!', 'error');
  //     }
  //   } catch (error) {
  //     console.error('Error fetching schedules:', error);
  //   }
  // };

  const handleSearch = () => {
    if (isLoading) return;
    setIsLoading(true);

    let params = {
      pageNum: currentPage,
      pageSize: itemsPerPage,
      state,
      city: '',
      instructor: instructor,
      paymentMode: '',
      timeZone: timeZone,
    };

    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key];
      }
    });

    axiosInstance.get('/venue', { params })
      .then(response => {
        if (response.data.message === "success") {
          setVenues(response.data.data.items);
          console.log(response.data.data);
          showNotification('Venue found successfully!', 'success');
        } else {
          showNotification('Venue search failed!', 'error');
        }
      })
      .catch(error => {
        console.error('Error in request:', error);
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleAdd = () => {
    const newVenue = {
      id: null,
      state: '',
      city: '',
      address: '',
      timeZone: '',
      cancellationPolicy: '',
      paymentMode: '',
      nonrefundableFee: 0,
      fobKey: '',
      deposit: '',
      membershipFee: 0,
      usageFee: 0,
      refundableStatus: '',
      bookMethod: '',
      registrationLink: '',
      instructor: null
    };

    setCurrentVenue(newVenue);
    setIsEditMode(false);
    setOpen(true);
  };

  const handleClickOpen = (venue) => {
    setCurrentVenue(venue);
    setOpen(true);
    setIsEditMode(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCurrentVenue(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    axiosInstance.put('/venue', currentVenue, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        setVenues(prev => prev.map(v => v.id === currentVenue.id ? currentVenue : v));
        setOpen(false);
        showNotification('Venue updated successfully!', 'success');
        checkVenuesAndInstructors(); // Check after save
      })
      .catch(error => {
        console.error('Error in request:', error);
        showNotification('Venue update failed!', 'error');
      });
  };

  const handleInsert = () => {
    axiosInstance.post('/venue', currentVenue, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        setOpen(false);
        showNotification('Venue added successfully!', 'success');
        checkVenuesAndInstructors(); // Check after insert
      })
      .catch(error => {
        console.error('Error in request:', error);
      });
  };

  const handleDelete = () => {
    axiosInstance.delete('/venue', { params: { venueId: currentVenue.id } })
      .then(response => {
        setOpen(false);
        showNotification('Venue deleted!', 'success');
        checkVenuesAndInstructors(); // Check after delete
      })
      .catch(error => {
        console.error('Error in request', error);
      });
  };

  const handleReset = () => {
    setTimeZone('');
    setState('');
    setInstructor('');
    setVenues([]);
    setCurrentPage(1);
    showNotification('Reset successfully!', 'success');
  };

  const handlePageChange = (event, value) => {
    setCurrentPage(value);
    handleSearch();
  };

  const checkVenuesAndInstructors = async () => {
    let updatedCourseSchedules = [];
    for (const venue of venues) {
      if (!venue.instructor) {
        showNotification(`Venue ${venue.id} has no instructor assigned!`, 'warning');
      } else {
        const venueSchedule = schedules.filter(schedule => schedule.venue_id === venue.id);
        const instructorSchedule = schedules.filter(schedule => schedule.instructor_id === venue.instructor);
  
        const matchingSchedules = venueSchedule.filter(vs =>
          instructorSchedule.some(is =>
            vs.date === is.date &&
            (
              (is.start_time >= vs.start_time && is.start_time < vs.end_time) ||
              (is.end_time > vs.start_time && is.end_time <= vs.end_time) ||
              (is.start_time <= vs.start_time && is.end_time >= vs.end_time)
            )
          )
        );
  
        if (matchingSchedules.length > 0) {
          // Add matching schedules to the course schedule update list
          matchingSchedules.forEach(vs => {
            instructorSchedule.forEach(is => {
              if (
                vs.date === is.date &&
                (
                  (is.start_time >= vs.start_time && is.start_time < vs.end_time) ||
                  (is.end_time > vs.start_time && is.end_time <= vs.end_time) ||
                  (is.start_time <= vs.start_time && is.end_time >= vs.end_time)
                )
              ) {
                const start_time = is.start_time > vs.start_time ? is.start_time : vs.start_time;
                const end_time = is.end_time < vs.end_time ? is.end_time : vs.end_time;
  
                updatedCourseSchedules.push({
                  instructor_id: venue.instructor,
                  venue_id: venue.id,
                  date: vs.date,
                  start_time: start_time,
                  end_time: end_time,
                  is_active: 1 // Assuming we want to set the new course schedule as active
                });
              }
            });
          });
        } else {
          showNotification(`Instructor ${venue.instructor} has no matching schedule with Venue ${venue.city}!`, 'warning');
        }
      }
    }
  
    if (updatedCourseSchedules.length > 0) {
      try {
        await axiosInstance.post('/course', { schedules: updatedCourseSchedules });
        showNotification('Course schedules updated successfully!', 'success');
      } catch (error) {
        console.error('Error updating course schedules:', error);
        showNotification('Failed to update course schedules!', 'error');
      }
    }
  };

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = venues.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(venues.length / itemsPerPage);

  return (
    <SidebarLayout>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <VenueSearchForm
          timeZone={timeZone}
          state={state}
          instructor={instructor}
          setTimeZone={setTimeZone}
          setState={setState}
          setInstructor={setInstructor}
          handleSearch={handleSearch}
          handleReset={handleReset}
        />
        <VenueTable currentItems={currentItems} handleClickOpen={handleClickOpen} />
        <VenuePagination totalPages={totalPages} currentPage={currentPage} handlePageChange={handlePageChange} />
        <Button variant="contained" color="primary" onClick={handleAdd} sx={{ mt: 2 }}>
          Add Venue
        </Button>
      </Container>
      <VenueDialog
        open={open}
        handleClose={handleClose}
        isEditMode={isEditMode}
        currentVenue={currentVenue}
        handleChange={handleChange}
        handleSave={handleSave}
        handleInsert={handleInsert}
        handleDelete={handleDelete}
        timeZones={timeZones}
      />
    </SidebarLayout>
  );
};

export default VenueMain;