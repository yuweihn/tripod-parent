import {ElMessage} from 'element-plus';
import request from '@/assets/js/request';


const dynamicButtonStore = defineStore(
    'dynamicButton',
    {
        state: () => ({
            buttons: [],
            isDynamicButtonLoaded: false
        }),
        actions: {
            generateButtons() {
                return new Promise((resolve, reject) => {
                    request.get('/sys/admin/permission/button/list').then(res => {
                        ElMessage({type: "success", message: res.data.msg});
                        this.buttons = res.data.data;
                        this.isDynamicButtonLoaded = true;
                        resolve(res);
                    }).catch(error => {
                        this.buttons = [];
                        this.isDynamicButtonLoaded = true;
                        ElMessage({type: 'error', message: error.message});
                        reject(error);
                    })
                });
            },
            resetDynamicButtonLoaded() {
                this.isDynamicButtonLoaded = false;
            }
        }
    }
)

export default dynamicButtonStore;
