import React, { useState } from "react";
import axiosInstance from "../AxiosInstance";
import SidebarLayout from "../SidebarLayout";
import InstructorSearchForm from "./InstructorSearchForm";
import InstructorTable from "./InstructorTable";
import InstructorPagination from "./InstructorPagination";
import InstructorDialog from "./InstructorDialog";
import { useNotification } from '../NotificationContext';
import { Container, Button } from '@mui/material';

const InstructorMain = () => {
  const [timeZone, setTimeZone] = useState('');
  const [state, setState] = useState('');
  const [instructors, setInstructors] = useState([]);
  const [venues, setVenues] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [currentVenue, setCurrentVenue] = useState({});
  const [currentInstructor, setCurrentInstructor] = useState({});
  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const { showNotification } = useNotification();
  const [firstname, setFirstname] = useState('');
  const [lastname, setLastname] = useState('');
  const [email, setEmail] = useState('');
  const [phonenumber, setPhonenumber] = useState('');

  const handleSearch = () => {
    if (isLoading) return; // 避免重复请求
    setIsLoading(true);

    let params = {
      pageNum: currentPage,
      pageSize: itemsPerPage,
      state,
      phoneNumber: phonenumber,
      firstname: firstname,
      lastname: lastname,
      email: email,
      timeZone: timeZone,
    };

    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key];
      }
    });

    axiosInstance.get('/instructor', { params })
      .then(response => {
        console.log('Response received:', response); // 添加日志
        if (response.data.message === "success") {
          setInstructors(response.data.data.items);
        } else {
          console.error('Error in response data:', response.data.message); // 添加日志
          showNotification('Instructor search failed!', 'error');
        }
      })
      .catch(error => {
        console.error('Error in request:', error); // 添加日志
        showNotification('Instructor search failed!', 'error');
      })
      .finally(() => {
        setIsLoading(false);
      });
  };

  const handleAdd = () => {
    const newInstructor = {
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
      instructorId: null
    };

    setCurrentInstructor(newInstructor);
    setIsEditMode(false);
    setOpen(true);
  };

  const handleClickOpen = (instructor) => {
    setCurrentInstructor(instructor);
    setOpen(true);
    setIsEditMode(true);
  };

  const handleClose = () => {
    setOpen(false);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCurrentInstructor(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    axiosInstance.put('/instructor', currentInstructor, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        setInstructors(prev => prev.map(v => v.id === currentVenue.id ? currentVenue : v));
        handleSearch();
        setOpen(false);
        showNotification('Instructor updated successfully!', 'success');
      })
      .catch(error => {
        console.error('Error in request:', error);
        showNotification('Instructor update failed!', 'error');
      });
  };

  const handleInsert = () => {
    axiosInstance.post('/instructor', currentInstructor, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        setOpen(false);
        handleSearch();
        showNotification('Instructor added successfully!', 'success');
      })
      .catch(error => {
        console.error('Error in request:', error);
        showNotification('Instructor addition failed!', 'error');
      });
  };

  const handleDelete = () => {
    axiosInstance.delete('/instructor', { params: { instructorId: currentInstructor.id } })
      .then(response => {
        setOpen(false);
        handleSearch();
        showNotification('Instructor deleted!', 'success');
      })
      .catch(error => {
        console.error('Error in request', error);
        showNotification('Instructor deletion failed!', 'error');
      });
  };

  const handleReset = () => {
    setTimeZone('');
    setState('');
    setFirstname('');
    setLastname('');
    setEmail('');
    setPhonenumber('');
    setInstructors([]);
    setCurrentPage(1);
  };

  const handlePageChange = (event, value) => {
    setCurrentPage(value);
    handleSearch();
  };

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = instructors.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(instructors.length / itemsPerPage);

  return (
    <SidebarLayout>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <InstructorSearchForm
          timeZone={timeZone}
          setTimeZone={setTimeZone}
          state={state}
          setState={setState}
          email={email}
          setEmail={setEmail}
          firstname={firstname}
          setFirstname={setFirstname}
          lastname={lastname}
          setLastname={setLastname}
          phonenumber={phonenumber}
          setPhonenumber={setPhonenumber}
          handleSearch={handleSearch}
          handleReset={handleReset}
        />
        <InstructorTable currentItems={currentItems} handleClickOpen={handleClickOpen} />
        <InstructorPagination totalPages={totalPages} currentPage={currentPage} handlePageChange={handlePageChange} />
        <Button variant="contained" color="primary" onClick={handleAdd} sx={{ mt: 2 }}>
          Add Instructor
        </Button>
      </Container>
      <InstructorDialog
        open={open}
        handleClose={handleClose}
        isEditMode={isEditMode}
        currentInstructor={currentInstructor}
        handleChange={handleChange}
        handleSave={handleSave}
        handleInsert={handleInsert}
        handleDelete={handleDelete}
      />
    </SidebarLayout>
  );
};

export default InstructorMain;