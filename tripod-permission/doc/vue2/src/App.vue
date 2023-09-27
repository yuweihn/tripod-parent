<template>
	<div id="app">
		<transition name="fade" mode="out-in">
			<router-view></router-view>
		</transition>
	</div>
</template>

<script>
	export default {
		name: 'app',
		components: {

		}
	}
</script>

<style lang="scss">
body {
	margin: 0px;
	padding: 0px;
	/*background: url(assets/bg1.jpg) center !important;
		background-size: cover;*/
	// background: #1F2D3D;
	font-family: Helvetica Neue, Helvetica, PingFang SC, Hiragino Sans GB, Microsoft YaHei, SimSun, sans-serif;
	font-size: 14px;
	-webkit-font-smoothing: antialiased;
}

#app {
	position: absolute;
	top: 0px;
	bottom: 0px;
	width: 100%;
}

.el-submenu [class^=fa] {
	vertical-align: baseline;
	margin-right: 10px;
}

.el-menu-item [class^=fa] {
	vertical-align: baseline;
	margin-right: 10px;
}

.toolbar {
	background: #f2f2f2;
	padding: 10px;
	//border:1px solid #dfe6ec;
	margin: 10px 0px;
	.el-form-item {
		margin-bottom: 10px;
	}
}
.toolbar2 {
	background: #f2f2f2;
	padding: 10px;
	margin: 10px 0 30px 0;
}

.fade-enter-active, .fade-leave-active {
	transition: all .2s ease;
}

.fade-enter, .fade-leave-active {
	opacity: 0;
}

.el-upload__tip {
  line-height: 10px;
}

.el-dialog .el-form {
  padding-right: 10px;
}
.dialog-footer {
  padding-right: 10px;
}
</style>
