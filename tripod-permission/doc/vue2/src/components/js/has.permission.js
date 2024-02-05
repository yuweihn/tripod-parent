/**
 * 1、在main.js中导入：import './components/js/has.permission';
 * 2、给组件增加v-hasPerm指令。如：<el-input title="..." width="..." v-hasPerm="['user.delete']">
 **/

// v-hasPerm: 是否具有某权限

import Vue from 'vue';
import store from '@/vuex/store';


Vue.directive('hasPerm', {
  inserted(el, binding, vnode) {
    const val = binding.value;
    setTimeout(() => {
      const hasPermissions = hasPerm(val);
      if (!hasPermissions) {
        el.parentNode && el.parentNode.removeChild(el);
      }
    }, 0);
  }
});

function hasPerm(val) {
  if (!val) {
    throw new Error('请设置权限标识');
  }
  const permissions = store.getters && store.getters.buttons;
  if (!permissions || !(permissions instanceof Array) || permissions.length <= 0) {
    return false;
  }
  const hasPermissions = permissions.some(permission => {
    return val instanceof Array && val.length > 0 ? val.includes(permission) : val === permission;
  });
  return hasPermissions;
}

Vue.prototype.hasPerm = hasPerm;
