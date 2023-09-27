<template>
<el-dialog title="信息" :visible.sync="formVisible" width="750px" :close-on-click-modal="true"
			 append-to-body v-drag>
	<div><pre style="white-space: pre-wrap;">{{content}}</pre></div>
	<div slot="footer" class="dialog-footer">
		<el-button @click.native="formVisible = false" class="el-icon-close"> 关闭</el-button>
	</div>
</el-dialog>
</template>

<script>
export default {
    data() {
        return {
            formVisible: false,
            content: null
        }
    },
    methods: {
        show: function(content) {
          this.content = content;
            this.formVisible = true;
        }
    },
    mounted() {

    }
}
</script>

<style scoped>

</style>

