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

const Venue = () => {
  const [timeZone, setTimeZone] = useState('');
  const [state, setState] = useState('');
  const [instructor, setInstructor] = useState('');
  const [venues, setVenues] = useState([]);
  const [currentPage, setCurrentPage] = useState(1);
  const [itemsPerPage] = useState(10);
  const [currentVenue, setCurrentVenue] = useState({});
  const [open, setOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isEditMode, setIsEditMode] = useState(false);
  const timeZones = ['PST', 'EST', 'CST', 'MST', 'GMT', 'UTC', 'BST', 'CEST']; 
  const [snackbarOpen, setSnackbarOpen] = useState(false);
  const [snackbarMessage, setSnackbarMessage] = useState('');
  const [snackbarSeverity, setSnackbarSeverity] = useState('success');
  const handleSearch = () => {

    if (isLoading) return; // 避免重复请求
    setIsLoading(true);

  // 初始化查询参数
  let params = {
    pageNum: currentPage,
    pageSize: itemsPerPage,
    state,
    city:'',
    instructor: instructor,
    paymentMode: '',
    timeZone: timeZone,
  };

  // 移除空的参数
  Object.keys(params).forEach(key => {
    if (params[key] === '' || params[key] === null || params[key] === undefined) {
      delete params[key];
    }
  });

  // 发送请求
  axiosInstance.get('/venue', { params })
    .then(response => {
      console.log('Response received:', response); // 添加日志
      if (response.data.message==="success") {
        setVenues(response.data.data.items);
        setSnackbarMessage('Venue finded successfully!');
        setSnackbarSeverity('success');
        setSnackbarOpen(true);
      } else {
        console.error('Error in response data:', response.data.message); // 添加日志
        setSnackbarMessage('Venue finded failed!');
        setSnackbarSeverity('error');
        setSnackbarOpen(true);
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
    setInstructor('');
    setVenues([]);
    setCurrentPage(1);
    setSnackbarMessage('Reset successfully!');
    setSnackbarSeverity('success');
    setSnackbarOpen(true);
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
  const currentItems = venues.slice(indexOfFirstItem, indexOfLastItem);
  const totalPages = Math.ceil(venues.length / itemsPerPage);

  
  
  return (
    <SidebarLayout>
    <Container maxWidth="lg" sx={{ mt: 4, mb: 4 }}>
      <Box display="flex" alignItems="center" mb={3}>
        <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
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
        <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
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
        <FormControl variant="outlined" sx={{ minWidth: 200, mr: 2 }}>
          <InputLabel>Instructor</InputLabel>
          <Select
            value={instructor}
            onChange={(e) => setInstructor(e.target.value)}
            label="Instructor"
          >
            <MenuItem value=""><em>Please Select</em></MenuItem>
            <MenuItem value="1">1</MenuItem>
  
          </Select>

        </FormControl>
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
              <TableCell sx={{ fontSize: '0.75rem', padding: '25px', whiteSpace: 'nowrap' }}>Address</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>TimeZone</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>State</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Instructor</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '20px', whiteSpace: 'nowrap' }}>City</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>CancellationPolicy</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>PaymentMode</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>NonrefundableFee</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>FobKey</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>Deposit</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>MembershipFee</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>UsageFee</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>RefundableStatus</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>BookMethod</TableCell>
              <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>RegistrationLink</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {currentItems.map((venue) => (
              <TableRow key={venue.id}>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.id}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.address}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.timeZone}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.state}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.instructor}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.city}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.cancellationPolicy}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.paymentMode}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.nonrefundableFee}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.fobKey}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.deposit}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.membershipFee}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.usageFee}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.refundableStatus}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.bookMethod}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>{venue.registrationLink}</TableCell>
                <TableCell sx={{ fontSize: '0.75rem', padding: '4px', whiteSpace: 'nowrap' }}>
                <Button variant="outlined" onClick={() => handleClickOpen(venue)}>Edit</Button>
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
        <DialogTitle>{isEditMode?"Edit Venue":"Add a Venue"}</DialogTitle>
        <DialogContent>
          <DialogContentText>
            {isEditMode?"Edit the information of the venue.":"Add the information of the venue"}
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
          <Button onClick={isEditMode?handleSave:handleInsert}>{isEditMode ? "Save Changes" : "Add Venue"}</Button>
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

export default Venue;