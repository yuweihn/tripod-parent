import hasPermObj from './permiss';


export default function directive(app) {
    app.config.globalProperties.hasPerm = hasPermObj.hasPerm;
    app.directive('hasPerm', hasPermObj);
}
