<!-- @author haha -->
<template>
  <div class="icon-body">
    <div class="icon-c">
      <el-input v-model="name" style="position: relative;" clearable placeholder="请输入图标名称" @clear="reset"
                  @input.native="filterIcons">
        <i slot="suffix" class="el-icon-search el-input__icon" />
      </el-input>
    </div>
    <div class="icon-c icon-list">
      <div v-for="(item, index) in iconList" :key="index" @click="onIconClick(item)">
        <svg-icon :icon-class="item" style="height: 30px; width: 16px;" />
        <span>{{item}}</span>
      </div>
    </div>
  </div>
</template>

<script>
import icons from './requireIcons';

export default {
  name: 'IconSelect',
  data() {
    return {
      name: '',
      iconList: icons
    }
  },
  methods: {
    reset() {
      this.name = '';
      this.iconList = icons;
    },
    filterIcons() {
      this.iconList = icons;
      if (this.name) {
        this.iconList = this.iconList.filter(item => item.includes(this.name));
      }
    },
    onIconClick(name) {
      this.$emit('selected', name);
      document.body.click();
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  .icon-body {
    width: 100%;
    .icon-c {
      margin: 10px;
    }
    .icon-list {
      height: 200px;
      overflow-y: scroll;
      div {
        height: 30px;
        line-height: 30px;
        margin-bottom: -5px;
        cursor: pointer;
        width: 33%;
        float: left;
      }
      span {
        display: inline-block;
        vertical-align: -0.15em;
        fill: currentColor;
        overflow: hidden;
      }
    }
  }
</style>
