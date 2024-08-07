import React, { useState, useEffect } from 'react';
import axiosInstance from '../AxiosInstance';
import SidebarLayout from '../SidebarLayout';
import CourseSearchForm from './CourseSearchForm';
import CourseTable from './CourseTable';
import CoursePagination from './CoursePagination';
import { Container, Button } from '@mui/material';
import { useNotification } from '../NotificationContext';

const CourseMain = () => {
  const [timeZone, setTimeZone] = useState('');
  const [state, setState] = useState('');
  const [instructor, setInstructor] = useState('');
  const [courses, setCourses] = useState([]);
  const [venues, setVenues] = useState([]);
  const [schedules, setSchedules] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [currentCourse, setCurrentCourse] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST'];
  const { showNotification } = useNotification();
  

  useEffect(() => {
    // Fetch venues and schedules data when the component mounts
   // fetchVenues();
   // fetchSchedules();
  }, []);

  useEffect(() => {
    // Automatically check venues and instructors after fetching data
    if (venues.length > 0 && schedules.length > 0) {
      checkVenuesAndInstructors();
    }
  }, [venues, schedules]);

//   const fetchVenues = async () => {
//     try {
//       const response = await axiosInstance.get('/venue');
//       if (response.data.message === "success") {
//         setVenues(response.data.data.items);
//       } else {
//         showNotification('Failed to fetch venues!', 'error');
//       }
//     } catch (error) {
//       console.error('Error fetching venues:', error);
//     }
//   };

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
      instructor: '',
      date: '',
    };

    Object.keys(params).forEach(key => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key];
      }
    });

    axiosInstance.get('/course', { params })
      .then(response => {
        console.log("Full response:", response.data); 
        if (response.data.message === "success") {
          // 成功时处理数据和警告
          console.log(response);
          setCourses(response.data.data ? response.data.data.items : []);
          console.log("Warnings (success):", response.data.warnings); // 打印 warnings
    
          if (response.data.warnings && response.data.warnings.length > 0) {
            response.data.warnings.forEach(warning => {
              showNotification(warning, 'warning');
            });
          }
          showNotification('Course found successfully!', 'success');
        } else {
          // 失败时处理警告
          showNotification('Course search failed!', 'error');
    
          if (response.data.warnings && response.data.warnings.length > 0) {
            console.log("Warnings (error):", response.data.warnings); // 打印 warnings
            response.data.warnings.forEach(warning => {
              showNotification(warning, 'warning');
            });
          }
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
    const newCourse = {
      courseid: null,
      address: '',
      instructor: '',
      date: '',
      start_time: '',
      end_time: '',
    };

    setCurrentCourse(newCourse);
    setIsEditMode(false);
  };

  const handleClickOpen = (course) => {
    setCurrentCourse(course);
    setIsEditMode(true);
  };

  const handleChange = (e) => {
    const { name, value } = e.target;
    setCurrentCourse(prev => ({ ...prev, [name]: value }));
  };

  const handleSave = () => {
    axiosInstance.put('/course', currentCourse, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        setCourses(prev => prev.map(c => c.courseid === currentCourse.courseid ? currentCourse : c));
        showNotification('Course updated successfully!', 'success');
        checkVenuesAndInstructors(); // Check after save
      })
      .catch(error => {
        console.error('Error in request:', error);
        showNotification('Course update failed!', 'error');
      });
  };

  const handleInsert = () => {
    axiosInstance.post('/course', currentCourse, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        showNotification('Course added successfully!', 'success');
        checkVenuesAndInstructors(); // Check after insert
      })
      .catch(error => {
        console.error('Error in request:', error);
      });
  };

  const handleDelete = () => {
    axiosInstance.delete('/course', { params: { courseid: currentCourse.courseid } })
      .then(response => {
        showNotification('Course deleted!', 'success');
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
    setCourses([]);
    setCurrentPage(1);
    showNotification('Reset successfully!', 'success');
  };

  const handlePageChange = (event, value) => {
    setCurrentPage(value);
    handleSearch();
  };

  const checkVenuesAndInstructors = () => {
    venues.forEach(venue => {
      if (!venue.instructor) {
        showNotification(`Venue ${venue.id} has no instructor assigned!`, 'warning');
      } else {
        const venueSchedule = schedules.filter(schedule => schedule.venue_id === venue.id);
        const instructorSchedule = schedules.filter(schedule => schedule.instructor_id === venue.instructor);

        const hasMatchingSchedule = venueSchedule.some(vs =>
          instructorSchedule.some(is =>
            vs.date === is.date &&
            vs.start_time === is.start_time &&
            vs.end_time === is.end_time
          )
        );

        if (!hasMatchingSchedule) {
          showNotification(`Instructor ${venue.instructor} has no matching schedule with Venue ${venue.id}!`, 'warning');
        }
      }
    });
  };

  const indexOfLastItem = currentPage * itemsPerPage;
  const indexOfFirstItem = indexOfLastItem - itemsPerPage;
  const currentItems = courses.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(courses.length / itemsPerPage);

  return (
    <SidebarLayout>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <CourseSearchForm
          timeZone={timeZone}
          state={state}
          instructor={instructor}
          setTimeZone={setTimeZone}
          setState={setState}
          setInstructor={setInstructor}
          handleSearch={handleSearch}
          handleReset={handleReset}
        />
        <CourseTable currentItems={currentItems} handleClickOpen={handleClickOpen} />
        <CoursePagination totalPages={totalPages} currentPage={currentPage} handlePageChange={handlePageChange} />

      </Container>
    </SidebarLayout>
  );
};

export default CourseMain;