import axios from 'axios';

// 创建一个axios实例
const axiosInstance = axios.create({
   baseURL: 'http://54.175.129.180:8080', // 后端API的基础URL
  //baseURL: 'http://localhost:8080',
});

// 添加请求拦截器
axiosInstance.interceptors.request.use(
  config => {
    // 从localStorage中获取令牌
    const token = localStorage.getItem('token');
    if (token) {
      // 在请求头中添加令牌
      config.headers['Authorization'] = token;
      //console.log("nihao"+token);
    }
    return config;
  },
  error => {
    return Promise.reject(error);
  }
);

export default axiosInstance;