<template>
    <div>
        <!-- 权限管理页 -->
        <el-card>
            <el-row :gutter="25">
                <el-col :span="10">
                    <!-- 搜索添加 -->
                    <el-input placeholder="请输入搜索内容" v-model="queryInfo.queryString" clearable @clear="findPage">
                        <el-button slot="append" icon="el-icon-search" @click="findPage"></el-button>
                    </el-input>
                </el-col>
                <el-col :span="2">
                    <el-button type="primary" @click="addShow">添加权限</el-button>
                </el-col>
            </el-row>
            <!-- 用户列表 -->
            <el-table :data="tableList" border stripe>
                <el-table-column type="index" label="序号"/>
                <el-table-column label="权限名称" prop="name"/>
                <el-table-column label="权限标识" prop="premission"/>
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
                :total="total"></el-pagination>
        </el-card>

        <!-- 对话框 -->
        <el-dialog :title="title" v-loading="loading" :visible.sync="open" width="50%" @colse="dialogClosed">
            <el-form :model="dataForm" :rules="rulesDataForm" ref="dataForm" label-width="100px">
                <!-- 名称 -->
                <el-form-item label="权限名称" prop="name">
                    <el-input v-model="dataForm.name"/>
                </el-form-item>
                <!-- 用户名 -->
                <el-form-item label="权限标识" prop="premission">
                    <el-input 
                        v-model="dataForm.premission"
                        @keyup.native="dataForm.premission=dataForm.premission.replace(/[^a-zA-Z_]/g,'')"/>
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
    name: 'Syspremission',
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
            //权限列表
            premissions: [],
            //选定权限
            selectPremission: [],
            //表单加载
            loading: false,
            //表单校验
            rulesDataForm: {
                // remark: [
                //     { required: true, message: "请输入权限名称", trigger: "blur" },
                // ],
                // name: [
                //     { required: true, message: "请输入权限标识，权限标识由字母+下划线组成", trigger: "blur" },
                // ]
            },

        }
    },
    created() {
        this.findPage();
    },
    methods: {
        handleClick(tab) {
            console.log(tab.index);
        },
        async findPage() {
            this.$ajax.post('/system/premission/findPage', this.queryInfo).then((res) => {
                console.log(res);
                if (!res.data.flag) return this.$message.error(res.data.message);
                this.tableList = res.data.data.rows;
                this.total = res.data.data.total;
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
            this.title = '添加权限';
            this.dataForm = {};
            this.open = true;
        },
        //打开编辑对话框
        editDialog(row) {
            this.title = '编辑权限';
            this.dataForm = row;
            this.open = true;
        },
        //对话框关闭
        dialogClosed() {
            //重置表单
            this.$refs.dataForm.resetFields();
        },
        //点击取消
        clickCancel() {
            this.dataForm = {};
            this.open = false;
        },
        //点击确定
        clickOk() {
            //表单校验
            this.$refs.dataForm.validate((valid) => {
                if (!valid) return this.$message.error('表单校验不通过，请认真填写后提交！');
                this.loading = true;
                //判断是修改还是添加
                if (this.dataForm.id === null || this.dataForm.id === undefined) {
                    //添加
                    this.$ajax.post('/system/premission/insert', this.dataForm).then((res) => {
                        if (!res.data.flag) {
                            setTimeout(() => {
                                this.loading = false;
                            }, 6000);
                            return this.$message.error(res.data.message);
                        }
                        this.open = false;
                        this.loading = false;
                        this.$message.success(res.data.message);
                        this.findPage();
                    });
                } else {
                    //修改
                    this.$ajax.post('/system/premission/update', this.dataForm).then((res) => {
                        if (!res.data.flag) {
                            setTimeout(() => {
                                this.loading = false;
                            }, 6000);
                            return this.$message.error(res.data.message);
                        }
                        this.open = false;
                        this.loading = false;
                        this.$message.success(res.data.message);
                        this.findPage();
                    });
                }
            });
        },
        //删除权限
        deleteById(id) {
            this.$confirm('此操作将永久删除该权限信息, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //delete请求遵循restful风格
                this.$ajax.delete(`/system/premission/delete/${id}`).then((res) => {
                    if (!res.data.flag) return this.$message.error(res.data.message);
                    this.$message.success(res.data.message);
                    this.findPage();
                });
            }).catch(() => {
                // 成功删除为confirm 取消为 cancel
                return this.$message.info("操作已取消");
            });
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