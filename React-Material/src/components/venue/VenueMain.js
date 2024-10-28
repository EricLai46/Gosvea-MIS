import React, { useState, useEffect } from 'react';
import axiosInstance from '../AxiosInstance';
import SidebarLayout from '../SidebarLayout';
import VenueSearchForm from './VenueSearchForm';
import VenueTable from './VenueTable';
import VenuePagination from './VenuePagination';
import VenueDialog from './VenueDialog';
import { Container, Button } from '@mui/material';
import { useNotification } from '../NotificationContext';
import {saveAs} from 'file-saver';
import {jwtDecode} from 'jwt-decode';

const VenueMain = () => {
  const [timeZone, setTimeZone] = useState('');
  const [state, setState] = useState('');
  const [instructor, setInstructor] = useState('');
  const [icpisManager,setIcpisManager]=useState('');
  const [venues, setVenues] = useState([]);
  const [totalVenues,setTotalVenues]=useState(0);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10); // 每页显示1个item
  const [currentVenue, setCurrentVenue] = useState({});
  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST'];
  const { showNotification } = useNotification();
  const [totalPages, setTotalPages] = useState(1); // 添加 totalPages 状态
  const [selectedFile,setSelectedFile]=useState(null);
  const [venueId,setVenueId]=useState('');
  const [instructors, setInstructors] = useState([]); // 新增 instructors 状态
  const [city,setCity]=useState([]);
  const [venueStatus,setVenueStatus]=useState('');
  const token = localStorage.getItem("token"); 
      let userRole = null;
      let icpisname=null;
      if (token) {
      const decodedToken = jwtDecode(token);
      userRole = decodedToken.claims.role;
      icpisname=decodedToken.claims.firstname;
    }
  useEffect(() => {
    fetchInstructors();
    fetchVenues();
    handleSearch();
  }, [currentPage]);
  const fetchInstructors = async () => {
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
    const response = await axiosInstance.get('/instructor/instructorname', {
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
//获取venues信息
  const fetchVenues = async () => {
    try {
      const token = localStorage.getItem('token');
      //("Stored JWT in localStorage:", token);
      
      const authHeader = 'Bearer ' + token;
      const response = await axiosInstance.get('/venue', {
        params: {
          pageNum: currentPage,
          pageSize: itemsPerPage,
          icpisname:icpisname,
          role:userRole,
          state,
          city: city,
          instructor: instructor,
          icpisManager: icpisManager,
          timeZone: timeZone,
          venueStatus: venueStatus,
        },
        headers: {
          'Authorization': authHeader  // 传递 Bearer token
        }
      });
      if (response.data.message === 'success') {
        setVenues(response.data.data.items);
      } else {
        showNotification('Get Venue information failed!', 'error');
      }
    } catch (error) {
      console.error('Get Venue information failed:', error);
    }
  };
//搜索venues信息
  const handleSearch = () => {
    if (isLoading) return;
    setIsLoading(true);

    let params = {
      pageNum: currentPage,
      pageSize: itemsPerPage,
      icpisname:icpisname,
      role:userRole,
      state,
      city: city,
      icpisManager: icpisManager,
      timeZone: timeZone,
      venueId:venueId,
      venueStatus:venueStatus
    };

    Object.keys(params).forEach((key) => {
      if (params[key] === '' || params[key] === null || params[key] === undefined) {
        delete params[key];
      }
    });
    const token = localStorage.getItem('token');
    //("Stored JWT in localStorage:", token);
    
    const authHeader = 'Bearer ' + token;
    axiosInstance
    .get('/venue', {
      params: params,  // 将 params 正确放入第二个参数中的 params 字段
      headers: {
        'Authorization': authHeader  // 传递 Bearer token
      }
    })
    .then((response) => {
      if (response.data.message === 'success') {
        console.log('Full Response:', response.data);
  
        // 处理 venues 数据
        const venues = response.data.data.items.map(venue => {
          // 直接使用返回的 instructors 列表
          return {
            ...venue,
            instructors: venue.instructors || [],  // 如果没有 instructors 则设置为空数组
          };
        });
  
        // 更新状态
        setVenues(venues);
        showNotification('Venues information searched successfully!', 'success');
        setTotalVenues(response.data.totalElements);
        setTotalPages(Math.ceil(response.data.data.totalElement / itemsPerPage));
      } else {
        showNotification('Venues information searched failed!', 'error');
      }
    })
    .catch((error) => {
      console.error('Error:', error);
    })
    .finally(() => {
      setIsLoading(false);
      //console.log(`totalpages,${totalPages}`);
    });
  }
  const handleAdd = () => {
    const newVenue = {
      id: '',
      state: '',
      city: '',
      address: '',
      timeZone: '',
      cancellationPolicy: '',
      paymentMode: '',
      nonrefundableFee: 0,
      fobKey: '',
      deposit: '',
      membershipFee: 0,
      usageFee: 0,
      refundableStatus: '',
      bookMethod: '',
      registrationLink: '',
      icpisManager: userRole==="ROLE_ICPIE"?'':icpisname,
      instructor: [],
      venueStatus: null,
    };

    setCurrentVenue(newVenue);
    setIsEditMode(false);
    setOpen(true);
  };

  const handleClickOpen = (venue) => {
    setCurrentVenue(venue);
    setOpen(true);
    setIsEditMode(true);
  };
//关闭
  const handleClose = () => {
    setOpen(false);
  };
//变化
const handleChange = (event) => {
  const { name, value } = event.target;

  // 如果是 instructors 列表，更新 currentVenue.instructors
  if (name === 'instructors') {
    const selectedInstructors = value.map(id => instructors.find(i => i.id === id));
    setCurrentVenue({
      ...currentVenue,
      instructors:  selectedInstructors,  // 更新为用户选择的 instructor ID 列表
    });
  } else {
    setCurrentVenue({
      ...currentVenue,
      [name]: value,  // 更新其他字段
    });
  }
};
//保存
const handleSave = () => {
// 从 currentVenue 中提取 instructor 对象，并生成 instructorIds 列表
const instructorIds = Array.isArray(currentVenue.instructors) 
? currentVenue.instructors.map(instructor => instructor.id) 
: []; // 提取 ID
const venueStatus=currentVenue.venueStatus;
const icpisManager=currentVenue.icpisManager;
const address=currentVenue.address;
const timezone=currentVenue.timeZone;
const state=currentVenue.state;
const city=currentVenue.city;
const cancellationpolicy=currentVenue.cancellationPolicy;
const paymentmode=currentVenue.paymentMode;
const nonrefundablefee=currentVenue.nonrefundableFee;
const fobkey=currentVenue.fobKey;
const deposit=currentVenue.deposit;
const membershipfee=currentVenue.membershipFee;
const usageFee=currentVenue.usageFee;
const refundablestatus=currentVenue.refundableStatus;
const bookmethod=currentVenue.bookMethod;
const registrationlink=currentVenue.registrationLink;
const cprPrice=currentVenue.cprPrice;
const blsPrice=currentVenue.blsPrice;
// 构建符合 VenueDTO 格式的对象
const venueDTO = {
  ...currentVenue,
  instructorIds: instructorIds,  // 替换 instructor 对象为 ID 列表
  venueStatus:venueStatus,
  icpisManager:icpisManager,
  address:address,
  timeZone:timezone,
  state:state,
  city:city,
  cancellationPolicy:cancellationpolicy,
  paymentMode:paymentmode,
  nonrefundableFee:nonrefundablefee,
  fobKey:fobkey,
  deposit:deposit,
  membershipFee:membershipfee,
  usageFee:usageFee,
  refundableStatus:refundablestatus,
  bookMethod:bookmethod,
  registrationLink:registrationlink,
  cprPrice:cprPrice,
  blsPrice:blsPrice
};
//console.log(JSON.stringify(venueDTO, null, 2));
//console.log('Venue DTO:', venueDTO);  // 调试时查看传递的 DTO 数据
//console.log("currentVenue.instructors:", currentVenue.icpisManager);  // 调试 instructors 原始数据

// 发送请求
axiosInstance.put('/venue', venueDTO, {
  headers: {
    'Content-Type': 'application/json',
  },
})
.then(response => {
  setVenues((prev) => prev.map((v) => (v.id === currentVenue.id ? currentVenue : v)));
  setOpen(false);
  showNotification('Venues information updated successfully!', 'success');
})
.catch(error => {
  // 处理错误
});
};
const handleInsert = () => {
  // 从 currentVenue 获取讲师的 id 列表，如果讲师为空则返回空数组
  const instructorIds = Array.isArray(currentVenue.instructors) 
    ? currentVenue.instructors.map(instructor => instructor.id) 
    : []; 

  // 创建要发送的请求数据
  const data = {
    id: currentVenue.id,
    venueStatus: currentVenue.venueStatus,
    icpisManager: currentVenue.icpisManager,
    address: currentVenue.address,
    cancellationPolicy: currentVenue.cancellationPolicy,
    paymentMode: currentVenue.paymentMode,
    nonrefundableFee: currentVenue.nonrefundableFee,
    fobKey: currentVenue.fobKey,
    deposit: currentVenue.deposit,
    membershipFee: currentVenue.membershipFee,
    usageFee: currentVenue.usageFee,
    refundableStatus: currentVenue.refundableStatus,
    bookMethod: currentVenue.bookMethod,
    registrationLink: currentVenue.registrationLink,
    city: currentVenue.city,
    timeZone: currentVenue.timeZone,
    state: currentVenue.state,
    instructorIds: instructorIds // 添加讲师 ID 列表
  };

  //console.log("新场地信息是：", data);

  // 发送请求
  axiosInstance
    .post('/venue', data, {
      headers: {
        'Content-Type': 'application/json',
      },
    })
    .then((response) => {
      setOpen(false);
      showNotification('Add a new venue successfully', 'success');
    })
    .catch((error) => {
      console.error('Bad Request:', error);
    });
};
//删除场地
  const handleDelete = () => {
    axiosInstance
      .delete('/venue', { params: { venueId: currentVenue.id } })
      .then((response) => {
        setOpen(false);
        showNotification('Venue deleted successfully!', 'success');
      })
      .catch((error) => {
        console.error('bad request', error);
      });
  };
//重置搜索条件
  const handleReset = () => {
    setTimeZone('');
    setState('');
    setInstructor('');
    setVenues([]);
    setCurrentPage(1);
    setIcpisManager('');
    setVenueId('');
    showNotification('Reset successfully!', 'success');
  };
//翻页
  const handlePageChange = (event, value) => {

    setCurrentPage(value);
    //console.log(`Page change triggered: ${value}`);
  };

  //导出文件
   const handleExport=async()=>{
    try{
      const response=await axiosInstance.get('/venue/export',{
        responseType:'blob',
      });

      const contentDisposition=(await response).headers['content-disposition'];
      const filename=contentDisposition
                ?contentDisposition.split('filename')[1].split(';')[0].replace(/"/g,''):'venues.xlsx';
      
      //console.log('Content-Disposition:', contentDisposition);          

    //文件下载
    saveAs(new Blob([response.data]),filename);
    } catch(error){
      console.error('Error exported data',error);
    }
   };
  

  //const totalPages = Math.ceil(totalVenues/ itemsPerPage);
  
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

        axiosInstance.post('/venue/import',formData,{
          headers:{
            'Content-Type':'multipart/form-data'
          }
        })
        .then(response=>{
          //console.log(response.data);

        })
        .catch(error=>{
          //console.log(error);
        });
   };

  return (
    <SidebarLayout>
      <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
        <VenueSearchForm
          timeZone={timeZone}
          state={state}
          instructor={instructor}
          icpisManager={icpisManager}
          setTimeZone={setTimeZone}
          setState={setState}
          setInstructor={setInstructor}
          handleSearch={handleSearch}
          handleReset={handleReset}
          handleExport={handleExport}
          handleFileChange={handleFileChange}
          handleUpload={handleUpload}
          selectedFile={selectedFile}
          setIcpisManager={setIcpisManager}
          venueId={venueId}
          userRole={userRole}
          setVenueId={setVenueId}
          venueStatus={venueStatus}
          setVenueStatus={setVenueStatus}
          city={city}
          setCity={setCity}
        />
        <VenueTable currentItems={venues} handleClickOpen={handleClickOpen} />
        <VenuePagination totalPages={totalPages} currentPage={currentPage} handlePageChange={handlePageChange}/>
        <Button variant="contained" color="primary" onClick={handleAdd} sx={{ mt: 2 }}>
          Add a new venue
        </Button>
      </Container>
      <VenueDialog
        userRole={userRole}
        icpisname={icpisname}
        open={open}
        handleClose={handleClose}
        isEditMode={isEditMode}
        currentVenue={currentVenue}
        handleChange={handleChange}
        handleSave={handleSave}
        handleInsert={handleInsert}
        handleDelete={handleDelete}
        timeZones={timeZones}
        instructors={instructors}
        icpisManager={icpisManager}
      />
    </SidebarLayout>
  );
};

export default VenueMain;