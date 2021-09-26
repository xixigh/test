import Vue from 'vue';
//引入axios
import axios from 'axios';
//引入element ui message
import {Message} from 'element-ui';
//配置请求拦截器
axios.interceptors.request.use((config) => {
    let token = sessionStorage.getItem('token');
    //如果存在token，则将携带token
    if (token) {
        config.headers['Authorization'] = token;
    }
    return config;
}, (error) => {
    console.log(error);
});

//响应
axios.interceptors.response.use((res) => {
    if (res.data.message === '您尚未登录，请登录！') {
        sessionStorage.clear();
    }
    return res;
}, (err) => {
    if (err.response.status === 400) {
        Message.warning(err.response.data.message);
    } else if (err.response.status === 405) {
        Message.warning(err.response.data.message);
    } else if (err.response.status === 403) {
        Message.warning('权限不足，请联系管理员！');
    } else if (err.response.status === 401) {
        sessionStorage.clear();
        Message.warning('未登录，请刷新后登录再操作！');
    } else if (err.response.status === 500) {
        if (err.response.statusText === 'Internal Server Error') {
            Message.error('后端未启动！');
        }
        if(err.response.data.message === 'Network Error') {
            Message.warning('网络错误，请稍后尝试！');
        } else if (err.response.data.message === '您尚未登录，请登录！') {
            console.log('您尚未登录，请登录！');
        } else {
            Message.warning(err.response.data.message);
        }
    } else {
        Message.warning('未知错误......');
    }
});
Vue.prototype.$ajax = axios;
//配置axios访问根路径
//axios.defaults.baseURL = 'http://localhost:9000/vue';
//允许携带cookie
// axios.defaults.withCredentials = true;
// //配置拦截器
// const req = axios.create();
// req.interceptors.request.use((config) => {
//     config.headers.authorization = localStorage.getItem('token');
//     return config;
// });

// req.interceptors.response.use((res) => {
//     return res;
// }, (err) => {
//     if (err.response.status === 401) {

//     }
// });
//让Vue挂载axios