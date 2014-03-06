/**
 * User: Hongfei Zhou
 * Date: 14-1-6
 * JS Description:
 */

var suiteOp = function ($scope, SUITE) {
    return {
        name: 'Create New Suite',
        op: function () {
            $scope.switchView('CreateSuite');
            $scope.newSuite = {
                name: "",
                tests: []
            };
            $scope.button = {
                submit: function (index) {
                    SUITE.save($scope.newSuite).success(function (data) {
                        $scope.newSuite.id = data.id;
                        $scope.suites.unshift($scope.newSuite);
                        $scope.suiteMetas.unshift({suiteModified: true});
                        $scope.switchView('SuiteList');
                    });
                },
                discard: function (index) {
                    $scope.switchView('SuiteList');
                }
            };
            $scope.$watch('newSuite.name', function () {
                $scope.button.disabled = typeof $scope.newSuite.name == 'undefined' || $scope.newSuite.name.length == 0;
            });
        }
    }
};


bearAPI.controller('SuiteController', ['$scope', 'SUITE', 'API', function ($scope, SUITE, API) {
    $scope.currView = 'loading';
    $scope.suiteView = {
        currentHover: -1,
        currHeight: 0,
        editIndex: -1
    };
    $scope.suiteToTest = {};
    $scope.currentSuite = {};
    $scope.newSuite = {};
    $scope.suites = [];
    $scope.suiteMetas = [];
    $scope.op1 = suiteOp($scope, SUITE);

    $scope.switchView = function (view) {
        $scope.currView = view;
    };
    $scope.switchOp = function (op) {
        $scope.op1 = op;
    };
    $scope.addGlobal = function (name, obj) {
        $scope[name] = obj
    };

    SUITE.query().success(function (suites) {
        $scope.suites = suites;
        $scope.suiteMetas = Array.apply(null, new Array(suites.length)).map(function (a) {
            return {};
        });
        $scope.switchView('SuiteList');
    });


    API.queryBriefMap().success(function (data) {
        $scope.briefAPIsMap = data;
    });

    $scope.button = {
        submit: function (index) {
            $scope.suiteView.editIndex = -1;
        },

        discard: function (index) {
            $scope.suiteView.editIndex = -1;
        }
    };

    $scope.switchButton = function (btn) {
        $scope.button = btn;
    };


    $scope.toAllSuite = function () {
        $scope.suiteView.editIndex = -1;
        $scope.switchView('SuiteList');
        $scope.op1 = suiteOp($scope, SUITE);
    };

    $scope.updateSuite = function (index) {
        if ($scope.suiteView.editIndex == index && $scope.suiteView.gapContent == 'editSuite') {
            $scope.suiteView.editIndex = -1;
        } else {
            $scope.currentSuite = $scope.suites[index];
            $scope.suiteView.currHeight = '120px';
            $scope.suiteView.gapContent = 'editSuite';
            $scope.button = {
                submit: function (index) {
                    SUITE.update($scope.currentSuite).success(function () {
                        $scope.suiteMetas[index].suiteModified = true;
                        $scope.suiteView.editIndex = -1;
                    });
                },
                discard: function (index) {
                    $scope.suiteView.editIndex = -1;
                }
            };
            $scope.suiteView.editIndex = index;
        }
    };

    $scope.deleteSuite = function (index) {
        if ($scope.suiteView.editIndex == index && $scope.suiteView.gapContent == 'deleteSuite') {
            $scope.suiteView.editIndex = -1;
        } else {
            $scope.currentSuite = $scope.suites[index];
            $scope.suiteView.currHeight = '110px';
            $scope.suiteView.gapContent = 'deleteSuite';
            $scope.button = {
                submit: function (index) {
                    SUITE.del($scope.currentSuite.id).success(function () {
                        $scope.suites.splice(index, 1);
                        $scope.suiteMetas.splice(index, 1);
                        $scope.suiteView.editIndex = -1;
                    });
                },
                discard: function (index) {
                    $scope.suiteView.editIndex = -1;
                }
            };
            $scope.suiteView.editIndex = index;
        }
    };

    $scope.goToSuite = function (index) {
        $scope.currentSuite = $scope.suites[index];
        $scope.suiteView.editIndex = -1;
        $scope.suiteToTest.currentSuite = $scope.currentSuite;
        $scope.suiteToTest.selectedTestIndex = -1;
        $scope.switchView('TestList');
    };

    $scope.goToMenuSuite = function (suite) {
        $scope.currentSuite = suite;
        $scope.suiteView.editIndex = -1;
        $scope.suiteToTest.currentSuite = $scope.currentSuite;
        $scope.switchView('TestList');
        $scope.suiteToTest.selectedTestIndex = -1;
    };

    $scope.goToMenuTest = function (index) {
        $scope.addGlobal('testToRequest', {currentTest: $scope.suiteToTest.tests[index]});
        $scope.suiteToTest.selectedTestIndex = index;
        $scope.switchView('RequestList');
    };
}]);