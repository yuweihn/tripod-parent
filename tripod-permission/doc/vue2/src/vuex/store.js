import Vue from 'vue';
import Vuex from 'vuex';
import dynamicRoutes from '@/dynamic.routes';
import dynamicButtons from '@/dynamic.buttons';


Vue.use(Vuex);

const getters = {
  routes: state0 => state0.dynamicRoutes.routes,
  isDynamicMenuLoaded: state0 => state0.dynamicRoutes.isDynamicMenuLoaded,
  buttons: state0 => state0.dynamicButtons.buttons,
  isDynamicButtonLoaded: state0 => state0.dynamicButtons.isDynamicButtonLoaded
}

const store = new Vuex.Store({
  modules: {
    dynamicRoutes,
    dynamicButtons
  },
  getters
});

export default store;
