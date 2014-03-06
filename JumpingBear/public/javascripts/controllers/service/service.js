/**
 * User: Hongfei Zhou
 * Date: 13-12-25
 * JS Description:
 */


/**
 * User: Hongfei Zhou
 * Date: 13-12-25
 * JS Description:
 */

var bearAPI = angular.module('api', ['ngRoute', 'ngResource']);

var viewSelectors = function (index) {

};

var secAPIController = SectionController(
    [secApi, secDashboard, secUI, secDocs],
    [
        {name: 'API', url: '/service/api'},
        {name: 'Suite', url: '/service/suite'},
        {name: 'Request', url: '/service/request'}
    ]
);

bearAPI.controller('SectionController', secAPIController);

bearAPI.config(['$routeProvider', '$locationProvider', function ($routeProvider, $locationProvider) {
    $locationProvider.html5Mode(true);
    $routeProvider.
        when('/service/api', {
            controller: 'ListAPIController',
            templateUrl: '/template/service/api'
        }).
        when('/service/suite', {
            controller: 'SuiteController',
            templateUrl: '/template/service/suite'
        });

}]);



