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
import {jwtDecode} from 'jwt-decode';
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
  const [city, setCity] = useState('');
  const [phonenumber, setPhonenumber] = useState('');
  const [totalPages, setTotalPages] = useState(1);
  const [totalInstructors,setTotalInstructors]=useState(0);
  const [selectedFile,setSelectedFile]=useState(null);
  const [venueId,setVenueId]=useState('');
  const token = localStorage.getItem("token"); 
  const [email,setEmail]=useState('');

  let userRole = null;
  let icpisname=null;
  if (token) {
  const decodedToken = jwtDecode(token);
  userRole = decodedToken.claims.role;
  icpisname=decodedToken.claims.firstname;
 


}

  useEffect(() => {
    fetchInstructors();
    //fetchVenues();
    handleSearch();
  }, [currentPage]);
  //获取venues信息
  const fetchVenues = async () => {
    try {
      const token = localStorage.getItem('token');
      //("Stored JWT in localStorage:", token);
      
      const authHeader = 'Bearer ' + token;
       // 判断 userRole 是否是 'ROLE_ICPIE'
    let queryParams = {};
    if (userRole !== 'ROLE_ICPIE') {
      queryParams = {
          role: userRole,
          icpisName: icpisname
      };
  } else {
      queryParams = { role: userRole }; // ROLE_ICPIE 也应该有 role 参数
  }
    const response = await axiosInstance.get('/venue/venuelistbyinstructor', {
      params: queryParams
      ,headers: {
        'Authorization': authHeader
      }
       // 传递参数
    });
      setInstructors(response.data); // 设置 instructors 数据
    } catch (error) {
      console.error('Error fetching instructors:', error);
    }
  };
  //获取instructor
  const fetchInstructors = async () => {
    try {
   
      const response = await axiosInstance.get('/instructor', {
        params: {
          pageNum: currentPage,
          pageSize: itemsPerPage,
          city: city,
          role: userRole,
          icpisname:icpisname,
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
      role: userRole,
      icpisname:icpisname,
      city:city,
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
    const token = localStorage.getItem('token');
    //("Stored JWT in localStorage:", token);
    
    const authHeader = 'Bearer ' + token;
    axiosInstance.get('/instructor', {
      params: params,  // 将 params 正确放入第二个参数中的 params 字段
      headers: {
        'Authorization': authHeader  // 传递 Bearer token
      }
    })
      .then(response => {
        //console.log('Response received:', response); // 添加日志
        if (response.data.message === "success") {
          setInstructors(response.data.data.items);
          setTotalInstructors(response.data.totalElements);
          setTotalPages(Math.ceil(response.data.data.totalElement / itemsPerPage));
          console.log("chaxun instrucot xinxi",instructors);
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
      id: '',
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
      fobKey: '',
      icpisManager:icpisname
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
   // 从 currentVenue 中提取 instructor 对象，并生成 instructorIds 列表
const venueIds = Array.isArray(currentInstructor.venues) 
? currentInstructor.venues.map(venue => venue.id) 
: []; // 提取 ID
const firstname=currentInstructor.firstname;
const lastname=currentInstructor.lastname;
const phonenumber=currentInstructor.phoneNumber;
const state=currentInstructor.state;
const city=currentInstructor.city;
const email=currentInstructor.email;
const wageHour=currentInstructor.wageHour;
const salaryInfo=currentInstructor.salaryInfo;
const fobkey=currentInstructor.fobKey;
const finance=currentInstructor.finance;
const deposit=currentInstructor.deposit;
const rentManikinNumbers=currentInstructor.rentManikinNumbers;
const totalClassTimes=currentInstructor.totalClassTimes;
const rentStatus=currentInstructor.rentStatus;
// 构建符合 VenueDTO 格式的对象
const instructorDTO = {
  ...currentInstructor,
  venueIds: venueIds,  // 替换 instructor 对象为 ID 列表
  firstname:firstname,
  lastname:lastname,
  phonenumber:phonenumber,
  email:email,
  state:state,
  city:city,
  wageHour:wageHour,
  salaryInfo:salaryInfo,
  finance:finance,
  fobKey:fobkey,
  deposit:deposit,
  rentManikinNumbers:rentManikinNumbers,
  totalClassTimes:totalClassTimes,
  rentStatus:rentStatus,
};

    axiosInstance.put('/instructor', instructorDTO, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
      .then(response => {
        setInstructors(prev => prev.map(v => v.id === currentVenue.id ? currentVenue : v));
        handleSearch();
        setOpen(false);
        showNotification('Instructor updated successfully!', 'success');
        console.log("venue information:",instructorDTO);
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
    const authHeader = 'Bearer ' + token;
    axiosInstance.post('/instructor', currentInstructor,  // 当前的 instructor 数据放在请求体中
      {
        headers: {
          'Authorization': authHeader  // 传递 Bearer token
        }
      }
   )
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
    const confrimDelete=window.confirm("Are you sure you want to delete this instructor. This action cannot be undone.");
    if(confrimDelete){
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
    }
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
    //handleSearch();
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
      
      //console.log('Content-Disposition:', contentDisposition);     
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
      //console.log(response.data);

    })
    .catch(error=>{
      console.log(error);
    });
};

  return (
    <SidebarLayout>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <InstructorSearchForm
          city={city}
          setCity={setCity}
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
        <InstructorTable currentItems={instructors} handleClickOpen={handleClickOpen} userRole={userRole} />
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
        userRole={userRole}
      />
    </SidebarLayout>
  );
};

export default InstructorMain;