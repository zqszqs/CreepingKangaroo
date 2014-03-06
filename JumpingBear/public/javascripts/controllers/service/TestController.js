/**
 * User: Hongfei Zhou
 * Date: 14-1-7
 * JS Description:
 */

bearAPI.controller('TestController', ['$scope', 'TEST', 'EXECUTOR', 'STATUS', 'API', function ($scope, TEST, EXECUTOR, STATUS, API) {
    $scope.testView = {
        editIndex: -1,
        view: 'TestList'
    };
    $scope.currentTest = {};
    $scope.tests = [];
    $scope.testMetas = [];
    $scope.testToRequest = {};
    $scope.op1 = $scope.switchOp({
        name: 'Create New Test',
        op: function () {
            $scope.newTest = {};
            $scope.testView.view = 'CreateTest';
            $scope.button.submit = function (index) {
                $scope.newTest.suiteId = $scope.suiteToTest.currentSuite.id;
                $scope.newTest.forAPI = $scope.newTest.forAPISelect.id;
                TEST.save($scope.newTest).success(function (data) {
                    $scope.newTest.id = data.id;
                    $scope.tests.unshift($scope.newTest);
                    $scope.testMetas.unshift({testModified: true});
                    $scope.testView.view = 'TestList';
                });
            };

            $scope.button.discard = function (index) {
                $scope.testView.view = 'TestList';
            };

            $scope.$watch('newTest.name + newTest.forAPISelect.name', function () {
                $scope.button.disabled =
                    (typeof $scope.newTest.name == 'undefined' || $scope.newTest.name.length == 0) ||
                        (typeof $scope.newTest.forAPISelect == 'undefined');
            });
        }
    });

    var testStatusUpdater = function (message) {
        var needsUpdateAgain = false;
        var statusObj = JSON.parse(message.data);
        for (var i = 0; i < $scope.tests.length; ++i) {
            if ($scope.tests[i].id == statusObj.testId) {
                switch (statusObj.status) {
                    case "SUCCESS":
                    case "FAILURE":
                    case "SKIPPED":
                    case "NONE":
                        $scope.testMetas[i].executePng = "execute.png";
                        break;
                    case "EXECUTING":
                        $scope.testMetas[i].executePng = "executing.gif";
                        needsUpdateAgain = true;
                        break;
                    case "PENDING":
                        $scope.testMetas[i] = "waiting.gif";
                        needsUpdateAgain = true;
                        break;
                }
            }
        }
        if (needsUpdateAgain)
            setTimeout(function () {
                STATUS.use(testStatusUpdater);
                STATUS.ofTest(statusObj.testId)
            }, 2000);
    };

    TEST.getBySuite($scope.suiteToTest.currentSuite.id).success(function (data) {
            $scope.testMetas = Array.apply(null, new Array(data.length)).map(function (a) {
                return {};
            });
            $scope.tests = data;

            STATUS.use(testStatusUpdater);

            for (var i = 0; i < data.length; ++i) {
                STATUS.ofTest(data[i].id);
            }

            $scope.suiteToTest.tests = $scope.tests;
        }
    );

    API.queryBrief().success(function (data) {
        $scope.briefAPIs = data;
    });

    $scope.goToTest = function (index) {
        $scope.addGlobal('testToRequest', {currentTest: $scope.tests[index]});
        $scope.suiteToTest.selectedTestIndex = index;
        $scope.testView.editIndex = -1;
        $scope.switchView('RequestList');
    };

    $scope.executeTest = function (index) {
        var test = $scope.tests[index];
        $scope.testMetas[index].executePng = "executing.gif";
        EXECUTOR.executeTest(test.id);
        STATUS.ofTest(test.id);
    };

    $scope.updateTest = function (index) {
        if ($scope.testView.editIndex == index && $scope.testView.gapContent == 'editTest') {
            $scope.testView.editIndex = -1;
        } else {
            $scope.currentTest = $scope.tests[index];
            $scope.testView.currHeight = '170px';
            $scope.testView.gapContent = 'editTest';
            $scope.button = {
                submit: function (index) {
                    TEST.update($scope.currentTest).success(function () {
                        $scope.testMetas[index].testModified = true;
                        $scope.testView.editIndex = -1;
                    });
                },
                discard: function (index) {
                    $scope.testView.editIndex = -1;
                }
            };
            $scope.testView.editIndex = index;
        }
    };

    $scope.getExecuteImg = function (index) {
        if ($scope.testMetas[index].executePng)
            return $scope.testMetas[index].executePng;
        else
            return "execute.png";
    };

    $scope.deleteTest = function (index) {
        if ($scope.testView.editIndex == index && $scope.testView.gapContent == 'deleteTest') {
            $scope.testView.editIndex = -1;
        } else {
            $scope.currentTest = $scope.tests[index];
            $scope.testView.currHeight = '110px';
            $scope.testView.gapContent = 'deleteTest';
            $scope.button = {
                submit: function (index) {
                    TEST.del($scope.currentTest.id).success(function () {
                        $scope.tests.splice(index, 1);
                        $scope.testMetas.splice(index, 1);
                        $scope.testView.editIndex = -1;
                    });
                },
                discard: function (index) {
                    $scope.testView.editIndex = -1;
                }
            };
            $scope.testView.editIndex = index;
        }
    };
}]);