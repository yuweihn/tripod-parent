import dynamicButtonStore from '@/dynamic.buttons';


export default {
    mounted(el, binding, vnode) {
        const val = binding.value;
        setTimeout(() => {
            const hasPermissions = hasPerm(val);
            if (!hasPermissions) {
                el.parentNode && el.parentNode.removeChild(el);
            }
        }, 0);
    },
    hasPerm: hasPerm
}

function hasPerm(val) {
    if (!val) {
        throw new Error('请设置权限标识');
    }
    const permissions = dynamicButtonStore().buttons;
    if (!permissions || !(permissions instanceof Array) || permissions.length <= 0) {
        return false;
    }
    const hasPermissions = permissions.some(permission => {
        return val instanceof Array && val.length > 0 ? val.includes(permission) : val === permission;
    });
    return hasPermissions;
}
