import * as React from 'react';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import DashboardIcon from '@mui/icons-material/Dashboard';

import AddLocationIcon from '@mui/icons-material/AddLocation';
import PeopleIcon from '@mui/icons-material/People';

import SchoolIcon from '@mui/icons-material/School';
import { Link } from 'react-router-dom';

export const mainListItems = (
  <React.Fragment>
    <ListItemButton component={Link} to="/dashboard">
      <ListItemIcon>
        <DashboardIcon />
      </ListItemIcon>
      <ListItemText primary="Dashboard" />
    </ListItemButton>
    {/* <ListItemButton>
      <ListItemIcon>
        <AdminPanelSettingsIcon/>
      </ListItemIcon>
      <ListItemText primary="ICPIEs"/>
    </ListItemButton> */}
    <ListItemButton component={Link} to="/venuemain">
      <ListItemIcon>
        <AddLocationIcon />
      </ListItemIcon>
      <ListItemText primary="Venues" />
    </ListItemButton>
    <ListItemButton  component={Link} to="/instructormain">
      <ListItemIcon>
        <PeopleIcon />
      </ListItemIcon>
      <ListItemText primary="Instructors" />
    </ListItemButton>
    <ListItemButton component={Link} to="/coursemain">
      <ListItemIcon>
        <SchoolIcon />
      </ListItemIcon> 
      <ListItemText primary="Courses" />
    </ListItemButton >
    <ListItemButton component={Link} to="/ADCalendarMain">
      <ListItemIcon>
        <CalendarMonthIcon/>
      </ListItemIcon> 
      <ListItemText primary="AD Calendar" />
    </ListItemButton >
    {/* <ListItemButton component={Link} to="/advertisementmain">
      <ListItemIcon>
        <AddLocationIcon />
      </ListItemIcon>
      <ListItemText primary="AD" />
    </ListItemButton> */}
    {/* <ListItemButton>
      <ListItemIcon>
        <AnalyticsIcon />
      </ListItemIcon>

      <ListItemText primary="Profile" />
    </ListItemButton> */}
  </React.Fragment>
);

// export const secondaryListItems = (
//   <React.Fragment>
//     <ListSubheader component="div" inset>
//       Saved reports
//     </ListSubheader>
//     <ListItemButton>
//       <ListItemIcon>
//         <TodayIcon />
//       </ListItemIcon>
//       <ListItemText primary="Current month" />
//     </ListItemButton>
//     <ListItemButton>
//       <ListItemIcon>
//         <EventIcon />
//       </ListItemIcon>
//       <ListItemText primary="Last month" />
//     </ListItemButton>
//     <ListItemButton>
//       <ListItemIcon>
//         <DateRangeIcon />
//       </ListItemIcon>
//       <ListItemText primary="Next month" />
//     </ListItemButton>
//   </React.Fragment>
// );
