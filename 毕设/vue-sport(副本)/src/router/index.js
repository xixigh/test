import Vue from 'vue';
import VueRouter from 'vue-router';
import store from '@/store';
import axios from "axios";

import { GenerateRoutes } from '@/utils/menus';

Vue.use(VueRouter);

export const constantRoutes = [
    {
        path: '/login',
        name: 'login',
        component: () => import('@/views/Login.vue'),
        hidden: true
    },
    {
        path: '/home',
        name: 'home',
        component: () => import('@/views/Home.vue'),
    },
];

const router = new VueRouter({
    routes: constantRoutes,
    mode: 'hash' //用history 生产环境配合nginx
});

function formatMneus(menus) {
    let arr = [];
    menus.forEach(item => {
        if (item.children !== null) {
            item.children.forEach(i => {
                i.component = loadView(i.component);
                arr.push(i);
            });
        }
    });
    return arr;
}

export const loadView = (view) => {
    return (resolve) => require([`@/views/${view}.vue`], resolve);
}

/**
 * 挂载路由导航守卫
 * to:将要访问的路径
 * from:从哪里访问的路径
 * next:之后要做的任务，是一个函数
 * next（）放行， next（'/URL'）强制跳转的路径。
 */
router.beforeEach((to, from, next) => {
    // 获取token
    const token = sessionStorage.getItem('token');
    if (token) {
        if (to.path === '/login') {
            next('/home');
        } else {
            if (!store.state.roles || store.state.roles.length < 1) {
                //获取用户基本信息
                axios.get('/user/getLoginInfo').then((res) => {
                    const user = res.data.data;
                    console.log(user);
                    //权限信息
                    if (user.authorities.length > 0) {
                        let roles = [];
                        user.authorities.forEach(item => {
                            roles.push(item.authority);
                        });
                        //添加角色信息
                        store.commit('setRoles', roles);
                        let premission = [];
                        user.premission.forEach(item => {
                            premission.push(item.authority);
                        });
                        //添加权限标识
                        store.commit('setPremission', premission);
                    }
                    //添加用户基本信息
                    store.commit('setUserName', user.nickName);
                    store.commit('setAvatar', user.avatarUrl);
                    //发送获取菜单信息请求
                    axios.get('/system/menu/findMenu').then((res) => {
                        //将菜单信息转换成 路由信息
                        store.commit('setRoutes', res.data);
                        const menus = formatMneus(res.data);
                        router.addRoutes([
                            {
                                path: '/home',
                                name: 'home',
                                component: () => import('@/views/Home.vue'),
                                children: menus
                            },
                        ]);
                    });
                    next();
                }).catch((err) => {
                    next('/home');
                });
            } else {
                next();
            }
        }
    } else {
        if (to.path === '/login') {
            next();
        } else {
            next(`/login?redirect=${to.fullPath}`);
        }
    }
});



export default router
