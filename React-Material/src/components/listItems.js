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
import {jwtDecode} from 'jwt-decode';

// 从 localStorage 中获取 token
const token = localStorage.getItem('token');
let userRole = null;
if (token) {
  const decodedToken = jwtDecode(token);
  userRole = decodedToken.claims.role;
}

// 函数封装菜单项的显示逻辑
const renderMenuItem = (requiredRole, link, icon, label) => {
  // 如果用户角色是 ROLE_ICPIE 或者用户角色和菜单要求的角色匹配，则渲染菜单项
  if (userRole === 'ROLE_ICPIE' || userRole === requiredRole) {
    return (
      <ListItemButton component={Link} to={link}>
        <ListItemIcon>{icon}</ListItemIcon>
        <ListItemText primary={label} />
      </ListItemButton>
    );
  }
  return null;
};

// 主菜单项的渲染
export const mainListItems = (
  <React.Fragment>
    {/* 无需角色控制的菜单 */}
    <ListItemButton component={Link} to="/dashboard">
      <ListItemIcon>
        <DashboardIcon />
      </ListItemIcon>
      <ListItemText primary="Dashboard" />
    </ListItemButton>

    {/* 基于角色动态控制的菜单项 */}
    {renderMenuItem('ROLE_ICPIS', '/venuemain', <AddLocationIcon />, 'Venues')}
    {renderMenuItem('ROLE_ICPIS', '/instructormain', <PeopleIcon />, 'Instructors')}
 
    
    {/* AD Calendar 仅限 ROLE_ICPIE 用户 */}
    {renderMenuItem('ROLE_ICPIE', '/ADCalendarMain', <CalendarMonthIcon />, 'AD Calendar')}
    {renderMenuItem('ROLE_ICPIE', '/coursemain', <SchoolIcon />, 'Courses')}
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
