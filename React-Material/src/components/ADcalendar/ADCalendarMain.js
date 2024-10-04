import React, { useState, useEffect } from 'react';
import axiosInstance from '../AxiosInstance';
import SidebarLayout from '../SidebarLayout';
import { Container, Button } from '@mui/material';
import { useNotification } from '../NotificationContext';
import ADSearchForm from './ADSearchForm';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import dayjs from 'dayjs';
const ADCalendarMain=()=>{
    const { showNotification } = useNotification();
    const[currentvenue,setCurrentvenue]=useState('');
    const[Courses,setCourses]=useState([]);
    const[venues,setVenues]=useState([]);
    useEffect(() => {
        axiosInstance.get('/api/icpie/course/coursevenueIdaddress')
          .then(response => {
            if (response.data) {
                
              setVenues(response.data); // 将返回的数据存储到 venues 状态中
            } else {
              showNotification('Failed to load venues!', 'error');
            }
          })
          .catch(error => {
            console.error('Error fetching venues:', error);
            showNotification('Error fetching venues!', 'error');
          });
      }, []); 

    const handleReset = () => {
        setCurrentvenue(''); // 重置选中的场地
        setCourses([]); // 重置课程数据
        showNotification('Reset successfully!', 'success');
      };

    
      const handleSearch = () => {
        // 检查是否有选择的场地
        if (!currentvenue.id) {
          showNotification('Please select a venue before searching!', 'warning');
          return; // 终止搜索，直到选择了场地
        }
      
        axiosInstance.get('/api/icpie/course/coursecalendar', { params: { venueId: currentvenue.id } })
        .then(response => {
          if (response.data) {
            console.log(response.data);
            setCourses(response.data);
             // 保存获取到的课程数据
          } else {
            showNotification('No courses found for this venue!', 'warning');
          }
        })
        .catch(error => {
          console.error('Error in course search:', error);
          showNotification('Error in course search!', 'error');
        });
    };
  
    // 将课程数据映射到 FullCalendar 事件格式
    const calendarEvents = Courses.map(course => ({
        title: `${course.courseTitle}`, // 显示课程开始时间
        start: `${course.date}T${course.startTime}`, // 合并日期和开始时间
        end: `${course.date}T${course.endTime}`, 
        price:`${course.price}`,
        extendedProps: { 
            price: `${course.price}`,
            backgroundColor: course.active ? 'green' : 'red' 
          }
      }));

      const renderEventContent = (eventInfo) => {
        return (
            <div 
            style={{ 
              backgroundColor: eventInfo.event.extendedProps.backgroundColor, 
              padding: '5px',
              borderRadius: '5px',
              color: 'white' 
            }}>
            <b>{eventInfo.timeText}</b>
            <br />
            <span>{eventInfo.event.title}</span>
            <br />
            <span>Price: ${eventInfo.event.extendedProps.price}</span>
          </div>
        );
      };
    return(
        <SidebarLayout>
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
          <ADSearchForm

            handleSearch={handleSearch}
            handleReset={handleReset}
            Courses={Courses}
            venues={venues}
            setCurrentvenue={setCurrentvenue}
          />
   {/* FullCalendar 日历显示 */}
   <FullCalendar
          plugins={[dayGridPlugin, interactionPlugin]}
          initialView="dayGridMonth"
          initialDate={dayjs().format('YYYY-MM-DD')} // 设置日历的初始日期为系统当前日期
          events={calendarEvents} // 使用课程数据作为事件
          headerToolbar={{
            left: 'prev,next today',
            center: 'title',
            right: 'dayGridMonth,timeGridWeek,timeGridDay',
          }}
          eventContent={renderEventContent} 
          eventClick={(info) => {
            alert('Course: ' + info.event.title);
          }}
          height="auto"
        />
          </Container>
          </SidebarLayout>
    )
};
export default ADCalendarMain;