<template>
    <div>
        <!-- 菜单管理 -->
        <el-card>
            <el-row :gutter="25">
                <el-col :span="10">
                    <!-- 搜索添加 -->
                    <el-input placeholder="请输入搜索内容" v-model="queryInfo.queryString" clearable @clear="findPage">
                        <el-button slot="append" icon="el-icon-search" @click="findPage"/>
                    </el-input>
                </el-col>
                <el-col :span="2">
                    <el-button type="primary" @click="addShow">添加菜单</el-button>
                </el-col>
            </el-row>
            <!-- 用户列表 -->
            <el-table :data="tableList" border stripe row-key="id" :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
                <el-table-column label="菜单标题" prop="title"/>
                <el-table-column label="后端路径" prop="url"/>
                <el-table-column label="前端路径" prop="path"/>
                <el-table-column label="组件名称" prop="component"/>
                <el-table-column label="菜单图标" prop="icon"/>
                <el-table-column label="操作">
                    <template slot-scope="scope">
                        <!-- 修改 -->
                        <el-button type="warning" v-show="scope.row.children != null" icon="el-icon-circle-plus-outline" size="mini" @click="addDialog(scope.row)"/>
                        <el-button type="primary" icon="el-icon-edit" size="mini" @click="editDialog(scope.row)"/>
                        <el-button type="danger" icon="el-icon-delete" size="mini" @click="deleteById(scope.row)"/>
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
                <el-form-item v-if="dataForm.parentId" label="父级菜单" prop="ptitle">
                    <el-checkbox left checked disabled>{{parentName}}</el-checkbox>
                </el-form-item>
                <el-form-item label="菜单标题" prop="title">
                    <el-input v-model="dataForm.title"/>
                </el-form-item>
                <el-form-item label="后端路径" prop="url">
                    <el-input 
                        v-model="dataForm.url"
                        @keyup.native="dataForm.url=dataForm.url.replace(/[^a-zA-Z/*]/g,'')"/>
                </el-form-item>
                <el-form-item label="前端路径" prop="path">
                    <el-input 
                        v-model="dataForm.path"
                        @keyup.native="dataForm.path=dataForm.path.replace(/[^a-zA-Z/*]/g,'')"/>
                </el-form-item>
                <el-form-item label="组件名称" prop="component">
                    <el-input 
                        v-model="dataForm.component"
                        @keyup.native="dataForm.component=dataForm.component.replace(/[^a-zA-Z]/g,'')"/>
                </el-form-item>
                <el-form-item label="菜单图标" prop="icon">
                    <el-select v-model="dataForm.icon" clearable filterable :popper-append-to-body="false" placeholder="请选择菜单图标">
                        <el-option
                            v-for="(item,index) in icons"
                            :key="index"
                            :value="item.value"
                            style="font-size: 18px"
                            :class="item.value">
                            <span class="option-lable">{{item.value}}</span>
                        </el-option>
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
//图标列表
import iconList from '../../utils/iconList.json';
export default {
    name: 'SysMenu',
    data() {
        return {
            queryInfo: {
                queryString: '',
                pageNumber: 1,
                pageSize: 5
            },
            //表格数据
            tableList: [],
            //总记录数
            total: 0,
            //图标
            icons: iconList,
            //对话框状态
            open: false,
            //对话框标题
            title: '',
            //父标题
            parentName: '',
            //表单
            dataForm: {},
            //表单加载
            loading: false,
            //表单校验
            rulesDataForm: {
                // remark: [
                //     { required: true, message: "请输入角色名称", trigger: "blur" },
                // ],
                // name: [
                //     { required: true, message: "请输入角色标识，角色标识由字母+下划线组成", trigger: "blur" },
                // ]
            },

        }
    },
    created() {
        this.findPage();
        this.icons = iconList.icon;
    },
    methods: {
        async findPage() {
            this.$ajax.post('/system/menu/findPage', this.queryInfo).then((res) => {
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
            this.title = '添加菜单';
            this.dataForm = {};
            this.open = true;
        },
        //添加子菜单
        addDialog(row) {
            console.log(row);
            this.title = '添加子菜单';
            if (row && row.path !== '/home') return this.$message.warning('系统目前只支持二级菜单哟！');
            this.dataForm.parentId = row.id;
            this.parentName = row.title;
            this.open = true;
        },
        //修改
        editDialog(row) {
            this.title = '修改菜单';
            this.dataForm = row;
            this.open = true;
        },
        //删除
        deleteById(row) {
            if (row.children && row.children.length > 0) return this.$message.warning('请删除其下的子菜单后删除！'); 
            this.$confirm('此操作将永久删除该菜单信息, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                //delete请求遵循restful风格
                this.$ajax.delete(`/system/menu/delete/${row.id}`).then((res) => {
                    if (!res.data.flag) return this.$message.error(res.data.message);
                    this.$message.success(res.data.message);
                    this.findPage();
                });
            }).catch(() => {
                // 成功删除为confirm 取消为 cancel
                return this.$message.info("操作已取消");
            });
        },
        dialogClosed() {
            this.$refs.dataForm.resetFields();
        },
        //取消
        clickCancel() {
            this.$refs.dataForm.resetFields();
            this.open = false;
        },
        //确定
        clickOk() {
            this.$refs.dataForm.validate((valid) => {
                if (!valid) return this.$message.error('表单校验失败，请仔细填写后提交！');
                //判断是添加还是修改
                if (this.dataForm.id === null || this.dataForm.id === undefined) {
                    this.$ajax.post('/system/menu/insert', this.dataForm).then((res) => {
                        if (!res.data.flag) return this.$message.error(res.data.message);
                        this.$message.success(res.data.message);
                        this.open = false;
                        this.findPage();
                    });
                } else {
                    this.$ajax.post('/system/menu/update', this.dataForm).then((res) => {
                        if (!res.data.flag) return this.$message.error(res.data.message);
                        this.$message.success(res.data.message);
                        this.open = false;
                        this.findPage();
                    });
                }
            });
        },
    }
}
</script>

<style lang="less" scoped>
.option-lable {
    margin-left: 5px;
}

.el-select {
    width: 100%;
}
</style>