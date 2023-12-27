<template>
    <div class="app-container home">
        <el-divider />
        <el-row :gutter="20">
            <el-col :xs="24" :sm="24" :md="12" :lg="8">
                <el-card class="update-log">
                    <template v-slot:header>
                        <div class="clearfix">
                            <span>联系信息</span>
                        </div>
                    </template>
                    <div class="body">
                        <p><i class="el-icon-s-promotion"></i> 官网：<el-link :href="homeUrl">{{homeUrl}}</el-link></p>
                        <p><i class="el-icon-chat-dot-round"></i> 24小时热线：<a href="javascript:;">400-100-1730</a></p>
                    </div>
                </el-card>
            </el-col>
            <el-col :xs="24" :sm="24" :md="12" :lg="8">
                <el-card class="update-log">
                    <template v-slot:header>
                        <div class="clearfix">
                            <span>更新日志</span>
                        </div>
                    </template>
                    <el-collapse accordion>
                        <el-collapse-item title="v1.0.2 - 2021-03-30">
                            <ol>
                                <li>升级fastjson到最新版</li>
                                <li>升级element-ui</li>
                                <li>修复页面切换时布局错乱的问题</li>
                                <li>优化文件下载出现的异常</li>
                            </ol>
                        </el-collapse-item>
                        <el-collapse-item title="v1.0.1 - 2020-08-01">
                            <ol>
                                <li>升级core-js到最新版本</li>
                                <li>升级oshi到最新版本</li>
                                <li>升级fastjson到最新版</li>
                            </ol>
                        </el-collapse-item>
                        <el-collapse-item title="v1.0.0 - 2020-05-15">
                            <ol>
                                <li>初始化</li>
                            </ol>
                        </el-collapse-item>
                    </el-collapse>
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup name="home">
const homeUrl = window.location.protocol + "//" + window.location.host + window.location.pathname;
</script>

<style scoped lang="scss">
.body .el-link {
    vertical-align: inherit;
}
</style>

