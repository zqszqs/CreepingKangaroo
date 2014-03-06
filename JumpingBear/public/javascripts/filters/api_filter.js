/**
 * User: Hongfei Zhou
 * Date: 13-12-27
 * JS Description:
 */

bearAPI.filter('zoomToZero', function () {
   return function (input) {
       if (typeof input == 'undefined')
            return 0;
       else return input;
   }
});

bearAPI.filter('hideLong', function () {
    return function (input, length) {
        if (input.length <= length) return input;
        else return input.substring(0, length - 6) + "..." + input.substring(input.length - 3)
    }
});