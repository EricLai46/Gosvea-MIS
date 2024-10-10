import React from 'react';
import SignInSide from './components/SignInSide.js';
import Dashboard from './components/Dashboard.js'
import Venue from './components/Venue.js';
import Instructor from './components/Instructor.js'
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import VenueMain from './components/venue/VenueMain.js';
import InstructorMain from './components/instructor/InstructorMain.js';
import CourseMain from './components/course/CourseMain.js'
import AdvertisementMain from './components/Advertisement/AdvertisementMain.js'
import ADCalendarMain from'./components/ADcalendar/ADCalendarMain.js'
import  ReportMain  from './components/reports/ReportMain.js';
function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<SignInSide />} />
        <Route path="/dashboard" element={<Dashboard />} />
        <Route path="/venue" element={<Venue />} />
        <Route path="/instructor" element={<Instructor />} />
        <Route path="/venuemain" element={<VenueMain />} />
        <Route path="/instructormain" element={<InstructorMain />} />
        <Route path="/coursemain" element={<CourseMain />} />
        <Route path="/advertisementmain" element={<AdvertisementMain/>}/>
        <Route path="/ADCalendarMain" element={<ADCalendarMain/>}/>
        <Route path='/ReportMain' element={<ReportMain/>}/>
      </Routes>
    </Router>
  );
}

export default App;