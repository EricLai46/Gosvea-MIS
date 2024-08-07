import React, { useState, useEffect } from 'react';
import FullCalendar from '@fullcalendar/react';
import dayGridPlugin from '@fullcalendar/daygrid';
import interactionPlugin from '@fullcalendar/interaction';
import { Dialog, DialogActions, DialogContent, DialogTitle, TextField, Button, Checkbox, FormControlLabel } from '@mui/material';
import axiosInstance from '../AxiosInstance';
import { addWeeks } from 'date-fns';

const VenueScheduleCalendar = ({ venueId }) => {
    const [events, setEvents] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [newEvent, setNewEvent] = useState({
        courseTitle: '',
        start: '',
        end: '',
        isWeekly: false,
    });
    const [selectedDate, setSelectedDate] = useState('');

    useEffect(() => {
        fetchEvents();
    }, [venueId]);

    const fetchEvents = async () => {
        try {
            const response = await axiosInstance.get(`/venue/schedule?venueId=${venueId}`);
            const availableTimes = response.data.data;
            //console.log(response.data.data);
            // Convert the data to FullCalendar event format
            const eventsData = availableTimes.map(time => ({
                title: time.courseTitle,
                start: `${time.date}T${time.startTime}`,
                end: time.endTime ? `${time.date}T${time.endTime}` : undefined,
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

        for (let i = 0; i < (newEvent.isWeekly ? 5 : 1); i++) {
            eventsToSave.push({
                courseTitle: newEvent.courseTitle,
                date: currentDate.toISOString().split('T')[0],
                venueId: venueId,
                startTime: newEvent.start,
                endTime: newEvent.end,
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
                id: response.data.id
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
    };
    const renderEventContent = (eventInfo) => (
        <div>
            <b>{eventInfo.timeText}</b>
            <i>{eventInfo.event.title}</i>
            <div>{eventInfo.event.extendedProps.endTime ? `End: ${eventInfo.event.extendedProps.endTime}` : ''}</div>
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
                    <TextField
                        autoFocus
                        margin="dense"
                        name="courseTitle"
                        label="Course Title"
                        type="text"
                        fullWidth
                        value={newEvent.courseTitle}
                        onChange={handleInputChange}
                    />
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
                    <FormControlLabel
                        control={
                            <Checkbox
                                checked={newEvent.isWeekly}
                                onChange={handleInputChange}
                                name="isWeekly"
                                color="primary"
                            />
                        }
                        label="Repeat weekly for 4 weeks"
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