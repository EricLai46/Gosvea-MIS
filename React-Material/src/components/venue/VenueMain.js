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
  const [totalVenues,setTotalVenues]=useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10); // 每页显示1个item
  const [currentVenue, setCurrentVenue] = useState({});
  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST'];
  const { showNotification } = useNotification();
  const [venueStatus]=useState('');
  const [totalPages, setTotalPages] = useState(1); // 添加 totalPages 状态
  useEffect(() => {
    fetchVenues();
    handleSearch();
  }, [currentPage]);

  const fetchVenues = async () => {
    try {
   
      const response = await axiosInstance.get('/venue', {
        params: {
          pageNum: currentPage,
          pageSize: itemsPerPage,
          state,
          city: '',
          instructor: instructor,
          paymentMode: '',
          timeZone: timeZone,
          venueStatus: venueStatus,
        },
      });
      if (response.data.message === 'success') {
        setVenues(response.data.data.items);
      } else {
        showNotification('Get Venue information failed!', 'error');
      }
    } catch (error) {
      console.error('Get Venue information failed:', error);
    }
  };

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

    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key];
      }
    });

    axiosInstance
      .get('/venue', { params })
      .then((response) => {
        if (response.data.message === 'success') {
          setVenues(response.data.data.items);
          showNotification('Venues information searched successfully!', 'success');
          setTotalVenues(response.data.totalElements);
          setTotalPages(Math.ceil(response.data.data.totalElement / itemsPerPage));
        } else {
          showNotification('Venues information searched failed!', 'error');
        }
      })
      .catch((error) => {
        console.error('Error:', error);
      })
      .finally(() => {
        setIsLoading(false);
        console.log(`totalpages,${totalPages}`);
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
      instructor: null,
      venueStatus: null,
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
    setCurrentVenue((prev) => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    axiosInstance
      .put('/venue', currentVenue, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      .then((response) => {
        setVenues((prev) => prev.map((v) => (v.id === currentVenue.id ? currentVenue : v)));
        setOpen(false);
        showNotification('Venues information updated successfully!', 'success');
      })
      .catch((error) => {
        console.error('Bad request:', error);
        showNotification('Venues information update failed!', 'error');
      });
  };

  const handleInsert = () => {
    axiosInstance
      .post('/venue', currentVenue, {
        headers: {
          'Content-Type': 'application/json',
        },
      })
      .then((response) => {
        setOpen(false);
        showNotification('Add a new venue successfully', 'success');
      })
      .catch((error) => {
        console.error('Bad Request:', error);
      });
  };

  const handleDelete = () => {
    axiosInstance
      .delete('/venue', { params: { venueId: currentVenue.id } })
      .then((response) => {
        setOpen(false);
        showNotification('Venue deleted successfully!', 'success');
      })
      .catch((error) => {
        console.error('bad request', error);
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
    console.log(`Page change triggered: ${value}`);
  };

  //const totalPages = Math.ceil(totalVenues/ itemsPerPage);
  

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
        <VenueTable currentItems={venues} handleClickOpen={handleClickOpen} />
        <VenuePagination totalPages={totalPages} currentPage={currentPage} handlePageChange={handlePageChange} />
        <Button variant="contained" color="primary" onClick={handleAdd} sx={{ mt: 2 }}>
          Add a new venue
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