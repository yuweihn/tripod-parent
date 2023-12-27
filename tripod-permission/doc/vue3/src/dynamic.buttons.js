import modal from '@/assets/js/modal';
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
                        modal.msgSuccess(res.data.msg);
                        this.buttons = res.data.data;
                        this.isDynamicButtonLoaded = true;
                        resolve(res);
                    }).catch(error => {
                        this.buttons = [];
                        this.isDynamicButtonLoaded = true;
                        modal.msgError(error.message);
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
