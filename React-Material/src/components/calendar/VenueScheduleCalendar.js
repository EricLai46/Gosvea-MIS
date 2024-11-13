import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { Dialog, DialogActions, DialogContent, DialogTitle, TextField, Button, Checkbox, FormControlLabel,FormControl,Select,MenuItem,InputLabel } from '@mui/material';
import axiosInstance from '../AxiosInstance';
import { addWeeks } from 'date-fns';

const VenueScheduleCalendar = ({ venueId, currentVenue }) => {
    const [events, setEvents] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [newEvent, setNewEvent] = useState({
        courseTitle: '',
        start: '',
        end: '',
        repeatTimes: 0,
        price: 0
    });
    const [selectedDate, setSelectedDate] = useState('');

    useEffect(() => {
        if (venueId && currentVenue) {
            console.log('Current Venue:', currentVenue);
            fetchEvents();
        }
    }, [venueId, currentVenue]);

    const fetchEvents = async () => {
        try {
            const response = await axiosInstance.get(`/venue/schedule?venueId=${venueId}`);
            const availableTimes = response.data.data;
            console.log(response.data.data);
            // Convert the data to FullCalendar event format
            const eventsData = availableTimes.map(time => ({
                title: time.courseTitle,
                start: `${time.date}T${time.startTime}`,
                end: time.endTime ? `${time.date}T${time.endTime}` : undefined,
                price: time.price,
                backgroundColor: 'lightgreen',
                id: time.id,
                extendedProps: {
                    endTime: time.endTime, // 确保结束时间传递到extendedProps
                }
            }));

            setEvents(eventsData);
        } catch (error) {
            console.error('Error fetching events:', error);
        }
    };

    const handleDateClick = (arg) => {
        setSelectedDate(arg.dateStr);
        setOpenDialog(true);
    };

    const handleEventClick = async (clickInfo) => {
        if (window.confirm(`Are you sure you want to delete the event '${clickInfo.event.title}'`)) {
            clickInfo.event.remove();
            try {
                await axiosInstance.delete(`/venue/schedule`, { params: { id: clickInfo.event.id } }); // MODIFIED
            } catch (error) {
                console.error('Error deleting event:', error);
            }
        }
    };

    const saveEvent = async () => {
        const eventsToSave = [];
        let currentDate = new Date(selectedDate);

        for (let i = 0; i < (newEvent.repeatTimes>0 ? newEvent.repeatTimes : 1); i++) {
            eventsToSave.push({
                courseTitle: newEvent.courseTitle,
                date: currentDate.toISOString().split('T')[0],
                venueId: venueId,
                startTime: newEvent.start,
                endTime: newEvent.end,
                price: newEvent.price
            });
            currentDate = addWeeks(currentDate, 1);
        }

        try {
            const responses = await Promise.all(eventsToSave.map(event => 
                axiosInstance.post(`/venue/schedule`, event)
            ));
            setEvents([...events, ...responses.map(response => ({
                title: newEvent.courseTitle,
                start: `${response.data.date}T${response.data.startTime}`,
                end: response.data.endTime ? `${response.data.date}T${response.data.endTime}` : undefined,
                backgroundColor: 'lightgreen',
                id: response.data.id,
                price: response.data.price
            }))]);
            setOpenDialog(false);
            fetchEvents();
        } catch (error) {
            console.error('Error saving event:', error);
        }
    };

    const handleInputChange = (e) => {
        const { name, value, checked, type } = e.target;
        setNewEvent(prevState => ({ ...prevState, [name]: type === 'checkbox' ? checked : value }));

        // 根据 courseTitle 更新价格
        if (name === 'courseTitle' && currentVenue) {
            console.log('Selected Course Title:', value);
            let price = 0;
            switch (value) {
                case 'cpr':
                    price = currentVenue.cprPrice;
                    break;
                case 'bls':
                    price = currentVenue.blsPrice;
                    break;
                case 'cpradult':
                    price=currentVenue.cpradultPrice;
                    break;
                case 'cprinstructor':
                    price=currentVenue.cprinstructorPrice;
                    break;
                default:
                    price = 0;
                    break;
            }
            setNewEvent(prevState => ({ ...prevState, price }));
        }
         // 当 start 时间变化时，自动设置 end 时间为 start 时间 + 2 小时
    if (name === 'start') {
        const [hours, minutes] = value.split(':'); // 分割出小时和分钟
        let newEndHour = parseInt(hours) + 2; // 加两个小时
        if (newEndHour >= 24) newEndHour = newEndHour - 24; // 确保时间在 24 小时内
        const endTime = `${String(newEndHour).padStart(2, '0')}:${minutes}`; // 格式化时间
        setNewEvent(prevState => ({ ...prevState, end: endTime })); // 更新 end 时间
    }
    };

    const renderEventContent = (eventInfo) => (
        <div>
            <b>{eventInfo.timeText}</b>
            <i>{eventInfo.event.title}</i>
            <div>{eventInfo.event.extendedProps.endTime ? `End: ${eventInfo.event.extendedProps.endTime}` : ''}</div>
            <div>{eventInfo.event.extendedProps.price ? `Price: $${eventInfo.event.extendedProps.price}` : ''}</div>
        </div>
    );
    return (
        <div>
            <FullCalendar
                plugins={[dayGridPlugin, interactionPlugin]}
                initialView="dayGridMonth"
                events={events}
                dateClick={handleDateClick}
                eventClick={handleEventClick}
                eventContent={renderEventContent}
            />
            <Dialog open={openDialog} onClose={() => setOpenDialog(false)}>
                <DialogTitle>Add New Event</DialogTitle>
                <DialogContent>

                <FormControl fullWidth margin="dense">
                    <InputLabel id="course-title-label">Course Title</InputLabel>
                    <Select
                    labelId="course-title-label"
                    name="courseTitle"
                    label="Course Title"
                    value={newEvent.courseTitle}
                    onChange={handleInputChange}
                    autoFocus
                    >
                    <MenuItem value="cpr">CPR</MenuItem>
                    <MenuItem value="bls">BLS</MenuItem>
                    <MenuItem value="cpradult">CPR Adult</MenuItem>
                    <MenuItem value="cprinstructor">CPR Instructor</MenuItem>
                     </Select>
                    </FormControl>
                    <TextField
                        margin="dense"
                        name="start"
                        label="Start Time"
                        type="time"
                        fullWidth
                        value={newEvent.start}
                        onChange={handleInputChange}
                    />
                    <TextField
                        margin="dense"
                        name="end"
                        label="End Time"
                        type="time"
                        fullWidth
                        value={newEvent.end}
                        onChange={handleInputChange}
                    />
                    <TextField
                        margin='dense'
                        name="price"
                        label="Price"
                        type="number"
                        value={newEvent.price}
                        onChange={handleInputChange}
                        inputProps={{ min: 0 }} 
                      />
                    {/* <FormControlLabel
                        control={
                            <Checkbox
                                checked={newEvent.isWeekly}
                                onChange={handleInputChange}
                                name="isWeekly"
                                color="primary"
                            />
                        }
                        label="Repeat weekly for 4 weeks"
                    /> */}
                 <TextField
                        margin='dense'
                        name="repeatTimes"
                        label="Repeat times"
                        type="number"
                        value={newEvent.repeatTimes}
                        onChange={handleInputChange}
                        inputProps={{ min: 0 }} 
                      />

                </DialogContent>
                <DialogActions>
                    <Button onClick={() => setOpenDialog(false)} color="primary">
                        Cancel
                    </Button>
                    <Button onClick={saveEvent} color="primary">
                        Save
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    );
};

export default VenueScheduleCalendar;