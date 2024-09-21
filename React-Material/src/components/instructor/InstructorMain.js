import React, { useState,useEffect } from "react";
import axiosInstance from "../AxiosInstance";
import SidebarLayout from "../SidebarLayout";
import InstructorSearchForm from "./InstructorSearchForm";
import InstructorTable from "./InstructorTable";
import InstructorPagination from "./InstructorPagination";
import InstructorDialog from "./InstructorDialog";
import { useNotification } from '../NotificationContext';
import { Container, Button } from '@mui/material';
import { saveAs } from 'file-saver';
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
  const [totalPages, setTotalPages] = useState(1);
  const [totalInstructors,setTotalInstructors]=useState(0);
  const [selectedFile,setSelectedFile]=useState(null);
  const [venueId,setVenueId]=useState('');
  useEffect(() => {
    fetchInstructors();
    handleSearch();
  }, [currentPage]);
  
  //获取instructor
  const fetchInstructors = async () => {
    try {
   
      const response = await axiosInstance.get('/instructor', {
        params: {
          pageNum: currentPage,
          pageSize: itemsPerPage,
          state,
          phoneNumber: '',
          lastname,
          firstname,
          
        },
      });
      if (response.data.message === 'success') {
         // 处理 venues 数据
         const instructorDTOs = response.data.data.items.map(instructor => {
          // 直接使用返回的 instructors 列表
          return {
            ...instructor,
           venueIds: instructor.venueIds || [],  // 如果没有 instructors 则设置为空数组
          };
        });
        setInstructors(response.data.data.items);
       
      } else {
        showNotification('获取Instructor信息失败！', 'error');
      }
    } catch (error) {
      console.error('获取Instructor信息时出错:', error);
    }
  };
 //搜索instructor
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
      venueId:venueId
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
          setTotalInstructors(response.data.totalElements);
          setTotalPages(Math.ceil(response.data.data.totalElement / itemsPerPage));
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
      venueId: '',
      firstname: '',
      phoneNumber: '',
      email: '',
      wagePerHour: 0,
      totalClassTimes: 0,
      deposit: 0,
      rentManikinNumbers: 0,
      finance: 0,
      rentStatus: '',
      fobKey: ''
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
//保存instructor 信息
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

      //后台更新instructor的信息
      //axiosInstance.
  };
  //添加新的instructor
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
  //删除instructor
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
  //重置搜索instructor的条件
  const handleReset = () => {
    
    setState('');
    setFirstname('');
    setLastname('');
    setEmail('');
    setPhonenumber('');
    setInstructors([]);
    setCurrentPage(1);
  };
 //翻页
  const handlePageChange = (event, value) => {
    setCurrentPage(value);
    handleSearch();
  };

  //导出文件
  const handleExport=async()=>{
    try{
      const response=await axiosInstance.get('/instructor/export',{
        responseType:'blob',
      });

      const contentDisposition=(await response).headers['content-disposition'];
      const filename=contentDisposition
                ?contentDisposition.split('filename')[1].split(';')[0].replace(/"/g,''):'instructors.xlsx';
      
      console.log('Content-Disposition:', contentDisposition);     
         //文件下载
    saveAs(new Blob([response.data]),filename);
  } catch(error){
    console.error('Error exported data',error);
  }
 };
   

 //上传instructor文件
  //上传文件到后端
  const handleFileChange=(event)=>{
    setSelectedFile(event.target.files[0]);
    event.target.value = null;
};

const handleUpload=()=>{
    if(!selectedFile){
      showNotification("Please select a file first!","error");
      return;
    }
    const formData=new FormData();
    formData.append('file',selectedFile);

    axiosInstance.post('/instructor/import',formData,{
      headers:{
        'Content-Type':'multipart/form-data'
      }
    })
    .then(response=>{
      console.log(response.data);

    })
    .catch(error=>{
      console.log(error);
    });
};

  return (
    <SidebarLayout>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <InstructorSearchForm

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
          handleExport={handleExport}
          handleFileChange={handleFileChange}
          handleUpload={handleUpload}
          selectedFile={selectedFile}
          venueId={venueId}
          setVenueId={setVenueId}
        />
        <InstructorTable currentItems={instructors} handleClickOpen={handleClickOpen} />
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