<template>
    <el-container class="home-container">
    <!--头部布局-->
    <el-header>
      <div>
        <el-avatar :src="avatar" />
        <span class="admin-name">运动管理平台—— 管理员({{ userName }})</span>
      </div>
      <el-button icon="el-icon-menu" type="info" size="mini" class="open-menu" @click="toggleCollapase" circle/>
      <el-button icon="el-icon-switch-button" type="warning" size="mini" class="phone-logout" @click="logout" circle/>
      <el-button type="info" class="logout" @click="logout">安全退出</el-button>
    </el-header>
    <el-container>
      <!--侧边布局-->
      <el-aside :width="isCollapse ?'64px':'200px'">
        <!--伸缩按钮-->
        <div class="toggle-button" @click="toggleCollapase"><i class="el-icon-s-fold"></i></div>
        <!--侧边栏菜单区 unique-opened="true" 只保持一个菜单展开 router开启路由 active-text-color 颜色-->
        <el-menu 
            background-color="#545c64" 
            text-color="#fff" 
            active-text-color="#409eff" 
            unique-opened 
            :collapse="isCollapse" 
            :collapse-transition="false"  
            :router="true" 
            :default-active="activePath">
            <el-submenu  :index="index + ''" v-for="(item, index) in menus" :key="index">
                <template slot="title">
                    <i :class="item.icon"/>
                    <span>{{item.title}}</span>
                </template>
                <el-menu-item  
                    :index="it.path" 
                    v-for="(it, indexj) in item.children" 
                    :key="indexj"  
                    @click="saveNavState(it.path)">
                    <template slot="title">
                        <i :class="it.icon"/>
                        <span>{{it.title}}</span>
                    </template>
                </el-menu-item>
            </el-submenu>
        </el-menu>
      </el-aside>
      <!--主体布局-->
      <el-main>
          <!-- 面包屑导航 -->
          <el-breadcrumb separator-class="el-icon-arrow-right" v-if="this.$router.currentRoute.path !== '/home'">
              <el-breadcrumb-item :to="{ path: '/home' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item>{{this.$router.currentRoute.title}}</el-breadcrumb-item>
          </el-breadcrumb>
          <div class="welcome" v-if="this.$router.currentRoute.path === '/home'">
              欢迎来到健康管理平台！
          </div>
          <!--路由占位符-->
          <router-view/>
      </el-main>
    </el-container>
  </el-container>
</template>

<script>
import axios from "axios";
import { mapState } from 'vuex';

export default {
    name: 'Home',
    //计算属性
    computed: {
        ...mapState(['menus', 'avatar', 'userName', 'roles'])
    },
    data() {
        return {
            //menuList: [],
            isCollapse: false,
            // 被激活的连接
            activePath:'',
        }
    },
    //页面加载时执行
    created() {
        //this.menuList = this.menus;
    },
    methods: {
        logout() {
            this.$confirm('您的操作将退出此系统, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$ajax.get('/user/logout').then((res) => {
                    this.$message.success(res.data.message);
                    //清空本地缓存
                    sessionStorage.clear();
                    //跳转页面
                    this.$router.replace({ path: '/login' });
                    //刷新vuex中的数据
                    window.location.reload();
                });
            }).catch(() => {
                this.$message.info('操作已取消!');
            });
        },
        toggleCollapase() {
            this.isCollapse = !this.isCollapse;
        },
        // 保存二级菜单的路径
        saveNavState(activePath){
            window.sessionStorage.setItem('activePath',activePath);// 存放点击的二级菜单
            this.activePath = activePath;// 给点击的菜单添加高亮
        },
    }
}
</script>

<style lang="less" scoped>
.el-header {
    background-color: #373d41;
    display: flex;
    justify-content: space-between;// 左右贴边
    padding-left: 0%;// 左边界
    align-items: center;// 水平
    color: #fff;
    font-size: 20px;
    > div { //左侧div加布局
        display: flex;
        align-items: center;
        span {
            margin-left: 15px;
        }
    }
}

.open-menu {
    display: none;
}
.phone-logout {
    display: none;
}

.admin-name {
    font-size: 30px;
    font-family: 华文行楷;
}

@media screen and (max-width: 500px) {
    .logout {
        display: none !important;
    }
    .el-header {
        font-size: 1px !important;
    }
    .admin-name {
        display: none;
    }
    .open-menu {
        display: block !important;
    }
    .phone-logout {
        display: block !important;
    }
    .toggle-button { 
        display: none !important;
    }
}
.el-aside {
    background-color: #333744;
    .el-menu{
        border-right: none;// 对其右边框
    }
}
.el-main {
    background-color: #eaedf1;
}
.home-container {
    height: 100%;
}
.logo_img {
    height: 75px !important;
    width: 70px !important;;
}
.iconfont{
    margin-right: 10px;
}
.toggle-button{
    background-color:#4A5064;
    font-size: 10px;
    line-height: 24px;
    color:#fff;
    text-align: center;
    letter-spacing: 0.2em;
    cursor: pointer;// 显示鼠标指针为：小手
}
.el-menu :hover{
    color: yellow;
}

.el-icon-s-fold {
    margin-top: 2px;
    font-size: 20px;
}

.welcome {
    font-family: 华文行楷;
    font-size: 50px;
    margin: 100px 150px auto;
    color:chocolate;
}
</style>