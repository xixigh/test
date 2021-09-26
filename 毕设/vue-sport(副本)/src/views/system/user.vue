<template>
    <div>
        <!-- 卡片视图区 -->
        <el-card>
            <el-row :gutter="25">
                <el-col :span="10">
                    <!-- 搜索添加 -->
                    <el-input placeholder="请输入搜索内容" v-model="queryInfo.queryString" clearable @clear="findPage">
                        <el-button slot="append" icon="el-icon-search" @click="findPage"/>
                    </el-input>
                </el-col>
                <el-col :span="2">
                    <el-button type="primary" @click="insertDialog">添加用户</el-button>
                </el-col>
            </el-row>
            <!-- 用户列表 -->
            <el-table :data="userList" border stripe>
                <el-table-column type="index" label="序号"/>
                <el-table-column label="用户昵称" prop="nickName"/>
                <el-table-column label="登录名" prop="username"/>
                <el-table-column label="性别" prop="sex" :formatter="sexFormat"/>
                <el-table-column label="邮箱" prop="email"/>
                <el-table-column label="管理员" prop="state">
                    <!-- scope.row 就是当前行的信息 -->
                    <template slot-scope="scope">
                        <el-switch v-model="scope.row.admin" @change="adminChanged($event,scope.row)" @click.stop.native/>
                    </template>
                </el-table-column>
                <el-table-column label="状态" prop="state">
                    <!-- scope.row 就是当前行的信息 -->
                    <template slot-scope="scope">
                        <el-switch v-model="scope.row.state" @change="stateChanged(scope.row)"/>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <el-tooltip class="item" effect="dark" content="重置密码(123456)" placement="top">
                            <!-- 重置密码 -->
                            <el-button type="info" icon="el-icon-unlock" size="mini" @click="resetPassword(scope.row)"/>
                        </el-tooltip>
                        <!-- 修改 -->
                        <el-button type="primary" icon="el-icon-edit" size="mini" @click="showDialog(scope.row)"/>
                    </template>
                </el-table-column>
            </el-table>
            <!-- 分页 -->
            <el-pagination
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
                :current-page="queryInfo.pageNumber"
                :page-sizes="[5, 10, 20, 50]"
                :page-size="queryInfo.pageSize"
                layout="total, sizes, prev, pager, next, jumper"
                :total="total"/>
        </el-card>

        <!-- 创建新用户对话框 -->
        <el-dialog :title="title" :visible.sync="dialogVisible" width="40%" @close="dialogClosed">
            <!-- 内容主体区域 -->
            <el-form :model="dataForm" :rules="rules" ref="form" label-width="70px">
                <!-- 用户名 -->
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="dataForm.username">
                        <i slot="prefix" class="el-icon-user"/>
                    </el-input>
                </el-form-item>
                <!-- 密码 -->
                <el-form-item label="密码" prop="password" v-if="dataForm.id === null || dataForm.id === undefined">
                    <el-input v-model="dataForm.password">
                        <i slot="prefix" class="el-icon-lock"/>
                    </el-input>
                </el-form-item>
                <!-- 邮箱 -->
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="dataForm.email">
                        <i slot="prefix" class="el-icon-message"/>
                    </el-input>
                </el-form-item>
                <!-- 角色 -->
                <el-form-item label="角色" prop="roles">
                    <el-select v-model="roles" style="width: 100%" multiple placeholder="请选择">
                        <el-option
                            v-for="item in roleList"
                            :key="item.id"
                            :label="item.remark"
                            :value="item.id">
                        </el-option>
                    </el-select>
                </el-form-item>
            </el-form>
            <!-- 内容底部区域 -->
            <span slot="footer" class="dialog-footer">
                <el-button @click="clickCancel">取 消</el-button>
                <el-button type="primary" @click="clickOk">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
