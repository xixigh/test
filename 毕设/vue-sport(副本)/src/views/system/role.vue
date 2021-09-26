<template>
    <div>
        <!-- 角色管理页 -->
        <el-card>
            <el-row :gutter="25">
                <el-col :span="10">
                    <!-- 搜索添加 -->
                    <el-input placeholder="请输入搜索内容" v-model="queryInfo.queryString" clearable @clear="findPage">
                        <el-button slot="append" icon="el-icon-search" @click="findPage"/>
                    </el-input>
                </el-col>
                <el-col :span="2">
                    <el-button type="primary" @click="addShow">添加角色</el-button>
                </el-col>
            </el-row>
            <!-- 用户列表 -->
            <el-table :data="tableList" border stripe>
                <el-table-column type="index" label="序号"/>
                <el-table-column label="角色名称" prop="remark"/>
                <el-table-column label="角色标识" prop="name"/>
                <el-table-column label="角色权限" width="400">
                    <template slot-scope="scope">
                        <div slot="reference" class="name-wrapper">
                            <el-tag
                                class="premission-btn"
                                :key="item.id"
                                v-for="item in scope.row.premissions"
                                size="medium">{{ item.name }}
                            </el-tag>
                        </div>
                    </template>
                </el-table-column>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <!-- 修改 -->
                        <el-button type="primary" icon="el-icon-edit" size="mini" @click="editDialog(scope.row)"/>
                        <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteById(scope.row.id)"/>
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

        <!-- 对话框 -->
        <el-dialog :title="title" v-loading="loading" :visible.sync="open" width="50%" @colse="dialogClosed">
            <el-form :model="dataForm" :rules="rulesDataForm" ref="dataForm" label-width="100px">
                <!-- 名称 -->
                <el-form-item label="角色名称" prop="remark">
                    <el-input v-model="dataForm.remark"/>
                </el-form-item>
                <!-- 用户名 -->
                <el-form-item label="角色标识" prop="name">
                    <el-input
                        v-model="dataForm.name"
                        @keyup.native="dataForm.name=dataForm.name.replace(/[^a-zA-Z_]/g,'')"/>
                </el-form-item>
                <!-- 菜单 -->
                <el-form-item label="菜单权限" prop="menu">
                    <el-select v-model="menuStatus" placeholder="请选择菜单" multiple @change="selectChange">
                        <el-option :value="mineStatusValue" style="height: auto">
                            <el-tree
                                :data="data"
                                show-checkbox
                                node-key="id"
                                ref="tree"
                                check-strictly
                                highlight-current
                                :props="defaultProps"
                                @check-change="handleCheckChange"/>
                        </el-option>
                    </el-select>
                </el-form-item>
                <!-- 邮箱 -->
                <el-form-item label="角色权限" prop="premissions">
                    <el-select v-model="selectPremission" multiple placeholder="请选择用户权限">
                        <el-option
                            v-for="(item,index) in premissions"
                            :key="index"
                            :label="item.name"
                            :value="item.id"/>
                    </el-select>
                </el-form-item>
            </el-form>
            <span slot="footer" class="dialog-footer">
                <el-button @click="clickCancel">取 消</el-button>
                <el-button type="primary" @click="clickOk">确 定</el-button>
            </span>
        </el-dialog>
    </div>
</template>

