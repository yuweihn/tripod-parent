import hasPermObj from './permission/has.perm';


export default function directive(app) {
    app.config.globalProperties.hasPerm = hasPermObj.hasPerm;
    app.directive('hasPerm', hasPermObj);
}
