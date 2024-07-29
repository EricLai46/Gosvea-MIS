import React,{useState} from "react";
import axiosInstance from "./AxiosInstance";
import SidebarLayout from "./SidebarLayout";
import {
    Container, 
    Box, 
    FormControl, 
    InputLabel, 
    Select, 
    MenuItem, 
    Button, 
    Table, 
    TableBody, 
    TableCell, 
    TableContainer, 
    TableHead, 
    TableRow, 
    Pagination,
    Dialog,
    DialogActions,
    DialogContent,
    DialogContentText,
    DialogTitle,
    TextField,
    Snackbar,
    Alert
  } from '@mui/material';

  const Instructor=()=>{
    const [timeZone, setTimeZone] = useState('');
    const [state, setState] = useState('');
    const [instructors, setInstructors] = useState([]);
    const [venues, setVenues] = useState([]);
    const [currentPage, setCurrentPage] = useState(1);
    const [itemsPerPage] = useState(10);
    const [currentVenue, setCurrentVenue] = useState({});
    const [currentInstructor,setCurrentInstructor]=useState([]);
    const [open, setOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST']; 
    const [snackbarOpen, setSnackbarOpen] = useState(false);
    const [snackbarMessage, setSnackbarMessage] = useState('');
    const [snackbarSeverity, setSnackbarSeverity] = useState('success');
    const [firstname,setFirstname]=useState('');
    const [lastname,setLastname]=useState('');
    const [email,setEmail]=useState('');
    const [phonenumber,setPhonenumber]=useState('');
    
    const handleSearch = () => {

        if (isLoading) return; // 避免重复请求
        setIsLoading(true);
    
      // 初始化查询参数
      let params = {
        pageNum: currentPage,
        pageSize: itemsPerPage,
        state,
        phoneNumber:phonenumber,
        firstname: firstname,
        lastname:lastname,
        email:email,
        timeZone: timeZone,
      };
    
      // 移除空的参数
      Object.keys(params).forEach(key => {
        if (params[key] === '' || params[key] === null || params[key] === undefined) {
          delete params[key];
        }
      });

        // 发送请求
        axiosInstance.get('/instructor', { params })
        .then(response => {
          console.log('Response received:', response); // 添加日志
          if (response.data.message==="success") {
            //setVenues(response.data.data.items);
            setInstructors(response.data.data.items);
          } else {
            console.error('Error in response data:', response.data.message); // 添加日志
          }
        })
        .catch(error => {
          console.error('Error in request:', error); // 添加日志
        })
        .finally(() => {
          setIsLoading(false);
        });
    };
    //添加新的场地
const handleAdd=()=>{
    const newVenue = {
      id: null,
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
      instructorId: null
    };
  
    // 打开一个窗口，设置currentVenue为新场地
    setCurrentVenue(newVenue);
    setIsEditMode(false);
    setOpen(true); // 打开模态窗口
  };
  const handleClickOpen = (venue) => {
    setCurrentVenue(venue);
    setOpen(true);
    setIsEditMode(true); 
  };
  
  const handleClose = () => {
    setOpen(false);
  };
  
  const handleChange = (e) => {
    const { name, value } = e.target;
    setCurrentVenue(prev => ({ ...prev, [name]: value }));
  };
  
  //更新场地信息
  const handleSave = () => {
    // 更新数据库逻辑
    axiosInstance.put('/venue', currentVenue, {
      headers: {
        'Content-Type': 'application/json'
      }
    })
    .then(response => {
      setVenues(prev => prev.map(v => v.id === currentVenue.id ? currentVenue : v));
      setOpen(false);
      setSnackbarMessage('Venue updated successfully!');
      setSnackbarSeverity('success');
      setSnackbarOpen(true);
    })
    .catch(error => {
      console.error('Error in request:', error);
      setSnackbarMessage('Venue updated failed!');
      setSnackbarSeverity('error');
      setSnackbarOpen(true);
    });
      
  };
  //插入新数据
  const handleInsert = () => {
    axiosInstance.post('/venue',currentVenue,{
      headers:{
        'Content-Type': 'application/json'
      }
    })
    .then(response=>{
      setOpen(false);
      setSnackbarMessage('Venue Added successfully!');
      setSnackbarSeverity('success');
      setSnackbarOpen(true);
    })
    .catch(error=>{
      console.error('Error in reqeest:', error);
    });
  };
  
  const handleDelete=()=>{
    axiosInstance.delete('/venue', { params: { venueId: currentVenue.id } })
      .then(response=>{
        setOpen(false);
        setSnackbarMessage('Venue deleted!');
        setSnackbarSeverity('success');
        setSnackbarOpen(true);
      })
      .catch(error=>{
        console.error('Error in request',error);
      });
  };
  
  //重置搜索数据
    const handleReset = () => {
      setTimeZone('');
      setState('');
      setFirstname('');
      setLastname('');
      setEmail('');
      setPhonenumber('');
      setInstructors([]);
      setCurrentPage(1);
    };
    const handlePageChange = (event, value) => {
      setCurrentPage(value);
      handleSearch();
    };
    //消息通知
    const handleSnackbarClose = (event, reason) => {
      if (reason === 'clickaway') {
        return;
      }
      setSnackbarOpen(false);
    };
    // 分页逻辑
    const indexOfLastItem = currentPage * itemsPerPage;
    const indexOfFirstItem = indexOfLastItem - itemsPerPage;
    const currentItems = instructors.slice(indexOfFirstItem, indexOfLastItem);
    const totalPages = Math.ceil(instructors.length / itemsPerPage);
  





    return (
        <SidebarLayout>
        <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
          <Box display="flex" alignItems="center" mb={3}>
            <FormControl variant="outlined" sx={{ minWidth: 100, mr: 2 }}>
              <InputLabel>Time Zone</InputLabel>
              <Select
                value={timeZone}
                onChange={(e) => setTimeZone(e.target.value)}
                label="Time Zone"
              >
                <MenuItem value=""><em>Please Select</em></MenuItem>
                <MenuItem value="PST">PST</MenuItem>
                <MenuItem value="CST">CST</MenuItem>
                <MenuItem value="MST">MST</MenuItem>
                <MenuItem value="EST">EST</MenuItem>
              </Select>
            </FormControl>
            <FormControl variant="outlined" sx={{ minWidth: 100, mr: 2 }}>
              <InputLabel>State</InputLabel>
              <Select
                value={state}
                onChange={(e) => setState(e.target.value)}
                label="State"
              >
                <MenuItem value=""><em>Please Select</em></MenuItem>
                <MenuItem value="ca">CA</MenuItem>
                <MenuItem value="wa">WA</MenuItem>
                <MenuItem value="az">AZ</MenuItem>
              </Select>
            </FormControl>
            <TextField
            variant="outlined"
            label="Email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            sx={{ minWidth: 50, mr: 2 }}
          />
           <TextField
            variant="outlined"
            label="LastName"
            value={lastname}
            onChange={(e) => setLastname(e.target.value)}
            sx={{ minWidth: 50, mr: 2 }}
          />
            <TextField
            variant="outlined"
            label="FirstName"
            value={firstname}
            onChange={(e) =>setFirstname(e.target.value)}
            sx={{ minWidth: 50, mr: 2 }}
          />
            <TextField
            variant="outlined"
            label="PhoneNumber"
            value={phonenumber}
            onChange={(e) => setPhonenumber(e.target.value)}
            sx={{ minWidth: 50, mr: 2 }}
          />

            <Button variant="contained" color="primary" onClick={handleSearch} sx={{ mr: 2 }}>
              Search
            </Button>
            <Button variant="outlined" onClick={handleReset}>
              Reset
            </Button>
            <Box sx={{ flexGrow: 1 }} />
            <Button variant="contained" color="primary" onClick={handleAdd} sx={{ mr: 8 }}>
              Add
            </Button>
          </Box>
          <TableContainer>
            <Table sx={{ minWidth: 650 }}>
              <TableHead>
                <TableRow>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Id</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '25px', whiteSpace: 'nowrap' }}>VenueId</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>FirstName</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>LastName</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>State</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '20px', whiteSpace: 'nowrap' }}>City</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>PhoneNumber</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Email</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Wage Per Hour</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Total Classes Times</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Deposit</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Rent Manikin Numbers</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Finance</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Rent Status</TableCell>
                  <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Fob Key</TableCell>
            
                </TableRow>
              </TableHead>
              <TableBody>
                {currentItems.map((instructor) => (
                  <TableRow key={instructor.id}>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.id}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.venueId}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.firstname}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.lastname}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.state}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.city}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.phoneNumber}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.email}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.wageHour}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.totalClassTimes}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.deposit}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.rentManikinNumbers}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.finance}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.rentStatus}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{instructor.fobKey}</TableCell>
                    <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>
                      <Button variant="outlined" onClick={() => handleClickOpen(instructor)}>Edit</Button>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
          <Box display="flex" justifyContent="center" mt={3}>
            <Pagination
              count={totalPages}
              page={currentPage}
              onChange={handlePageChange}
            />
          </Box>
        </Container>
        <Dialog open={open} onClose={handleClose}>
            <DialogTitle>{isEditMode?"Edit Instructor":"Add a Instructor"}</DialogTitle>
            <DialogContent>
              <DialogContentText>
                {isEditMode?"Edit the information of the instructor.":"Add the information of the instructor"}
              </DialogContentText>
              <TextField
                margin="dense"
                label="Address"
                type="text"
                fullWidth
                name="address"
                value={currentVenue.address}
                onChange={handleChange}
              />
              <FormControl margin="dense" fullWidth>
                <InputLabel>Time Zone</InputLabel>
                <Select
                  name="timeZone"
                  value={currentVenue.timeZone}
                  onChange={handleChange}
                  >
                   {timeZones.map((tz)=>(
                    <MenuItem key={tz} value={tz}>
                      {tz}
                    </MenuItem>
                   ))}   
    
                  </Select>
              </FormControl>
              <TextField
                margin="dense"
                label="State"
                type="text"
                fullWidth
                name="state"
                value={currentVenue.state}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Instructor"
                type="text"
                fullWidth
                name="instructor"
                value={currentVenue.instructor}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="City"
                type="text"
                fullWidth
                name="city"
                value={currentVenue.city}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Cancellation Policy"
                type="text"
                fullWidth
                name="cancellationPolicy"
                value={currentVenue.cancellationPolicy}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Payment Mode"
                type="text"
                fullWidth
                name="paymentMode"
                value={currentVenue.paymentMode}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Nonrefundable Fee"
                type="text"
                fullWidth
                name="nonrefundableFee"
                value={currentVenue.nonrefundableFee}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Fob Key"
                type="text"
                fullWidth
                name="fobKey"
                value={currentVenue.fobKey}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Deposit"
                type="text"
                fullWidth
                name="deposit"
                value={currentVenue.deposit}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Membership Fee"
                type="text"
                fullWidth
                name="membershipFee"
                value={currentVenue.membershipFee}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Usage Fee"
                type="text"
                fullWidth
                name="usageFee"
                value={currentVenue.usageFee}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Refundable Status"
                type="text"
                fullWidth
                name="refundableStatus"
                value={currentVenue.refundableStatus}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Book Method"
                type="text"
                fullWidth
                name="bookMethod"
                value={currentVenue.bookMethod}
                onChange={handleChange}
              />
              <TextField
                margin="dense"
                label="Registration Link"
                type="text"
                fullWidth
                name="registrationLink"
                value={currentVenue.registrationLink}
                onChange={handleChange}
              />
            </DialogContent>
            <DialogActions>
              <Button onClick={handleClose} color="primary">
                Cancel
              </Button>
              <Button onClick={isEditMode?handleSave:handleInsert}>{isEditMode ? "Save Changes" : "Add the Instrctor"}</Button>
              {isEditMode&&<Button onClick={handleDelete} color="primary"> DELETE</Button>}
    
            </DialogActions>
          </Dialog>
          <Snackbar
            open={snackbarOpen}
            autoHideDuration={3000}
            onClose={handleSnackbarClose}
          >
            <Alert onClose={handleSnackbarClose} severity={snackbarSeverity}>
              {snackbarMessage}
            </Alert>
          </Snackbar>
      </SidebarLayout>
      );
  };

  export default Instructor;