<script>
    export default {
        name: 'SysRole',
        data() {
            return {
                queryInfo: {
                    queryString: '',
                    pageNumber: 1,
                    pageSize: 5
                },
                //选项卡
                activeName: 'first',
                //表格数据
                tableList: [],
                //总记录数
                total: 0,
                //对话框状态
                open: false,
                //对话框标题
                title: '',
                //表单
                dataForm: {},
                //菜单列表
                data: [],
                //权限列表
                premissions: [],
                //选定权限
                selectPremission: [],
                //表单加载
                loading: false,
                mineStatusValue: [],
                menuStatus: [],
                defaultProps: {
                    children: "children",
                    label: "title"
                },
                //表单校验
                rulesDataForm: {
                    remark: [
                        {required: true, message: "请输入角色名称", trigger: "blur"},
                    ],
                    name: [
                        {required: true, message: "请输入角色标识，角色标识由字母+下划线组成", trigger: "blur"},
                    ]
                },

            }
        },
        created() {
            this.findPage();
            //查询权限列表
            this.findPremissions();
            this.findMenus();
        },
        methods: {
            handleClick(tab) {
                console.log(tab.index);
            },
            async findPage() {
                this.$ajax.post('/system/role/findPage', this.queryInfo).then((res) => {
                    if (!res.data.flag) return this.$message.error(res.data.message);
                    this.tableList = res.data.data.rows;
                    this.total = res.data.data.total;
                });
            },
            //查询权限列表
            findPremissions() {
                this.$ajax.get('/system/role/findPremissions').then((res) => {
                    if (!res.data.flag) return this.$message.error(res.data.message);
                    this.premissions = res.data.data;
                });
            },
            findMenus() {
                this.$ajax.get('/system/menu/findMenu').then((res) => {
                    this.data = res.data;
                });
            },
            //页数变化查询
            handleCurrentChange(newPageNumber) {
                this.queryInfo.pageNumber = newPageNumber;
                this.findPage();
            },
            //页码变化查询
            handleSizeChange(newPageSize) {
                this.queryInfo.pageSize = newPageSize;
                this.findPage();
            },
            //添加对话框
            addShow() {
                this.title = '添加角色';
                this.dataForm = {};
                this.selectPremission = [];
                this.menuStatus = [];
                this.$refs.tree.setCheckedKeys([]);
                this.open = true;
            },
            //打开编辑对话框
            editDialog(row) {
                this.dataForm = {};
                this.selectPremission = [];
                this.menuStatus = [];
                this.title = '编辑角色';
                this.dataForm = row;
                let temp = [];
                let check = [];
                //菜单回显
                row.menus.forEach(item => {
                    temp.push(item.title);
                    check.push(item.id);
                    if (item.children && item.children.length > 0) {
                        item.children.forEach(i => {
                            temp.push(i.title);
                            check.push(i.id);
                        });
                    }
                });
                this.$nextTick(() => {
                    this.$refs.tree.setCheckedKeys(check);
                });
                this.menuStatus = temp;
                for (let i = 0; i < row.premissions.length; i++) {
                    this.selectPremission[i] = row.premissions[i].id;
                }
                this.open = true;
            },
            //对话框关闭
            dialogClosed() {
                this.selectPremission = [];
                this.menuStatus = [];
                this.$refs.tree.setCheckedKeys([]);
                //重置表单
                this.$refs.dataForm.resetFields();
            },
            //点击取消
            clickCancel() {
                this.dataForm = {};
                this.selectPremission = [];
                this.menuStatus = [];
                this.$refs.tree.setCheckedKeys([]);
                this.open = false;
            },
            //点击确定
            clickOk() {
                //给表单权限赋值
                this.dataForm.premissions = [];
                for (let i = 0; i < this.selectPremission.length; i++) {
                    let tempId = {id: null};
                    tempId.id = this.selectPremission[i];
                    this.dataForm.premissions.push(tempId);
                }
                //表单校验
                this.$refs.dataForm.validate((valid) => {
                    if (!valid) return this.$message.error('表单校验不同过，请认真填写后提交！');
                    this.loading = true;
                    //判断是修改还是添加
                    if (this.dataForm.id === null || this.dataForm.id === undefined) {
                        //添加
                        this.$ajax.post('/system/role/insert', this.dataForm).then((res) => {
                            if (!res.data.flag) {
                                setTimeout(() => {
                                    this.loading = false;
                                }, 6000);
                                return this.$message.error(res.data.message);
                            }
                            this.open = false;
                            this.loading = false;
                            this.findPage();
                        });
                    } else {
                        //修改
                        this.$ajax.post('/system/role/update', this.dataForm).then((res) => {
                            if (!res.data.flag) {
                                setTimeout(() => {
                                    this.loading = false;
                                }, 6000);
                                return this.$message.error(res.data.message);
                            }
                            this.open = false;
                            this.loading = false;
                            this.findPage();
                        });
                    }
                    //将信息回复，避免key重复报错
                    this.dataForm.premissions = [];
                    //清空选定权限项
                    this.selectPremission = [];
                });
            },
            //删除角色
            deleteById(id) {
                this.$confirm('此操作将永久删除该角色信息, 是否继续?', '提示', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    //delete请求遵循restful风格
                    this.$ajax.delete(`/system/role/delete/${id}`).then((res) => {
                        if (!res.data.flag) return this.$message.error(res.data.message);
                        this.$message.success(res.data.message);
                        this.findPage();
                    });
                }).catch(() => {
                    // 成功删除为confirm 取消为 cancel
                    return this.$message.info("操作已取消");
                });
            },
            //选择菜单
            selectChange(e) {
                let arrNew = [];
                let dataLength = this.mineStatusValue.length;
                let eleng = e.length;
                for (let i = 0; i < dataLength; i++) {
                    for (let j = 0; j < eleng; j++) {
                        if (e[j] === this.mineStatusValue[i].label) {
                            arrNew.push(this.mineStatusValue[i])
                        }
                    }
                }
                //设置勾选的值
                this.$refs.tree.setCheckedNodes(arrNew);
            },
            //点击下拉树之后的
            handleCheckChange() {
                //1. 第一个true代表 是否只是叶子节点  第二个true代表 是否包含半选节点（就是使得选择的时候不包含父节点）
                let res = this.$refs.tree.getCheckedNodes();
                let arrLabel = [];
                let arrId = [];
                let arr = [];
                res.forEach(item => {
                    arrLabel.push(item.title);
                    arrId.push(item.id);
                    arr.push(item);
                });
                this.mineStatusValue = arr;
                this.menuStatus = arrLabel;
                this.dataForm.menus = [];
                for (let i = 0; i < arrId.length; i++) {
                    let tempId = {id: null};
                    tempId.id = arrId[i];
                    this.dataForm.menus.push(tempId);
                }
            }
        }
    }
</script>

<style lang="less" scoped>
    .premission-btn {
        margin-top: 5px;
        margin-left: 5px;
    }

    .el-select {
        width: 100%;
    }
</style>