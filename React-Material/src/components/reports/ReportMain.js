import React, { useState, useEffect } from 'react';
import axiosInstance from '../AxiosInstance';
import SidebarLayout from '../SidebarLayout';
import { Container, Button } from '@mui/material';
import { useNotification } from '../NotificationContext';
import ReportSearchForm from './ReportSearchForm';

const ReportMain=()=>{
  const [date,setDate]=useState(null);


  const handleSearch = () => {
    // 调用后端 API 获取 Excel 文件
    const token = localStorage.getItem('token');
    console.log("Stored JWT in localStorage:", token);
    
    const authHeader = 'Bearer ' + token;
    axiosInstance.get('/api/icpie/course/coursesummary', {
      params: {
        date: date  // 传递需要查询的日期作为参数
      },
      responseType: 'blob', // 确保 axios 处理文件流
      headers: {
        'Authorization': authHeader  // 传递 Bearer token
      }
    })
    .then((response) => {
      // 创建一个虚拟的链接用于下载文件
      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
  
      // 设置下载文件的名称
      link.setAttribute('download', 'AD_Summary_'+date+'.xlsx');
  
      // 模拟点击链接进行下载
      document.body.appendChild(link);
      link.click();
  
      // 移除下载链接
      document.body.removeChild(link);
    })
    .catch((error) => {
      console.error('导出Excel文件时出错:', error);
    });
  };
    return(
        <SidebarLayout>
            <Container maxWidth="lg" sx={{mt:4,mb:4}}>
                <ReportSearchForm
                 date={date}
                 setDate={setDate}
                 handleSearch={handleSearch}
                />

            </Container>
        </SidebarLayout>
    )
};




export default ReportMain;