export default {
    name: 'SysUser',
    data() {
        return {
            // 请求参数(查询参数，页码，每页大小)
            queryInfo: {
                queryString: "",
                pageNumber: 1,
                pageSize: 5
            },
            userList: [],// 用户列表
            total: 0, // 最大数据记录
            dialogVisible: false ,// 对话框显示
            // 添加用户表单项
            dataForm: {},
            title: '',
            //角色列表
            roles: [],
            // 验证规则
            rules:{
                username:[
                    { required: true, message: "请输入用户名", trigger: "blur" },
                    { min: 3, max: 8, message: "长度在 5 到 8 个字符", trigger: "blur" },
                    { validator: this.checkData, trigger: ['blur', 'change'] }
                ],
                password:[
                    { required: true, message: "请输入密码", trigger: "blur" },
                    { min: 6, max: 8, message: "长度在 6 到 8 个字符", trigger: "blur" }
                ],
                email:[
                    { required: true, message: "请输入邮箱", trigger: "blur" },
                    { type: 'email', message: "请输入正确邮箱地址", trigger: ['blur', 'change'] }
                ],
                roles: [
                    {
                        validator: (rule, value, callback) => {
                            if (this.roles.length < 1) {
                                callback(new Error('请选择角色'));
                            } else {
                                callback();
                            }
                        },
                        trigger: "change"
                    },
                ]
            },
            //角色列表
            roleList: [],
        }
    },
    //加载的钩子函数
    created() {
        this.findPage();
        console.log(JSON.parse(sessionStorage.getItem('user')));
        this.$ajax.get('/user/findRole').then((res) => {
            this.roleList = res.data;
        });
    },
    methods: {
        async findPage() {
            // 调用post请求
            this.$ajax.post('/user/findPage', this.queryInfo).then((res) => {
                console.log('查询', res);
                if (!res.data.flag) return this.$message.error(res.data.message);
                this.userList = res.data.data.rows;// 将返回数据赋值
                this.total = res.data.data.total;// 总个数
            });
        },
        // 监听pageSize改变的事件
        handleSizeChange(newSize) {
            this.queryInfo.pageSize = newSize;
            this.findPage(); // 数据发生改变重新申请数据
        },
        // 监听pageNum改变的事件
        handleCurrentChange(newNumber) {
            this.queryInfo.pageNumber = newNumber;
            this.findPage(); // 数据发生改变重新申请数据
        },
        // 监听添加用户
        dialogClosed(){
            this.dataForm = {};// 重置表单项
            this.roles = [];
        },
        //添加显示
        insertDialog() {
            this.dataForm = {};
            this.$nextTick(() => {
                this.$refs.form.clearValidate();
            });
            this.roles = [];
            this.dialogVisible = true;
        },
        // 展示修改框
        showDialog(row){
            this.dataForm = {};
            this.roles = [];
            this.$nextTick(() => {
                this.$refs.form.clearValidate();
            });
            //打开对话框
            this.dialogVisible = true;
            console.log(row);
            //将作用域插槽中的值赋值到对话框中
            this.dataForm = row;
            row.roles.forEach(item => {
                this.roles.push(item.id);
            });
        },
        //取消
        clickCancel() {
            this.dataForm = {};// 重置表单项
            this.roles = [];
            this.dialogVisible = false;
        },
        // 确认
        clickOk() {
            this.$refs.form.validate((valid) => {
                if (!valid) return this.$message.error('信息验证未通过！');
                this.dataForm.roles = [];
                this.roles.forEach(item => {
                    this.dataForm.roles.push({id: item});
                });
                this.dataForm.authorities = null;
                this.dataForm.premission = null;
                if (this.dataForm.id === null || this.dataForm.id === undefined) {
                    //发送ajax请求
                    this.$ajax.post('/user/add', this.dataForm).then((res) => {
                        if (!res.data.flag) return this.$message.error(res.data.message);
                        this.$message.success(res.data.message);
                    }).finally(() => {
                        this.dialogVisible = false;
                        this.findPage();
                    });
                } else {
                    //发送ajax请求
                    this.$ajax.post('/user/edit', this.dataForm).then((res) => {
                        if (!res.data.flag) return this.$message.error(res.data.message);
                        this.$message.success(res.data.message);
                    }).finally(() => {
                        this.dialogVisible = false;
                        this.findPage();
                    });
                }
            });
        },
        // 重置密码
        resetPassword(row){
            // 弹框
            this.$confirm('此操作将重置该用户的密码为123456, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //获取管理员编号
                const adminId = JSON.parse(sessionStorage.getItem('user')).id;
                this.$ajax.post('/user/resetPassword', { adminId, userId: row.id }).then((res) => {
                    if (!res.data.flag) {
                        row.state = false;
                        if (res.data.data === 403) {
                            this.$message.error(res.data.message);
                            this.$router.push({ path: '/login' });
                        }
                        return this.$message.error(res.data.message);
                    }
                    this.$message.success(res.data.message);
                });
            }).catch(() => {
                // 成功删除为confirm 取消为 cancel
                return this.$message.info("操作已取消");
            });
        },
        //自定义表单验证
        checkData(rule, value, callback) {
            if (value) {
                if (/[\u4E00-\u9FA5]/g.test(value)) {
                    callback(new Error('用户名不能存在中文！'));
                } else {
                    callback();
                }
            }
            callback();
        },
        sexFormat(row) {
            let sex = row.sex;
            if (sex === 1) {
                return '男';
            } else if (sex === 2) {
                return '女';
            } else {
                return '未知';
            }
        }
    }
}
</script>

<style lang="less" scoped>

</style>