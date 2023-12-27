import {Message} from 'element-ui';
import axios from 'axios';
import global from '@/components/js/global';


const dynamicButtons = {
    state: {
        buttons: [],
        isDynamicButtonLoaded: false
    },
    mutations: {
        SET_BUTTONS: (state, buttons) => {
            state.buttons = buttons;
            state.isDynamicButtonLoaded = true;
        },
        REMOVE_DYNAMIC_LOADED: (state) => {
            state.isDynamicButtonLoaded = false;
        }
    },
    actions: {
        GenerateButtons({commit}) {
            return new Promise(resolve => {
                // 向后端请求权限按钮数据
                axios.get(global.baseUrl + '/sys/admin/permission/button/list', {}).then((res) => {
                    if (res.data.code === '0000') {
                        Message({type: "success", message: res.data.msg});
                        var buttons = res.data.data;
                        postButtons(commit, resolve, buttons);
                    } else {
                        Message.error(res.data.msg);
                        postButtons(commit, resolve);
                    }
                }).catch((err) => {
                    Message.error(err.message);
                    postButtons(commit, resolve);
                });
            });
        },
        RemoveDynamicButtonLoaded({commit}) {
            return new Promise(resolve => {
                commit('REMOVE_DYNAMIC_LOADED');
                resolve();
            });
        }
    }
}
function postButtons(commit, resolve, buttons) {
    buttons = buttons == null ? [] : buttons;
    commit('SET_BUTTONS', buttons);
    resolve(buttons);
}

export default dynamicButtons;
