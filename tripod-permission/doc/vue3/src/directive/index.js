import hasPerm from './permission/has.perm';


export default function directive(app) {
    app.directive('hasPerm', hasPerm);
}
