import * as React from 'react';
import ListItemButton from '@mui/material/ListItemButton';
import ListItemIcon from '@mui/material/ListItemIcon';
import ListItemText from '@mui/material/ListItemText';
import CalendarMonthIcon from '@mui/icons-material/CalendarMonth';
import DashboardIcon from '@mui/icons-material/Dashboard';

import AddLocationIcon from '@mui/icons-material/AddLocation';
import PeopleIcon from '@mui/icons-material/People';
import AssessmentIcon from '@mui/icons-material/Assessment';
import SchoolIcon from '@mui/icons-material/School';
import { Link } from 'react-router-dom';
import {jwtDecode} from 'jwt-decode';
import  {useEffect, useState } from 'react';
export default function ListItems() {
  // useState 和 useEffect 必须放在 React 组件内部
  const [userRole, setUserRole] = useState('');

  useEffect(() => {
    //console.log("useEffect has been triggered");
    const token = localStorage.getItem('token');
    if (token) {
      try {
        const decodedToken = jwtDecode(token);
        //console.log("Decoded Token:", decodedToken);
        setUserRole(decodedToken.claims.role);
        //console.log("User Role:", decodedToken.claims.role);
      } catch (error) {
       // console.error('Invalid token:', error);
      }
    } else {
      // 如果 localStorage 中没有 token，可以将用户角色设置为空或默认角色
      const role = localStorage.getItem('role');
      if (role) {
        setUserRole(role);
      }
    }
  }, []); // 确保 useEffect 只在组件挂载时执行一次

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
    return null; // 如果用户角色不匹配，则返回 null 不渲染此菜单项
  };

  return (
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
      {renderMenuItem('ROLE_ICPIE','/ReportMain',<AssessmentIcon/>,'Report')}
    </React.Fragment>
  );
}
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
