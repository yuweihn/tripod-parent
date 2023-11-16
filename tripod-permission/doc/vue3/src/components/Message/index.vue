<template>
<el-dialog title="信息" v-model="formVisible" width="650px" :close-on-click-modal="true" append-to-body draggable>
	<div><pre style="white-space: pre-wrap;">{{content}}</pre></div>
	<div slot="footer" class="dialog-footer" style="margin-top: 50px;">
		<el-button @click.native="formVisible = false" :icon="Close"> 关闭</el-button>
	</div>
</el-dialog>
</template>

<script setup>
import {Close} from '@element-plus/icons-vue';
const formVisible = ref(false);
const content = ref(null);

function show(ctt) {
    content.value = ctt;
    formVisible.value = true;
}
defineExpose({
    show
})
</script>

<style scoped>

</style>

