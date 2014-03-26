/**
 * User: Hongfei Zhou
 * Date: 13-12-25
 * JS Description:
 */

var bear = angular.module('jumpingbear', ["ngRoute"]);

var secApi = {name: "API Testing", url: "/service/api"};
var secDashboard = {name: "Dash Board", url: "/test/dashboard"};
var secUI = {name: "UI Testing", url: "/test/ui"};
var secDocs = {name: "Documentation", url: "/test/docs"};

function SectionController(allSections, sectionBtns) {
    return function ($scope, $location) {
        $scope.menuShow = false;
        $scope.currentSection = allSections[0].name;
        $scope.menuItems = allSections;
        $scope.buttons = sectionBtns;
        $scope.clickNav = function (index) {
            $scope.selectBtn = index;
        };


        console.log($location.path());
        if ($location.path() == '/test/api')
            $scope.selectBtn = 0;
        else if ($location.path() == '/test/api/suite')
            $scope.selectBtn = 1;
        else
            $scope.selectBtn = 0;
    }
}
